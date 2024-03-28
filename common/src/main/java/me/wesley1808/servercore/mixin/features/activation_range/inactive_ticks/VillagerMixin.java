package me.wesley1808.servercore.mixin.features.activation_range.inactive_ticks;

import me.wesley1808.servercore.common.config.legacy.ActivationRangeConfig;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * Based on: Paper & Spigot (Entity-Activation-Range.patch)
 * <p>
 * Patch Author: Aikar (aikar@aikar.co)
 * <br>
 * License: GPL-3.0 (licenses/GPL.md)
 */
@Mixin(Villager.class)
public abstract class VillagerMixin extends AbstractVillager {
    private VillagerMixin(EntityType<? extends AbstractVillager> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow
    protected abstract void maybeDecayGossip();

    @Shadow
    @Override
    protected abstract void customServerAiStep();

    @Override
    public void servercore$inactiveTick() {
        if (this.getUnhappyCounter() > 0) {
            this.setUnhappyCounter(this.getUnhappyCounter() - 1);
        }

        if (ActivationRangeConfig.VILLAGER_TICK_ALWAYS.get()) {
            this.tickCount++;
            this.customServerAiStep();
        }

        this.maybeDecayGossip();
        super.servercore$inactiveTick();
    }
}