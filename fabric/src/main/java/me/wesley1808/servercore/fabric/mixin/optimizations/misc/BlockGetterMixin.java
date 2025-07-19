package me.wesley1808.servercore.fabric.mixin.optimizations.misc;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockGetter.class)
public interface BlockGetterMixin {

    @Redirect(
            method = "method_17743",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/BlockGetter;getFluidState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/material/FluidState;"
            )
    )
    private FluidState servercore$fastFluidCheck(BlockGetter getter, BlockPos pos, @Local(ordinal = 0) BlockState state) {
        return state.getFluidState();
    }
}
