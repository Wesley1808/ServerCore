package org.provim.servercore.mixin.performance.activation_range;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;
import org.provim.servercore.config.tables.ActivationRangeConfig;
import org.provim.servercore.interfaces.InactiveEntity;
import org.provim.servercore.utils.ActivationRange;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * From: PaperMC & Spigot (Entity-Activation-Range.patch)
 * License: GPL-3.0 (licenses/GPL.md)
 */

@Mixin(Villager.class)
public abstract class VillagerMixin extends AbstractVillager implements InactiveEntity {
    private VillagerMixin(EntityType<? extends AbstractVillager> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow
    protected abstract void maybeDecayGossip();

    @Shadow
    @Override
    protected abstract void customServerAiStep();

    @Override
    public void inactiveTick() {
        if (this.getUnhappyCounter() > 0) {
            this.setUnhappyCounter(this.getUnhappyCounter() - 1);
        }

        if (ActivationRangeConfig.VILLAGER_TICK_ALWAYS.get()) {
            this.tickCount++;
            this.customServerAiStep();
        }

        this.maybeDecayGossip();
        this.noActionTime++;
        ActivationRange.updateAge(this);
        ActivationRange.updateGoalSelectors(this.goalSelector, this.targetSelector);
    }
}