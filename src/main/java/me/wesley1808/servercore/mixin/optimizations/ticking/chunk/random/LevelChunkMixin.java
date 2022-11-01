package me.wesley1808.servercore.mixin.optimizations.ticking.chunk.random;

import me.wesley1808.servercore.common.interfaces.chunk.ILevelChunk;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import net.minecraft.world.ticks.LevelChunkTicks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
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
@Mixin(LevelChunk.class)
public class LevelChunkMixin implements ILevelChunk {
    // Instead of using a random every time the chunk is ticked, define when lightning strikes preemptively.
    @Unique
    private int lightningTick;

    @Override
    public final int shouldDoLightning(RandomSource randomSource) {
        if (this.lightningTick-- <= 0) {
            this.lightningTick = randomSource.nextInt(100000) << 1;
            return 0;
        }
        return -1;
    }

    @Inject(method = "<init>(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/ChunkPos;Lnet/minecraft/world/level/chunk/UpgradeData;Lnet/minecraft/world/ticks/LevelChunkTicks;Lnet/minecraft/world/ticks/LevelChunkTicks;J[Lnet/minecraft/world/level/chunk/LevelChunkSection;Lnet/minecraft/world/level/chunk/LevelChunk$PostLoadProcessor;Lnet/minecraft/world/level/levelgen/blending/BlendingData;)V", at = @At("RETURN"))
    private void servercore$initLightingTick(Level level, ChunkPos chunkPos, UpgradeData upgradeData, LevelChunkTicks<?> levelChunkTicks, LevelChunkTicks<?> levelChunkTicks2, long l, LevelChunkSection[] levelChunkSections, LevelChunk.PostLoadProcessor postLoadProcessor, BlendingData blendingData, CallbackInfo ci) {
        this.lightningTick = level.threadSafeRandom.nextInt(100000) << 1;
    }
}