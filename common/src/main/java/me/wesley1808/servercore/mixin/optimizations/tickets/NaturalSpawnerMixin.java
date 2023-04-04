package me.wesley1808.servercore.mixin.optimizations.tickets;

import me.wesley1808.servercore.common.utils.ChunkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(NaturalSpawner.class)
public class NaturalSpawnerMixin {

    @Redirect(
            method = "isInNetherFortressBounds",
            require = 0,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"
            )
    )
    private static BlockState servercore$preventAddingTickets(ServerLevel level, BlockPos pos) {
        return ChunkManager.getBlockState(level, pos);
    }
}