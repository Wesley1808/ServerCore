package me.wesley1808.servercore.mixin.optimizations.mob_spawning;

import me.wesley1808.servercore.utils.ChunkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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

    // Sets cached chunk to null after finishing spawn attempts.
    // This way we guarantee this chunk isn't used for custom spawn logic in other mods, causing weird incompatibilities.
    @Inject(method = "spawnCategoryForPosition(Lnet/minecraft/world/entity/MobCategory;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/chunk/ChunkAccess;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/NaturalSpawner$SpawnPredicate;Lnet/minecraft/world/level/NaturalSpawner$AfterSpawnCallback;)V", at = @At("RETURN"))
    private static void onReturn(MobCategory mobCategory, ServerLevel serverLevel, ChunkAccess chunkAccess, BlockPos blockPos, NaturalSpawner.SpawnPredicate spawnPredicate, NaturalSpawner.AfterSpawnCallback afterSpawnCallback, CallbackInfo ci) {
        cachedChunk = null;
    }

    // Fast biome lookups.
    @Redirect(
            method = "mobsAt",
            require = 0,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;getBiome(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/biome/Biome;"
            )
    )
    private static Biome fastBiomeLookup$1(ServerLevel level, BlockPos pos) {
        return cachedChunk != null ? getRoughBiome(pos, cachedChunk) : level.getBiome(pos);
    }

    @Redirect(
            method = "getRandomSpawnMobAt",
            require = 0,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;getBiome(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/biome/Biome;"
            )
    )
    private static Biome fastBiomeLookup$2(ServerLevel level, BlockPos pos) {
        return cachedChunk != null ? getRoughBiome(pos, cachedChunk) : level.getBiome(pos);
    }

    // Fast block / fluid state lookups.
    @Redirect(
            method = "isInNetherFortressBounds",
            require = 0,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"
            )
    )
    private static BlockState fastBlockStateLookup$1(ServerLevel level, BlockPos pos) {
        return cachedChunk != null ? cachedChunk.getBlockState(pos) : level.getBlockState(pos);
    }

    @Redirect(
            method = "isSpawnPositionOk",
            require = 0,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/LevelReader;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"
            )
    )
    private static BlockState fastBlockStateLookup$2(LevelReader level, BlockPos pos) {
        return cachedChunk != null ? cachedChunk.getBlockState(pos) : level.getBlockState(pos);
    }

    @Redirect(
            method = "isSpawnPositionOk",
            require = 0,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/LevelReader;getFluidState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/material/FluidState;"
            )
    )
    private static FluidState fastFluidStateLookup(LevelReader level, BlockPos pos) {
        return cachedChunk != null ? cachedChunk.getFluidState(pos) : level.getFluidState(pos);
    }
}