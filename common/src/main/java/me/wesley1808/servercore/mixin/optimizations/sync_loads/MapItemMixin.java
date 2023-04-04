package me.wesley1808.servercore.mixin.optimizations.sync_loads;

import me.wesley1808.servercore.common.utils.ChunkManager;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MapItem.class)
public class MapItemMixin {

    // Stop maps from loading chunks.
    @Redirect(
            method = "update",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;getChunk(II)Lnet/minecraft/world/level/chunk/LevelChunk;"
            )
    )
    private LevelChunk servercore$onlyUpdateIfLoaded(Level level, int chunkX, int chunkZ) {
        return (LevelChunk) ChunkManager.getChunkNow(level, chunkX, chunkZ);
    }

    @Redirect(
            method = "update",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/chunk/LevelChunk;isEmpty()Z"
            )
    )
    private boolean servercore$validateNotNull(LevelChunk chunk) {
        return chunk == null || chunk.isEmpty();
    }
}