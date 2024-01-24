package me.wesley1808.servercore.mixin.features.misc;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import me.wesley1808.servercore.common.utils.ChunkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Entity.class)
public class EntityMixin {

    // Fall back to default spawn position if the spawn chunks aren't loaded.
    @WrapOperation(
            method = "findDimensionEntryPoint",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/chunk/LevelChunk;getHeight(Lnet/minecraft/world/level/levelgen/Heightmap$Types;II)I"
            )
    )
    private int servercore$fixSpawnHeight(LevelChunk instance, Heightmap.Types types, int x, int y, Operation<Integer> original, ServerLevel level, @Local BlockPos blockPos) {
        return ChunkManager.hasChunk(level, x >> 4, y >> 4) ? original.call(instance, types, x, y) : blockPos.getY();
    }
}
