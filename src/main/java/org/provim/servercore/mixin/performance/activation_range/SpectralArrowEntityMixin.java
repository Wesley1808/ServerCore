package org.provim.servercore.mixin.performance.activation_range;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.world.World;
import org.provim.servercore.interfaces.InactiveEntity;
import org.spongepowered.asm.mixin.Mixin;

/**
 * From: Spigot (Entity-Activation-Range.patch)
 * License: GPL-3.0 (licenses/GPL.md)
 */

@Mixin(SpectralArrowEntity.class)
public abstract class SpectralArrowEntityMixin extends PersistentProjectileEntity implements InactiveEntity {
    private SpectralArrowEntityMixin(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void inactiveTick() {
        if (this.inGround) {
            this.age();
        }
    }
}