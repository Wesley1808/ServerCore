package me.wesley1808.servercore.mixin.features.activation_range.inactive_ticks;

import me.wesley1808.servercore.interfaces.activation_range.InactiveEntity;
import net.minecraft.world.entity.LivingEntity;
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
