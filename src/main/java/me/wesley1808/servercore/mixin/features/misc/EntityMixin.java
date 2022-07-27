package me.wesley1808.servercore.mixin.features.misc;

import me.wesley1808.servercore.common.utils.ChunkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.levelgen.Heightmap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Entity.class)
public abstract class EntityMixin {

    // Fall back to default spawn position if the spawn chunks aren't loaded.
    @Redirect(
            method = "findDimensionEntryPoint",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;getHeightmapPos(Lnet/minecraft/world/level/levelgen/Heightmap$Types;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/BlockPos;"
            )
    )
    private BlockPos servercore$fixSpawnHeight(ServerLevel level, Heightmap.Types types, BlockPos blockPos) {
        return ChunkManager.isChunkLoaded(level, blockPos) ? level.getHeightmapPos(types, blockPos) : blockPos;
    }
}
