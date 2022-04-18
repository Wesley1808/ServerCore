package me.wesley1808.servercore.mixin.optimizations.ticking.chunk;

import me.wesley1808.servercore.interfaces.ILevelChunk;
import me.wesley1808.servercore.interfaces.IServerLevel;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Supplier;

/**
 * From: Airplane (Optimize-random-calls-in-chunk-ticking.patch)
 * License: GPL-3.0 (licenses/GPL.md)
 */

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin extends Level implements IServerLevel {
    @Unique
    private int currentIceAndSnowTick = 0;

    protected ServerLevelMixin(WritableLevelData writableLevelData, ResourceKey<Level> resourceKey, Holder<DimensionType> holder, Supplier<ProfilerFiller> supplier, boolean bl, boolean bl2, long l, int i) {
        super(writableLevelData, resourceKey, holder, supplier, bl, bl2, l, i);
    }

    @Redirect(
            method = "tickChunk",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/RandomSource;nextInt(I)I",
                    ordinal = 0
            )
    )
    private int replaceLightningCheck(RandomSource randomSource, int i, LevelChunk chunk, int i2) {
        return ((ILevelChunk) chunk).shouldDoLightning(randomSource);
    }

    @Redirect(
            method = "tickChunk",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/RandomSource;nextInt(I)I",
                    ordinal = 1
            )
    )
    private int replaceIceAndSnowCheck(RandomSource randomSource, int i) {
        return this.currentIceAndSnowTick++ & 15;
    }

    @Override
    public void resetIceAndSnowTick() {
        this.currentIceAndSnowTick = this.random.nextInt(16);
    }
}
