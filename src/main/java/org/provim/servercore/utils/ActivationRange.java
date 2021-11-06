package org.provim.servercore.utils;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.HopperMinecartEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.provim.servercore.ServerCore;
import org.provim.servercore.interfaces.ActivationEntity;
import org.provim.servercore.interfaces.IGoalSelector;
import org.provim.servercore.interfaces.IPathAwareEntity;
import org.provim.servercore.interfaces.IWorld;

import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;

import static org.provim.servercore.config.tables.ActivationRangeConfig.*;

/**
 * Partially From: PaperMC & Spigot (Entity-Activation-Range.patch)
 * License: GPL-3.0 (licenses/GPL.md)
 */

public class ActivationRange {
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
        if (entity instanceof RaiderEntity) {
            return ActivationType.RAIDER;
        } else if (entity instanceof WaterCreatureEntity) {
            return ActivationType.WATER;
        } else if (entity instanceof VillagerEntity) {
            return ActivationType.VILLAGER;
        } else if (entity.getType() == EntityType.ZOMBIE || entity.getType() == EntityType.HUSK) {
            return ActivationType.ZOMBIE;
        } else if (entity instanceof CreeperEntity || entity instanceof SlimeEntity || entity instanceof Hoglin) {
            return ActivationType.MONSTER_BELOW;
        } else if (entity instanceof FlyingEntity) {
            return ActivationType.FLYING;
        } else if (entity instanceof Monster) {
            return ActivationType.MONSTER;
        } else if (entity instanceof PassiveEntity || entity instanceof AmbientEntity) {
            return ActivationType.ANIMAL;
        } else if (entity instanceof PathAwareEntity) {
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
        return (((ActivationEntity) entity).getActivationType().activationRange.getAsInt() <= 0)
                || entity instanceof PlayerEntity
                || entity instanceof ThrownEntity
                || entity instanceof EnderDragonEntity
                || entity instanceof EnderDragonPart
                || entity instanceof WitherEntity
                || entity instanceof FireballEntity
                || entity instanceof LightningEntity
                || entity instanceof TntEntity
                || entity instanceof EndCrystalEntity
                || entity instanceof FireworkRocketEntity
                || entity instanceof EyeOfEnderEntity
                || entity instanceof TridentEntity

                // ServerCore
                || entity instanceof GhastEntity
                || entity instanceof HopperMinecartEntity;

    }

    /**
     * Activates entities in {@param world} that are close enough to players.
     */

    public static void activateEntities(ServerWorld world) {
        if (ENABLED.get() && ServerCore.getServer().getTicks() % 20 == 0) {
            int maxRange = Integer.MIN_VALUE;
            for (ActivationType type : ActivationType.values()) {
                maxRange = Math.max(type.activationRange.getAsInt(), maxRange);
            }

            IWorld info = (IWorld) world;
            info.setRemainingAnimals(Math.min(info.getRemainingAnimals() + 1, ANIMAL_WAKEUP_MAX.get()));
            info.setRemainingVillagers(Math.min(info.getRemainingVillagers() + 1, VILLAGER_WAKEUP_MAX.get()));
            info.setRemainingMonsters(Math.min(info.getRemainingMonsters() + 1, MONSTER_WAKEUP_MAX.get()));
            info.setRemainingFlying(Math.min(info.getRemainingFlying() + 1, FLYING_WAKEUP_MAX.get()));

            maxRange = Math.min((ServerCore.getServer().getPlayerManager().getViewDistance() << 4) - 8, maxRange);
            for (ServerPlayerEntity player : world.getPlayers()) {
                if (player.isSpectator()) {
                    continue;
                }

                Box maxBB;
                if (USE_VERTICAL_RANGE.get()) {
                    maxBB = player.getBoundingBox().expand(maxRange, 96, maxRange);
                    for (ActivationType type : ActivationType.values()) {
                        type.boundingBox = player.getBoundingBox().expand(type.activationRange.getAsInt());
                        if (type.extraHeightUp) type.boundingBox.stretch(0, 96, 0);
                        if (type.extraHeightDown) type.boundingBox.stretch(0, -96, 0);
                    }
                } else {
                    maxBB = player.getBoundingBox().expand(maxRange, 256, maxRange);
                    for (ActivationType type : ActivationType.values()) {
                        final int range = type.activationRange.getAsInt();
                        type.boundingBox = player.getBoundingBox().expand(range, 256, range);
                    }
                }

                for (Entity entity : world.getOtherEntities(player, maxBB)) {
                    activateEntity(entity);
                }
            }
        }
    }

    private static void activateEntity(Entity entity) {
        final ActivationEntity activationEntity = (ActivationEntity) entity;
        final int currentTick = ServerCore.getServer().getTicks();

        if (currentTick > activationEntity.getActivatedTick()) {
            if (activationEntity.isExcluded() || activationEntity.getActivationType().boundingBox.intersects(entity.getBoundingBox())) {
                activationEntity.setActivatedTick(currentTick + 19);
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

        if (entity.getFireTicks() > 0) {
            return 2;
        }

        final ActivationEntity activationEntity = (ActivationEntity) entity;
        if (activationEntity.getActivatedImmunityTick() >= ServerCore.getServer().getTicks()) {
            return 1;
        }

        if (!entity.isAlive()) {
            return 40;
        }

        // quick checks.
        final ActivationType type = activationEntity.getActivationType();
        if (entity.isTouchingWater() && entity.isPushedByFluids() && !(type == ActivationType.ANIMAL || type == ActivationType.FLYING || type == ActivationType.VILLAGER || type == ActivationType.WATER || entity instanceof BoatEntity)) {
            return 100;
        }

        // ServerCore - Immunize moving items & xp orbs.
        if (entity instanceof ItemEntity || entity instanceof ExperienceOrbEntity) {
            final Vec3d velocity = entity.getVelocity();
            if (velocity.x != 0 || velocity.z != 0 || velocity.y > 0) {
                return 20;
            }
        }

        if (!(entity instanceof PersistentProjectileEntity projectile)) {
            if (!entity.isOnGround() && !entity.isTouchingWater() && !(entity instanceof FlyingEntity)) {
                return 10;
            }
        } else if (!projectile.inGround) {
            return 1;
        }

        // special cases.
        if (entity instanceof LivingEntity living) {
            if (living.jumping || !living.getActiveStatusEffects().isEmpty() || living.isClimbing()) {
                return 1;
            }

            if (entity instanceof MobEntity mob && mob.getTarget() != null) {
                return 20;
            }

            if (entity instanceof BeeEntity bee) {
                BlockPos movingTarget = ((IPathAwareEntity) bee).getMovingTarget();
                if (bee.getAngryAt() != null
                        || (bee.getHivePos() != null && bee.getHivePos().equals(movingTarget))
                        || (bee.getFlowerPos() != null && bee.getFlowerPos().equals(movingTarget))
                ) {
                    return 20;
                }
            }

            if (entity instanceof VillagerEntity villager) {
                Brain<VillagerEntity> brain = villager.getBrain();

                if (VILLAGER_TICK_PANIC.get()) {
                    for (Activity activity : VILLAGER_PANIC_IMMUNITIES) {
                        if (brain.hasActivity(activity)) {
                            return 20 * 5;
                        }
                    }
                }

                final int immunityAfter = VILLAGER_WORK_IMMUNITY_AFTER.get();
                if (immunityAfter > 0 && (ServerCore.getServer().getTicks() - activationEntity.getActivatedTick()) >= immunityAfter) {
                    if (brain.hasActivity(Activity.WORK)) {
                        return VILLAGER_WORK_IMMUNITY_FOR.get();
                    }
                }
            }

            if (entity instanceof LlamaEntity llama && llama.isFollowing()) {
                return 1;
            }

            if (entity instanceof AnimalEntity animal) {
                if (animal.isBaby() || animal.isInLove()) {
                    return 5;
                }
                if (entity instanceof SheepEntity sheep && sheep.isSheared()) {
                    return 1;
                }
            }

            if (entity instanceof CreeperEntity creeper && creeper.isIgnited()) {
                return 20;
            }

            if (entity instanceof MobEntity mob && ((IGoalSelector) mob.targetSelector).hasTasks()) {
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
        final ActivationEntity activationEntity = (ActivationEntity) entity;
        if (shouldTick(entity, activationEntity)) {
            return true;
        }

        final int currentTick = ServerCore.getServer().getTicks();
        final boolean active = activationEntity.getActivatedTick() >= currentTick;
        activationEntity.setTemporarilyActive(false);

        if (!active) {
            if ((currentTick - activationEntity.getActivatedTick() - 1) % 20 == 0) {
                // Check immunities every 20 inactive ticks.
                if (checkIfImmune(entity, currentTick)) return true;

                final boolean shouldTickInactive = activationEntity.getActivationType().tickInactive.getAsBoolean();
                activationEntity.setTemporarilyActive(shouldTickInactive);
                return shouldTickInactive;
            }
            // Spigot - Add a little performance juice to active entities. Skip 1/4 if not immune.
        } else if (entity.age % 4 == 0) {
            // ServerCore - If immune, increase activated ticks.
            return checkIfImmune(entity, currentTick);
        }
        return active;
    }

    private static boolean shouldTick(Entity entity, ActivationEntity activationEntity) {
        return !ENABLED.get() || activationEntity.isExcluded() || entity.inNetherPortal || entity.hasNetherPortalCooldown()
                || (entity.age < 200 && activationEntity.getActivationType() == ActivationType.MISC) // New misc entities
                || (entity instanceof MobEntity mob && mob.holdingEntity instanceof PlayerEntity) // Player leashed mobs
                || (entity instanceof LivingEntity living && living.hurtTime > 0); // Attacked mobs
    }

    private static boolean checkIfImmune(Entity entity, int currentTick) {
        final ActivationEntity activationEntity = (ActivationEntity) entity;
        final int immunity = checkEntityImmunities(entity);
        if (immunity >= 0) {
            activationEntity.setActivatedTick(currentTick + immunity);
            return true;
        }
        return false;
    }

    private static int checkInactiveWakeup(Entity entity) {
        final ActivationEntity activationEntity = (ActivationEntity) entity;
        final ActivationType type = activationEntity.getActivationType();
        final IWorld info = (IWorld) entity.world;
        long inactiveFor = ServerCore.getServer().getTicks() - activationEntity.getActivatedTick();

        if (type == ActivationType.VILLAGER) {
            if (inactiveFor > VILLAGER_WAKEUP_INTERVAL.get() * 20 && info.getRemainingVillagers() > 0) {
                info.setRemainingVillagers(info.getRemainingVillagers() - 1);
                return 100;
            }
        } else if (type == ActivationType.ANIMAL || type == ActivationType.NEUTRAL) {
            if (inactiveFor > ANIMAL_WAKEUP_INTERVAL.get() * 20 && info.getRemainingAnimals() > 0) {
                info.setRemainingAnimals(info.getRemainingAnimals() - 1);
                return 100;
            }
        } else if (type == ActivationType.FLYING) {
            if (inactiveFor > FLYING_WAKEUP_INTERVAL.get() * 20 && info.getRemainingFlying() > 0) {
                info.setRemainingFlying(info.getRemainingFlying() - 1);
                return 100;
            }
        } else if (type == ActivationType.MONSTER || type == ActivationType.MONSTER_BELOW || type == ActivationType.RAIDER || type == ActivationType.ZOMBIE) {
            if (inactiveFor > MONSTER_WAKEUP_INTERVAL.get() * 20 && info.getRemainingMonsters() > 0) {
                info.setRemainingMonsters(info.getRemainingMonsters() - 1);
                return 100;
            }
        }
        return -1;
    }

    // Updates breeding age for passive entities.
    public static void updateBreedingAge(PassiveEntity passive) {
        final int age = passive.getBreedingAge();
        if (age < 0) {
            passive.setBreedingAge(age + 1);
        } else if (age > 0) {
            passive.setBreedingAge(age - 1);
        }
    }

    // Updates goal selectors for mob entities.
    public static void updateGoalSelectors(GoalSelector goalSelector, GoalSelector targetSelector) {
        if (((IGoalSelector) goalSelector).inactiveTick()) {
            goalSelector.tick();
        }

        if (((IGoalSelector) targetSelector).inactiveTick()) {
            targetSelector.tick();
        }
    }

    public enum ActivationType {
        VILLAGER(VILLAGER_ACTIVATION_RANGE::get, VILLAGER_TICK_INACTIVE::get),
        ZOMBIE(ZOMBIE_ACTIVATION_RANGE::get, ZOMBIE_TICK_INACTIVE::get, true, false),
        MONSTER(MONSTER_ACTIVATION_RANGE::get, MONSTER_TICK_INACTIVE::get, true, false),
        MONSTER_BELOW(MONSTER_ACTIVATION_RANGE::get, MONSTER_TICK_INACTIVE::get, true, true),
        NEUTRAL(NEUTRAL_ACTIVATION_RANGE::get, NEUTRAL_TICK_INACTIVE::get),
        ANIMAL(ANIMAL_ACTIVATION_RANGE::get, ANIMAL_TICK_INACTIVE::get),
        WATER(WATER_ACTIVATION_RANGE::get, WATER_TICK_INACTIVE::get),
        FLYING(FLYING_ACTIVATION_RANGE::get, FLYING_TICK_INACTIVE::get, true, false),
        RAIDER(RAIDER_ACTIVATION_RANGE::get, RAIDER_TICK_INACTIVE::get, true, false),
        MISC(MISC_ACTIVATION_RANGE::get, MISC_TICK_INACTIVE::get);

        private final IntSupplier activationRange;
        private final BooleanSupplier tickInactive;
        private final boolean extraHeightUp;
        private final boolean extraHeightDown;
        private Box boundingBox = new Box(0, 0, 0, 0, 0, 0);

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