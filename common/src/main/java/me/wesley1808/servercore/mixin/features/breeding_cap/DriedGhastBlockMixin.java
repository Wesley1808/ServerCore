package me.wesley1808.servercore.mixin.features.breeding_cap;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import me.wesley1808.servercore.common.config.Config;
import me.wesley1808.servercore.common.config.data.breeding_cap.BreedingCapConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.block.DriedGhastBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DriedGhastBlock.class)
public class DriedGhastBlockMixin {

    @WrapWithCondition(
            method = "tickWaterlogged",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/DriedGhastBlock;spawnGhastling(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V"
            )
    )
    private boolean servercore$enforceBreedCap(DriedGhastBlock instance, ServerLevel level, BlockPos position, BlockState state) {
        BreedingCapConfig config = Config.get().breedingCap();
        return !config.enabled() || !config.happyGhasts().exceedsLimit(EntityTypes.HAPPY_GHAST, level, position);
    }
}