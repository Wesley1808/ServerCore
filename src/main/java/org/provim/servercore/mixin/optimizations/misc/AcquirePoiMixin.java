package org.provim.servercore.mixin.optimizations.misc;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.AcquirePoi;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * From: Airplane (Skip-POI-finding-if-stuck-in-vehicle.patch)
 * License: GPL-3.0 (licenses/GPL.md)
 */

@Mixin(AcquirePoi.class)
public abstract class AcquirePoiMixin {

    @Shadow
    private long nextScheduledStart;

    // Airplane - wait an additional 10s to check again if they're stuck
    @Inject(method = "start(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/PathfinderMob;J)V", at = @At(value = "INVOKE", shift = At.Shift.BEFORE, target = "Lnet/minecraft/server/level/ServerLevel;getPoiManager()Lnet/minecraft/world/entity/ai/village/poi/PoiManager;"))
    private void skipIfStuck(ServerLevel level, PathfinderMob entity, long gameTime, CallbackInfo ci) {
        if (entity.getNavigation().isStuck()) this.nextScheduledStart += 200L;
    }
}
