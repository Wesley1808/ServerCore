package me.wesley1808.servercore.mixin.features.ticking;

import me.wesley1808.servercore.common.config.tables.FeatureConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.AcquirePoi;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Based on: Airplane (Skip-POI-finding-if-stuck-in-vehicle.patch)
 * Patch Author: Paul Sauve (paul@technove.co)
 * License: GPL-3.0 (licenses/GPL.md)
 */

@Mixin(AcquirePoi.class)
public class AcquirePoiMixin {
    @Shadow
    private long nextScheduledStart;

    // Airplane - wait an additional 10s to check again if they're stuck
    @Inject(
            method = "start(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/PathfinderMob;J)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;getPoiManager()Lnet/minecraft/world/entity/ai/village/poi/PoiManager;",
                    shift = At.Shift.BEFORE
            )
    )
    private void servercore$skipIfStuck(ServerLevel level, PathfinderMob entity, long gameTime, CallbackInfo ci) {
        if (entity.getNavigation().isStuck() && FeatureConfig.LOBOTOMIZE_VILLAGERS.get()) {
            this.nextScheduledStart += 200L;
        }
    }
}
