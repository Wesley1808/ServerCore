package me.wesley1808.servercore.mixin.optimizations.ticking;

import me.wesley1808.servercore.interfaces.ILevelChunk;
import me.wesley1808.servercore.interfaces.IServerLevel;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Random;
import java.util.function.Supplier;

/**
 * From: Airplane (Optimize-random-calls-in-chunk-ticking.patch)
 * License: GPL-3.0 (licenses/GPL.md)
 */

@Mixin(value = ServerLevel.class, priority = 900)
public abstract class ServerLevelMixin extends Level implements IServerLevel {
    @Unique
    private int currentIceAndSnowTick = 0;

    protected ServerLevelMixin(WritableLevelData writableLevelData, ResourceKey<Level> resourceKey, Holder<DimensionType> holder, Supplier<ProfilerFiller> supplier, boolean bl, boolean bl2, long l) {
        super(writableLevelData, resourceKey, holder, supplier, bl, bl2, l);
    }

    @Redirect(
            method = "tickChunk",
            require = 0,
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Random;nextInt(I)I",
                    ordinal = 0
            )
    )
    private int replaceLightningCheck(Random random, int i, LevelChunk chunk, int randomTickSpeed) {
        return ((ILevelChunk) chunk).shouldDoLightning(this.random);
    }

    @Redirect(
            method = "tickChunk",
            require = 0,
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Random;nextInt(I)I",
                    ordinal = 1
            )
    )
    private int replaceIceAndSnowCheck(Random random, int i) {
        return this.currentIceAndSnowTick++ & 15;
    }

    @Override
    public void resetIceAndSnowTick() {
        this.currentIceAndSnowTick = this.random.nextInt(16);
    }
}
