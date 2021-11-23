package org.provim.servercore.mixin.optimizations.chunk_loading;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.provim.servercore.utils.ChunkManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MapItem.class)
public abstract class MapItemMixin {

    // Stop maps from loading chunks.
    @Redirect(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getChunkAt(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/chunk/LevelChunk;"))
    private LevelChunk onlyUpdateIfLoaded(Level level, BlockPos pos) {
        return ChunkManager.getChunkIfLoaded(level, pos);
    }

    @Redirect(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/LevelChunk;isEmpty()Z"))
    private boolean validateNotNull(LevelChunk chunk) {
        return chunk == null || chunk.isEmpty();
    }
}