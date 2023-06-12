package me.wesley1808.servercore.common.activation_range;

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import me.wesley1808.servercore.common.config.tables.ActivationRangeConfig;
import me.wesley1808.servercore.common.interfaces.activation_range.LevelInfo;
import me.wesley1808.servercore.common.utils.Util;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ambient.AmbientCreature;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * Based on: Paper & Spigot (Entity-Activation-Range.patch)
 * <p>
 * Patch Author: Aikar (aikar@aikar.co)
 * <br>
 * License: GPL-3.0 (licenses/GPL.md)
 */
public class ActivationRange {
    private static final ReferenceOpenHashSet<EntityType<?>> EXCLUDED_ENTITY_TYPES = new ReferenceOpenHashSet<>();
    private static final Predicate<Goal> BEE_GOAL_IMMUNITIES = goal -> goal instanceof Bee.BeeGoToKnownFlowerGoal || goal instanceof Bee.BeeGoToHiveGoal;
    private static final Activity[] VILLAGER_PANIC_IMMUNITIES = {
            Activity.HIDE,
            Activity.PRE_RAID,
            Activity.RAID,
            Activity.PANIC
    };

    public static void reload() {
        EXCLUDED_ENTITY_TYPES.clear();

        for (String type : ActivationRangeConfig.EXCLUDED_ENTITY_TYPES.get()) {
            Optional<EntityType<?>> optional = EntityType.byString(type);
            optional.ifPresent(EXCLUDED_ENTITY_TYPES::add);
        }
    }

    /**
     * Puts entities in their corresponding groups / activation types, upon initialization.
     */
    public static ActivationType initializeEntityActivationType(Entity entity) {
        if (entity instanceof Raider) {
            return ActivationType.RAIDER;
        } else if (entity instanceof WaterAnimal) {
            return ActivationType.WATER;
        } else if (entity instanceof Villager) {
            return ActivationType.VILLAGER;
        } else if (entity.getType() == EntityType.ZOMBIE || entity.getType() == EntityType.HUSK) {
            return ActivationType.ZOMBIE;
        } else if (entity instanceof Creeper || entity instanceof Slime || entity instanceof Hoglin) {
            return ActivationType.MONSTER_BELOW;
        } else if (entity instanceof FlyingMob) {
            return ActivationType.FLYING;
        } else if (entity instanceof Enemy) {
            return ActivationType.MONSTER;
        } else if (entity instanceof AgeableMob || entity instanceof AmbientCreature) {
            return ActivationType.ANIMAL;
        } else if (entity instanceof PathfinderMob) {
            return ActivationType.NEUTRAL;
        } else {
            return ActivationType.MISC;
        }
    }

    /**
     * Checks for entity exclusions.
     * Any entities on this list will always tick, regardless of activation range settings.
     *
     * @return Boolean: whether the entity will be excluded from activation range checks.
     */
    public static boolean isExcluded(Entity entity) {
        return entity.getActivationType().activationRange.getAsInt() <= 0
               || entity.getActivationType().tickInterval.getAsInt() == 0
               || entity instanceof Player
               || entity instanceof ThrowableItemProjectile
               || entity instanceof EnderDragon
               || entity instanceof EnderDragonPart
               || entity instanceof WitherBoss
               || entity instanceof Fireball
               || entity instanceof LightningBolt
               || entity instanceof PrimedTnt
               || entity instanceof EndCrystal
               || entity instanceof FireworkRocketEntity
               || entity instanceof EyeOfEnder
               || entity instanceof ThrownTrident
               || EXCLUDED_ENTITY_TYPES.contains(entity.getType());
    }

    /**
     * Activates entities in {@param level} that are close enough to players.
     */
    public static void activateEntities(ServerLevel level) {
        int currentTick = level.getServer().getTickCount();
        int maxRange = Integer.MIN_VALUE;
        for (ActivationType type : ActivationType.values()) {
            maxRange = Math.max(type.activationRange.getAsInt(), maxRange);
        }

        LevelInfo info = (LevelInfo) level;
        for (ActivationType.Wakeup wakeup : ActivationType.Wakeup.values()) {
            info.setRemaining(wakeup, Math.min(info.getRemaining(wakeup) + 1, wakeup.max.getAsInt()));
        }

        maxRange = Math.min((level.getServer().getPlayerList().getViewDistance() << 4) - 8, maxRange);
        for (ServerPlayer player : level.players()) {
            if (player.isSpectator()) {
                continue;
            }

            AABB maxBB;
            if (ActivationRangeConfig.USE_VERTICAL_RANGE.get()) {
                maxBB = player.getBoundingBox().inflate(maxRange, 128, maxRange);
                for (ActivationType type : ActivationType.values()) {
                    type.boundingBox = player.getBoundingBox().inflate(type.activationRange.getAsInt());

                    if (type.extraHeightUp) type.boundingBox = type.boundingBox.expandTowards(0, 96, 0);
                    if (type.extraHeightDown) type.boundingBox = type.boundingBox.expandTowards(0, -96, 0);
                }
            } else {
                maxBB = player.getBoundingBox().inflate(maxRange, 256, maxRange);
                for (ActivationType type : ActivationType.values()) {
                    final int range = type.activationRange.getAsInt();
                    type.boundingBox = player.getBoundingBox().inflate(range, 256, range);
                }
            }

            for (Entity entity : level.getEntities(player, maxBB)) {
                activateEntity(entity, currentTick);
            }
        }
    }

    private static void activateEntity(Entity entity, int currentTick) {
        if (currentTick > entity.getActivatedTick()) {
            if (entity.isExcluded() || entity.getActivationType().boundingBox.intersects(entity.getBoundingBox())) {
                entity.setActivatedTick(currentTick + 19);
            }
        }
    }

    /**
     * Checks for immunities on inactive entities.
     *
     * @param entity: The entity to check immunities for
     * @return Integer: the amount of ticks an entity should be immune for activation range checks.
     */
    public static int checkEntityImmunities(Entity entity, int currentTick) {
        final int inactiveWakeUpImmunity = checkInactiveWakeup(entity, currentTick);
        if (inactiveWakeUpImmunity > -1) {
            return inactiveWakeUpImmunity;
        }

        if (entity.getRemainingFireTicks() > 0) {
            return 2;
        }

        if (entity.getActivatedImmunityTick() >= currentTick) {
            return 1;
        }

        if (!entity.isAlive()) {
            return 40;
        }

        // quick checks.
        final ActivationType type = entity.getActivationType();
        if (entity.isInWater() && entity.isPushedByFluid() && !(type == ActivationType.ANIMAL || type == ActivationType.FLYING || type == ActivationType.VILLAGER || type == ActivationType.WATER || entity instanceof Boat)) {
            return 100;
        }

        // ServerCore - Immunize moving items & xp orbs.
        if (entity instanceof ItemEntity || entity instanceof ExperienceOrb) {
            final Vec3 movement = entity.getDeltaMovement();
            if (movement.x != 0 || movement.z != 0 || movement.y > 0) {
                return 20;
            }
        }

        if (!(entity instanceof AbstractArrow projectile)) {
            if (!entity.onGround() && !entity.isInWater() && !(entity instanceof FlyingMob || entity instanceof Bat)) {
                return 10;
            }
        } else if (!projectile.inGround) {
            return 1;
        }

        // special cases.
        if (entity instanceof LivingEntity living) {
            if (living.jumping || !living.getActiveEffects().isEmpty() || living.onClimbable()) {
                return 1;
            }

            if (entity instanceof Mob mob && mob.getTarget() != null) {
                return 20;
            }

            if (entity instanceof Bee bee && (bee.beePollinateGoal.isPollinating() || bee.isAngry() || Util.hasTasks(bee.getGoalSelector(), BEE_GOAL_IMMUNITIES))) {
                return 20;
            }

            if (entity instanceof Villager villager) {
                Brain<Villager> brain = villager.getBrain();

                if (ActivationRangeConfig.VILLAGER_TICK_PANIC.get()) {
                    for (Activity activity : VILLAGER_PANIC_IMMUNITIES) {
                        if (brain.isActive(activity)) {
                            return 20 * 5;
                        }
                    }
                }

                final int immunityAfter = ActivationRangeConfig.VILLAGER_WORK_IMMUNITY_AFTER.get();
                if (immunityAfter > 0 && (currentTick - entity.getActivatedTick()) >= immunityAfter) {
                    if (brain.isActive(Activity.WORK)) {
                        return ActivationRangeConfig.VILLAGER_WORK_IMMUNITY_FOR.get();
                    }
                }
            }

            if (entity instanceof Llama llama && llama.inCaravan()) {
                return 1;
            }

            if (entity instanceof Animal animal) {
                if (animal.isBaby() || animal.isInLove()) {
                    return 5;
                }

                if (entity instanceof Sheep sheep && sheep.isSheared()) {
                    return 1;
                }
            }

            if (entity instanceof Creeper creeper && creeper.isIgnited()) {
                return 20;
            }

            if (entity instanceof Mob mob && Util.hasTasks(mob.targetSelector)) {
                return 0;
            }
        }
        return -1;
    }

    /**
     * Checks if an entity is active. If not, check for immunities once per second.
     *
     * @param entity: The ticking entity
     * @return Boolean: whether the entity should tick.
     */
    public static boolean checkIfActive(Entity entity, int currentTick) {
        if (shouldTick(entity)) return true;

        boolean active = entity.getActivatedTick() >= currentTick;
        if (!active) {
            final int tickInterval = entity.getActivationType().tickInterval.getAsInt();

            if ((currentTick - entity.getActivatedTick() - 1) % tickInterval == 0) {
                // Check immunities every 20 inactive ticks.
                final int immunity = checkEntityImmunities(entity, currentTick);
                if (immunity >= 0) {
                    entity.setActivatedTick(currentTick + immunity);
                    return true;
                }

                return tickInterval > 0;
            }
            // Spigot - Add a little performance juice to active entities. Skip 1/4 if not immune.
        } else if (ActivationRangeConfig.SKIP_NON_IMMUNE.get() && entity.getFullTickCount() % 4 == 0 && checkEntityImmunities(entity, currentTick) < 0) {
            return false;
        }

        return active;
    }

    private static boolean shouldTick(Entity entity) {
        return !ActivationRangeConfig.ENABLED.get() || entity.isExcluded() || entity.isInsidePortal || entity.isOnPortalCooldown()
               || (entity.tickCount < 200 && entity.getActivationType() == ActivationType.MISC) // New misc entities
               || (entity instanceof Mob mob && mob.leashHolder instanceof Player) // Player leashed mobs
               || (entity instanceof LivingEntity living && living.hurtTime > 0); // Attacked mobs
    }

    private static int checkInactiveWakeup(Entity entity, int currentTick) {
        ActivationType.Wakeup wakeup = entity.getActivationType().wakeup;
        if (wakeup != null && currentTick - entity.getActivatedTick() >= wakeup.interval.getAsInt() * 20L) {
            LevelInfo info = (LevelInfo) entity.level();
            int remaining = info.getRemaining(wakeup);
            if (remaining > 0) {
                info.setRemaining(wakeup, remaining - 1);
                return 100;
            }
        }

        return -1;
    }
}