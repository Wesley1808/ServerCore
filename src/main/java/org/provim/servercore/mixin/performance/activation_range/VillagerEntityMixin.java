package org.provim.servercore.mixin.performance.activation_range;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.world.World;
import org.provim.servercore.config.tables.ActivationRangeConfig;
import org.provim.servercore.interfaces.InactiveEntity;
import org.provim.servercore.utils.ActivationRange;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * From: PaperMC & Spigot (Entity-Activation-Range.patch)
 * License: GPL-3.0 (licenses/GPL.md)
 */

@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin extends MerchantEntity implements InactiveEntity {
    private VillagerEntityMixin(EntityType<? extends MerchantEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow
    @Override
    protected abstract void mobTick();

    @Shadow
    protected abstract void decayGossip();

    @Override
    public void inactiveTick() {
        if (this.getHeadRollingTimeLeft() > 0) {
            this.setHeadRollingTimeLeft(this.getHeadRollingTimeLeft() - 1);
        }

        if (ActivationRangeConfig.VILLAGER_TICK_ALWAYS.get()) {
            this.mobTick();
        }

        this.decayGossip();

        this.despawnCounter++;
        ActivationRange.updateBreedingAge(this);
        ActivationRange.updateGoalSelectors(this.goalSelector, this.targetSelector);
    }
}