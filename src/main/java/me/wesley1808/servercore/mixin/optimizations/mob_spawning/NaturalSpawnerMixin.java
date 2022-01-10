package me.wesley1808.servercore.mixin.optimizations.mob_spawning;

import me.wesley1808.servercore.utils.ChunkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(NaturalSpawner.class)
public abstract class NaturalSpawnerMixin {
    @Unique
    private static ChunkAccess cachedChunk;

    @Shadow
    static Biome getRoughBiome(BlockPos blockPos, ChunkAccess chunkAccess) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    private static boolean isRightDistanceToPlayerAndSpawnPoint(ServerLevel serverLevel, ChunkAccess chunkAccess, BlockPos.MutableBlockPos mutableBlockPos, double d) {
        throw new UnsupportedOperationException();
    }

    // Cancel spawn attempts if they are in unloaded chunks.
    @Redirect(
            method = "spawnCategoryForPosition(Lnet/minecraft/world/entity/MobCategory;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/chunk/ChunkAccess;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/NaturalSpawner$SpawnPredicate;Lnet/minecraft/world/level/NaturalSpawner$AfterSpawnCallback;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/NaturalSpawner;isRightDistanceToPlayerAndSpawnPoint(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/chunk/ChunkAccess;Lnet/minecraft/core/BlockPos$MutableBlockPos;D)Z"
            )
    )
    private static boolean onlySpawnIfLoaded(ServerLevel world, ChunkAccess chunk, BlockPos.MutableBlockPos pos, double squaredDistance) {
        return isRightDistanceToPlayerAndSpawnPoint(world, chunk, pos, squaredDistance) && (cachedChunk = ChunkManager.getChunkIfLoaded(world, pos)) != null;
    }

    // Fast blockstate lookup.
    @Redirect(
            method = "isInNetherFortressBounds",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"
            )
    )
    private static BlockState fastBlockStateLookup(ServerLevel level, BlockPos pos) {
        return cachedChunk.getBlockState(pos);
    }

    // Fast biome lookups.
    @Redirect(
            method = "mobsAt",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;getBiome(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/biome/Biome;"
            )
    )
    private static Biome fastBiomeLookup$1(ServerLevel level, BlockPos pos) {
        return getRoughBiome(pos, cachedChunk);
    }

    @Redirect(
            method = "getRandomSpawnMobAt",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;getBiome(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/biome/Biome;"
            )
    )
    private static Biome fastBiomeLookup$2(ServerLevel level, BlockPos pos) {
        return getRoughBiome(pos, cachedChunk);
    }
}