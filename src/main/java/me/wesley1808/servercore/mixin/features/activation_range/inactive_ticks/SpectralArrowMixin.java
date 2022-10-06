package me.wesley1808.servercore.mixin.features.activation_range.inactive_ticks;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.SpectralArrow;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Based on: Spigot (Entity-Activation-Range.patch)
 * Patch Author: Aikar (aikar@aikar.co)
 * License: GPL-3.0 (licenses/GPL.md)
 */

@Mixin(SpectralArrow.class)
public abstract class SpectralArrowMixin extends AbstractArrow {
    private SpectralArrowMixin(EntityType<? extends AbstractArrow> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void inactiveTick() {
        super.inactiveTick();

        if (this.inGround) {
            this.tickDespawn();
        }
    }
}