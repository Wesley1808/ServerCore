package me.wesley1808.servercore.mixin.optimizations.ticking.chunk.random;

import me.wesley1808.servercore.common.interfaces.chunk.IServerLevel;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Based on: Airplane (Optimize-random-calls-in-chunk-ticking.patch)
 * Patch Author: Paul Sauve (paul@technove.co)
 * License: GPL-3.0 (licenses/GPL.md)
 */

@Mixin(ServerChunkCache.class)
public class ServerChunkCacheMixin {
    @Shadow
    @Final
    ServerLevel level;

    @Inject(
            method = "tickChunks",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/profiling/ProfilerFiller;push(Ljava/lang/String;)V",
                    ordinal = 0
            )
    )
    private void servercore$resetIceAndSnowTick(CallbackInfo ci) {
        ((IServerLevel) this.level).resetIceAndSnowTick();
    }
}