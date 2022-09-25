package me.wesley1808.servercore.mixin.optimizations.ticking.chunk.random;

import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LiquidBlock.class)
public abstract class LiquidBlockMixin {

    /**
     * Liquid blocks already run their fluid random ticks in {@link net.minecraft.server.level.ServerLevel#tickChunk(LevelChunk, int)}
     * This patch gets rid of the second 'duplicate' random tick.
     */
    @Redirect(
            method = "isRandomlyTicking",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/material/FluidState;isRandomlyTicking()Z"
            )
    )
    private boolean servercore$cancelDuplicateFluidTicks(FluidState fluidState) {
        return false;
    }
}
