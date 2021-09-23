package org.provim.servercore.mixin.performance.maps;

import net.minecraft.item.FilledMapItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.provim.servercore.utils.ChunkManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FilledMapItem.class)
public abstract class FilledMapItemMixin {

    // Stop maps from loading chunks.
    @Redirect(method = "updateColors", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getWorldChunk(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/world/chunk/WorldChunk;"))
    private WorldChunk onlyUpdateIfLoaded(World world, BlockPos pos) {
        return ChunkManager.getChunkIfLoaded(world, pos);
    }

    @Redirect(method = "updateColors", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/WorldChunk;isEmpty()Z"))
    private boolean validateNotNull(WorldChunk worldChunk) {
        return worldChunk == null || worldChunk.isEmpty();
    }
}