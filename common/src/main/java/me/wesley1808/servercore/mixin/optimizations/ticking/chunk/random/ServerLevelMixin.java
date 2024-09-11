package me.wesley1808.servercore.mixin.optimizations.ticking.chunk.random;

import me.wesley1808.servercore.common.interfaces.chunk.ILevelChunk;
import me.wesley1808.servercore.common.interfaces.chunk.IServerLevel;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

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
@Mixin(value = ServerLevel.class, priority = 900)
public abstract class ServerLevelMixin extends Level implements IServerLevel {
    @Unique
    private int servercore$currentIceAndSnowTick = 0;

    private ServerLevelMixin(WritableLevelData writableLevelData, ResourceKey<Level> resourceKey, RegistryAccess registryAccess, Holder<DimensionType> holder, boolean bl, boolean bl2, long l, int i) {
        super(writableLevelData, resourceKey, registryAccess, holder, bl, bl2, l, i);
    }

    @Redirect(
            method = "tickChunk",
            require = 0,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/RandomSource;nextInt(I)I",
                    ordinal = 0
            )
    )
    private int servercore$replaceLightningCheck(RandomSource randomSource, int thunderChance, LevelChunk chunk, int randomTickSpeed) {
        return ((ILevelChunk) chunk).servercore$shouldDoLightning(randomSource, thunderChance);
    }

    @Redirect(
            method = "tickChunk",
            require = 0,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/RandomSource;nextInt(I)I",
                    ordinal = 1
            )
    )
    private int servercore$replaceIceAndSnowCheck(RandomSource randomSource, int i) {
        return this.servercore$currentIceAndSnowTick++ & 15;
    }

    @Override
    public void servercore$resetIceAndSnowTick() {
        this.servercore$currentIceAndSnowTick = this.random.nextInt(16);
    }
}
