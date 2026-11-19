package me.wesley1808.servercore.mixin.optimizations.ticking.chunk.random;

import me.wesley1808.servercore.common.interfaces.chunk.ILevelChunk;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
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
 * Depending on configuration for things that tick in a chunk, this is a
 * 5-10% improvement.
 * <p>
 * Patch Author: Paul Sauve (paul@technove.co)
 * <br>
 * License: GPL-3.0 (licenses/GPL.md)
 */
@Mixin(value = ServerLevel.class, priority = 900)
public class ServerLevelMixin {

    @Redirect(
            method = "tickThunder",
            require = 0,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/RandomSource;nextInt(I)I",
                    ordinal = 0
            )
    )
    private int servercore$replaceLightningCheck(RandomSource randomSource, int thunderChance, LevelChunk chunk) {
        return ((ILevelChunk) chunk).servercore$shouldDoLightning(randomSource, thunderChance);
    }
}
