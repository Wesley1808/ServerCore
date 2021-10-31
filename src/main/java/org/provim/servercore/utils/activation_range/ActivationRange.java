package org.provim.servercore.utils.activation_range;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.HopperMinecartEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import org.provim.servercore.ServerCore;
import org.provim.servercore.config.tables.ActivationRangeConfig;
import org.provim.servercore.interfaces.ActivationTypeEntity;
import org.provim.servercore.interfaces.EntityWithTarget;
import org.provim.servercore.interfaces.IGoalSelector;
import org.provim.servercore.interfaces.TargetPosition;
import org.provim.servercore.mixin.accessor.EntityAccessor;
import org.provim.servercore.mixin.accessor.LivingEntityAccessor;
import org.provim.servercore.mixin.accessor.MobEntityAccessor;

import java.util.function.Function;

/**
 * Partially From: Spigot & PaperMC (Entity-Activation-Range-2.0.patch)
 * License: GPL-3.0 (licenses/GPL.md)
 */


public final class ActivationRange {
    private static final Activity[] VILLAGER_PANIC_IMMUNITIES = {Activity.HIDE, Activity.PRE_RAID, Activity.RAID, Activity.PANIC};
    private static final double MAX_HEIGHT_ABOVE = getMaxRange(ActivationType::getExtraHeightAbove);
    private static final double MAX_HEIGHT_BELOW = getMaxRange(ActivationType::getExtraHeightBelow);
    private static final int MAX_RANGE = getMaxRange(ActivationType::getActivationRange);

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
        } else if (entity.getType() == EntityType.ZOMBIE) {
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
        return ((ActivationTypeEntity) entity).getActivationType().getActivationRange() <= 0
                || entity instanceof EyeOfEnderEntity
                || entity instanceof ItemEntity
                || entity instanceof HopperMinecartEntity
                || entity instanceof PlayerEntity
                || entity instanceof ProjectileEntity
                || entity instanceof EnderDragonEntity
                || entity instanceof EnderDragonPart
                || entity instanceof WitherEntity
                || entity instanceof GhastEntity
                || entity instanceof LightningEntity
                || entity instanceof TntEntity
                || entity instanceof EndCrystalEntity
                || entity instanceof FallingBlockEntity;
    }

    /**
     * Activates entities in {@param world} that are close enough to players.
     */

    public static void activateEntities(ServerWorld world) {
        int maxRange = Math.min((ServerCore.getServer().getPlayerManager().getViewDistance() << 4) - 8, MAX_RANGE);
        for (ServerPlayerEntity player : world.getPlayers()) {
            if (player.isSpectator()) {
                continue;
            }

            Box maxBB;
            if (ActivationRangeConfig.USE_VERTICAL_RANGE.get()) {
                maxBB = player.getBoundingBox().expand(maxRange).stretch(0, MAX_HEIGHT_ABOVE, 0).stretch(0, -MAX_HEIGHT_BELOW, 0);
                for (ActivationType activationType : ActivationType.values()) {
                    activationType.setBoundingBox(player.getBoundingBox().expand(activationType.getActivationRange()).stretch(0, activationType.getExtraHeightAbove(), 0).stretch(0, -activationType.getExtraHeightBelow(), 0));
                }
            } else {
                maxBB = player.getBoundingBox().expand(maxRange, 256, maxRange);
                for (ActivationType activationType : ActivationType.values()) {
                    activationType.setBoundingBox(player.getBoundingBox().expand(activationType.getActivationRange(), 256, activationType.getActivationRange()));
                }
            }

            for (Entity entity : world.getOtherEntities(player, maxBB)) {
                activateEntity(entity);
            }
        }
    }

    private static void activateEntity(Entity entity) {
        ActivationTypeEntity activationTypeEntity = (ActivationTypeEntity) entity;
        if (ServerCore.getServer().getTicks() > activationTypeEntity.getActivatedTick()) {
            if (activationTypeEntity.isExcluded() || activationTypeEntity.getActivationType().getBoundingBox().intersects(entity.getBoundingBox())) {
                activationTypeEntity.setActivatedTick(ServerCore.getServer().getTicks() + 19);
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
        int inactiveWakeUpImmunity = checkInactiveWakeup(entity);
        if (inactiveWakeUpImmunity > -1) {
            return inactiveWakeUpImmunity;
        }

        if (entity.getFireTicks() > 0) {
            return 2;
        }

        ActivationTypeEntity activationTypeEntity = (ActivationTypeEntity) entity;
        ActivationType type = activationTypeEntity.getActivationType();
        long inactiveFor = ServerCore.getServer().getTicks() - activationTypeEntity.getActivatedTick();

        // quick checks.
        if (entity.isTouchingWater() && !(type == ActivationType.ANIMAL || type == ActivationType.FLYING || type == ActivationType.VILLAGER || type == ActivationType.WATER || (type == ActivationType.MISC && !(entity instanceof AbstractMinecartEntity)))) {
            return 100;
        }

        if (!entity.isAlive()) {
            return 40;
        }

        if ((!entity.isOnGround() && !(entity instanceof FlyingEntity))) {
            return 20;
        }

        // special cases.
        if (entity instanceof LivingEntity livingEntity) {
            if (livingEntity.isClimbing() || ((LivingEntityAccessor) livingEntity).isJumping() || !livingEntity.getStatusEffects().isEmpty()) {
                return 1;
            }
            if (entity instanceof MobEntity mobEntity && mobEntity.getTarget() != null) {
                return 20;
            }
            if (entity instanceof BeeEntity bee) {
                BlockPos movingTarget = ((EntityWithTarget) bee).getMovingTarget();
                if (bee.getAngryAt() != null
                        || (bee.getHivePos() != null && bee.getHivePos().equals(movingTarget))
                        || (bee.getFlowerPos() != null && bee.getFlowerPos().equals(movingTarget))
                ) {
                    return 20;
                }
            }
            if (entity instanceof VillagerEntity villager) {
                Brain<VillagerEntity> brain = villager.getBrain();
                if (ActivationRangeConfig.VILLAGER_TICK_PANIC.get()) {
                    for (Activity activity : VILLAGER_PANIC_IMMUNITIES) {
                        if (brain.hasActivity(activity)) {
                            return 20 * 5;
                        }
                    }
                }

                final int villagerWorkImmunity = ActivationRangeConfig.VILLAGER_WORK_IMMUNITY_AFTER.get() * 20;
                if (villagerWorkImmunity > 0 && inactiveFor >= villagerWorkImmunity) {
                    if (brain.hasActivity(Activity.WORK)) {
                        return ActivationRangeConfig.VILLAGER_WORK_IMMUNITY_FOR.get();
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
            if (entity instanceof MobEntity && ((IGoalSelector) ((MobEntityAccessor) entity).getTargetSelector()).hasTasks()) {
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

    public static boolean isActive(Entity entity) {
        if (!ActivationRangeConfig.ENABLED.get()) {
            return true;
        }

        ActivationTypeEntity activationTypeEntity = (ActivationTypeEntity) entity;
        if (isImmune(entity, activationTypeEntity)) {
            return true;
        }

        int ticks = ServerCore.getServer().getTicks();
        boolean active = activationTypeEntity.getActivatedTick() >= ticks;

        if (!active) {
            if ((ticks - activationTypeEntity.getActivatedTick() - 1) % 20 == 0) {
                int immunity = checkEntityImmunities(entity);
                if (immunity >= 0) {
                    activationTypeEntity.setActivatedTick(ticks + immunity);
                    return true;
                }

                return activationTypeEntity.getActivationType().shouldTickInactive();
            }
        }

        return active;
    }

    private static boolean isImmune(Entity entity, ActivationTypeEntity activationTypeEntity) {
        return activationTypeEntity.isExcluded()
                || (entity instanceof LivingEntity living && living.hurtTime > 0)
                || entity.hasNetherPortalCooldown() || ((EntityAccessor) entity).isInNetherPortal();
    }

    /**
     * Wakes up inactive entities on a specified interval.
     *
     * @param entity: The entity to wake up
     * @return Integer: the amount of ticks an entity will wake up for.
     */

    private static int checkInactiveWakeup(Entity entity) {
        ActivationTypeEntity activationTypeEntity = (ActivationTypeEntity) entity;
        ActivationType type = activationTypeEntity.getActivationType();
        if (type.getWakeUpFor() > 0 && ServerCore.getServer().getTicks() - activationTypeEntity.getActivatedTick() > type.getWakeUpInterval()) {
            return type.getWakeUpFor();
        }

        return -1;
    }

    public static void resetTargetPosition(Goal goal) {
        if (goal instanceof MoveToTargetPosGoal targetGoal) {
            ((TargetPosition) targetGoal).setTargetPosition(BlockPos.ORIGIN);
        }
    }

    private static int getMaxRange(Function<ActivationType, Integer> function) {
        int value = 0;
        for (ActivationType type : ActivationType.values()) {
            value = Math.max(function.apply(type), value);
        }

        return value;
    }
}