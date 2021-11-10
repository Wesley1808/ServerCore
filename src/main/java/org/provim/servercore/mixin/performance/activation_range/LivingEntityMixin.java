package org.provim.servercore.mixin.performance.activation_range;

import net.minecraft.world.entity.LivingEntity;
import org.provim.servercore.interfaces.InactiveEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * From: Spigot (Entity-Activation-Range.patch)
 * License: GPL-3.0 (licenses/GPL.md)
 */

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements InactiveEntity {

    @Shadow
    protected int noActionTime;

    @Override
    public void inactiveTick() {
        this.noActionTime++;
    }
}
