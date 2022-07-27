package me.wesley1808.servercore.mixin.features.dynamic_activation;

import me.wesley1808.servercore.common.interfaces.activation_range.DynamicActivationEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * From: Pufferfish (Dynamic-Activation-of-Brain.patch)
 * License: GPL-3.0 (licenses/GPL.md)
 */

@Mixin(Brain.class)
public abstract class BrainMixin<E extends LivingEntity> {
    @Unique
    private int tickCount = 0;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void servercore$shouldTickBehaviors(ServerLevel serverLevel, E livingEntity, CallbackInfo ci) {
        if (livingEntity instanceof DynamicActivationEntity entity && this.tickCount++ % entity.getActivationPriority() != 0) {
            ci.cancel();
        }
    }
}