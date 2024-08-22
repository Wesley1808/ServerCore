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
 * <p>
 * Especially at over 30,000 chunks these random calls are fairly heavy. We
 * use a different method here for checking lightning, and for checking
 * ice.
 * <p>
 * Lightning: Each chunk now keeps an int of how many ticks until the
 * lightning should strike. This int is a random number from 0 to 100000 * 2,
 * the multiplication is required to keep the probability the same.
 * <p>
 * Ice and snow: We just generate a single random number 0-16 and increment
 * it, while checking if it's 0 for the current chunk.
 * <p>
 * Depending on configuration for things that tick in a chunk, this is a
 * 5-10% improvement.
 * <p>
 * Patch Author: Paul Sauve (paul@technove.co)
 * <br>
 * License: GPL-3.0 (licenses/GPL.md)
 */
@Mixin(ServerChunkCache.class)
public class ServerChunkCacheMixin {
    @Shadow
    @Final
    ServerLevel level;

    @Inject(method = "tickChunks(Lnet/minecraft/util/profiling/ProfilerFiller;JLjava/util/List;)V", at = @At(value = "HEAD"))
    private void servercore$resetIceAndSnowTick(CallbackInfo ci) {
        ((IServerLevel) this.level).servercore$resetIceAndSnowTick();
    }
}