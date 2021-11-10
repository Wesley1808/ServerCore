package org.provim.servercore.mixin.performance.activation_range;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.SpectralArrow;
import net.minecraft.world.level.Level;
import org.provim.servercore.interfaces.InactiveEntity;
import org.spongepowered.asm.mixin.Mixin;

/**
 * From: Spigot (Entity-Activation-Range.patch)
 * License: GPL-3.0 (licenses/GPL.md)
 */

@Mixin(SpectralArrow.class)
public abstract class SpectralArrowMixin extends AbstractArrow implements InactiveEntity {
    private SpectralArrowMixin(EntityType<? extends AbstractArrow> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void inactiveTick() {
        if (this.inGround) {
            this.tickDespawn();
        }
    }
}