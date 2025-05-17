package me.wesley1808.servercore.mixin.optimizations.sync_loads;

import me.wesley1808.servercore.common.utils.ChunkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Level.class)
public class LevelMixin {

    @Redirect(
            method = "updateNeighbourForOutputSignal",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;hasChunkAt(Lnet/minecraft/core/BlockPos;)Z"
            )
    )
    private boolean servercore$onlyUpdateIfLoaded(Level level, BlockPos pos) {
        return ChunkManager.hasChunk(level, pos);
    }
}
