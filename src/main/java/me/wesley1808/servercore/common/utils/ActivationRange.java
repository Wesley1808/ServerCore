package me.wesley1808.servercore.common.utils;

import me.wesley1808.servercore.common.ServerCore;
import me.wesley1808.servercore.common.config.tables.ActivationRangeConfig;
import me.wesley1808.servercore.common.interfaces.activation_range.IGoalSelector;
import me.wesley1808.servercore.common.interfaces.activation_range.ILevel;
import me.wesley1808.servercore.common.interfaces.activation_range.IPathFinderMob;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
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
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.MinecartHopper;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;

/**
 * Based on: Paper & Spigot (Entity-Activation-Range.patch)
 * Patch Author: Aikar (aikar@aikar.co)
 * License: GPL-3.0 (licenses/GPL.md)
 */

public final class ActivationRange {
    private static final Activity[] VILLAGER_PANIC_IMMUNITIES = {
            Activity.HIDE,
            Activity.PRE_RAID,
            Activity.RAID,
            Activity.PANIC
    };

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

                // ServerCore
                || entity instanceof Ghast
                || entity instanceof Warden
                || entity instanceof MinecartHopper;

    }

    /**
     * Activates entities in {@param world} that are close enough to players.
     */
    public static void activateEntities(ServerLevel level) {
        int maxRange = Integer.MIN_VALUE;
        for (ActivationType type : ActivationType.values()) {
            maxRange = Math.max(type.activationRange.getAsInt(), maxRange);
        }

        ILevel info = (ILevel) level;
        info.setRemainingAnimals(Math.min(info.getRemainingAnimals() + 1, ActivationRangeConfig.ANIMAL_WAKEUP_MAX.get()));
        info.setRemainingVillagers(Math.min(info.getRemainingVillagers() + 1, ActivationRangeConfig.VILLAGER_WAKEUP_MAX.get()));
        info.setRemainingMonsters(Math.min(info.getRemainingMonsters() + 1, ActivationRangeConfig.MONSTER_WAKEUP_MAX.get()));
        info.setRemainingFlying(Math.min(info.getRemainingFlying() + 1, ActivationRangeConfig.FLYING_WAKEUP_MAX.get()));

        maxRange = Math.min((ServerCore.getServer().getPlayerList().getViewDistance() << 4) - 8, maxRange);
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
                activateEntity(entity);
            }
        }
    }

    private static void activateEntity(Entity entity) {
        final int currentTick = ServerCore.getServer().getTickCount();

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
    public static int checkEntityImmunities(Entity entity) {
        final int inactiveWakeUpImmunity = checkInactiveWakeup(entity);
        if (inactiveWakeUpImmunity > -1) {
            return inactiveWakeUpImmunity;
        }

        if (entity.getRemainingFireTicks() > 0) {
            return 2;
        }

        if (entity.getActivatedImmunityTick() >= ServerCore.getServer().getTickCount()) {
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
            if (!entity.isOnGround() && !entity.isInWater() && !(entity instanceof FlyingMob || entity instanceof Bat)) {
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

            if (entity instanceof Bee bee) {
                BlockPos movingTarget = ((IPathFinderMob) bee).getMovingTarget();
                if (bee.isAngry()
                        || (bee.getHivePos() != null && bee.getHivePos().equals(movingTarget))
                        || (bee.getSavedFlowerPos() != null && bee.getSavedFlowerPos().equals(movingTarget))
                ) {
                    return 20;
                }
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
                if (immunityAfter > 0 && (ServerCore.getServer().getTickCount() - entity.getActivatedTick()) >= immunityAfter) {
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

            if (entity instanceof Mob mob && ((IGoalSelector) mob.targetSelector).hasTasks()) {
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
    public static boolean checkIfActive(Entity entity) {
        if (shouldTick(entity)) return true;

        final int currentTick = ServerCore.getServer().getTickCount();
        final boolean active = entity.getActivatedTick() >= currentTick;

        if (!active) {
            if ((currentTick - entity.getActivatedTick() - 1) % 20 == 0) {
                // Check immunities every 20 inactive ticks.
                final int immunity = checkEntityImmunities(entity);
                if (immunity >= 0) {
                    entity.setActivatedTick(currentTick + immunity);
                    return true;
                }

                return entity.getActivationType().tickInactive.getAsBoolean();
            }
            // Spigot - Add a little performance juice to active entities. Skip 1/4 if not immune.
        } else if (ActivationRangeConfig.SKIP_NON_IMMUNE.get() && entity.getFullTickCount() % 4 == 0 && checkEntityImmunities(entity) < 0) {
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

    private static int checkInactiveWakeup(Entity entity) {
        final ActivationType type = entity.getActivationType();
        final ILevel info = (ILevel) entity.level;
        long inactiveFor = ServerCore.getServer().getTickCount() - entity.getActivatedTick();

        if (type == ActivationType.VILLAGER) {
            if (inactiveFor > ActivationRangeConfig.VILLAGER_WAKEUP_INTERVAL.get() * 20 && info.getRemainingVillagers() > 0) {
                info.setRemainingVillagers(info.getRemainingVillagers() - 1);
                return 100;
            }
        } else if (type == ActivationType.ANIMAL || type == ActivationType.NEUTRAL) {
            if (inactiveFor > ActivationRangeConfig.ANIMAL_WAKEUP_INTERVAL.get() * 20 && info.getRemainingAnimals() > 0) {
                info.setRemainingAnimals(info.getRemainingAnimals() - 1);
                return 100;
            }
        } else if (type == ActivationType.FLYING) {
            if (inactiveFor > ActivationRangeConfig.FLYING_WAKEUP_INTERVAL.get() * 20 && info.getRemainingFlying() > 0) {
                info.setRemainingFlying(info.getRemainingFlying() - 1);
                return 100;
            }
        } else if (type == ActivationType.MONSTER || type == ActivationType.MONSTER_BELOW || type == ActivationType.RAIDER || type == ActivationType.ZOMBIE) {
            if (inactiveFor > ActivationRangeConfig.MONSTER_WAKEUP_INTERVAL.get() * 20 && info.getRemainingMonsters() > 0) {
                info.setRemainingMonsters(info.getRemainingMonsters() - 1);
                return 100;
            }
        }
        return -1;
    }

    public enum ActivationType {
        VILLAGER(ActivationRangeConfig.VILLAGER_ACTIVATION_RANGE::get, ActivationRangeConfig.VILLAGER_TICK_INACTIVE::get),
        ZOMBIE(ActivationRangeConfig.ZOMBIE_ACTIVATION_RANGE::get, ActivationRangeConfig.ZOMBIE_TICK_INACTIVE::get, true, false),
        MONSTER(ActivationRangeConfig.MONSTER_ACTIVATION_RANGE::get, ActivationRangeConfig.MONSTER_TICK_INACTIVE::get, true, false),
        MONSTER_BELOW(ActivationRangeConfig.MONSTER_ACTIVATION_RANGE::get, ActivationRangeConfig.MONSTER_TICK_INACTIVE::get, true, true),
        NEUTRAL(ActivationRangeConfig.NEUTRAL_ACTIVATION_RANGE::get, ActivationRangeConfig.NEUTRAL_TICK_INACTIVE::get),
        ANIMAL(ActivationRangeConfig.ANIMAL_ACTIVATION_RANGE::get, ActivationRangeConfig.ANIMAL_TICK_INACTIVE::get),
        WATER(ActivationRangeConfig.WATER_ACTIVATION_RANGE::get, ActivationRangeConfig.WATER_TICK_INACTIVE::get),
        FLYING(ActivationRangeConfig.FLYING_ACTIVATION_RANGE::get, ActivationRangeConfig.FLYING_TICK_INACTIVE::get, true, false),
        RAIDER(ActivationRangeConfig.RAIDER_ACTIVATION_RANGE::get, ActivationRangeConfig.RAIDER_TICK_INACTIVE::get, true, false),
        MISC(ActivationRangeConfig.MISC_ACTIVATION_RANGE::get, ActivationRangeConfig.MISC_TICK_INACTIVE::get);

        private final IntSupplier activationRange;
        private final BooleanSupplier tickInactive;
        private final boolean extraHeightUp;
        private final boolean extraHeightDown;
        private AABB boundingBox = new AABB(0, 0, 0, 0, 0, 0);

        ActivationType(IntSupplier activationRange, BooleanSupplier tickInactive, boolean extraHeightUp, boolean extraHeightDown) {
            this.activationRange = activationRange;
            this.tickInactive = tickInactive;
            this.extraHeightUp = extraHeightUp;
            this.extraHeightDown = extraHeightDown;
        }

        ActivationType(IntSupplier activationRange, BooleanSupplier tickInactive) {
            this(activationRange, tickInactive, false, false);
        }
    }
}