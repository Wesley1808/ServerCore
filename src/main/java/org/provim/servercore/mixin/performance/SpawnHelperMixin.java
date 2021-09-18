package org.provim.servercore.mixin.performance;

import net.minecraft.entity.EntityData;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.jetbrains.annotations.Nullable;
import org.provim.servercore.utils.ChunkManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(SpawnHelper.class)
public abstract class SpawnHelperMixin {
    @Unique
    private static Pool<SpawnSettings.SpawnEntry> cachedSpawnEntries;

    @Unique
    private static Biome cachedBiome;

    @Unique
    private static Chunk cachedChunk;

    private SpawnHelperMixin() {
    }

    @Shadow
    private static boolean isAcceptableSpawnPosition(ServerWorld serverWorld, Chunk chunk, BlockPos.Mutable mutable, double d) {
        return false;
    }

    @Shadow
    static Biome getBiomeDirectly(BlockPos pos, Chunk chunk) {
        throw new AssertionError();
    }

    @Shadow
    private static Pool<SpawnSettings.SpawnEntry> getSpawnEntries(ServerWorld world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, SpawnGroup spawnGroup, BlockPos pos, @Nullable Biome biome) {
        throw new AssertionError();
    }

    // Cancel spawn attempts if they are in unloaded chunks.
    @Redirect(method = "spawnEntitiesInChunk(Lnet/minecraft/entity/SpawnGroup;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/world/chunk/Chunk;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/SpawnHelper$Checker;Lnet/minecraft/world/SpawnHelper$Runner;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/SpawnHelper;isAcceptableSpawnPosition(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/world/chunk/Chunk;Lnet/minecraft/util/math/BlockPos$Mutable;D)Z"))
    private static boolean onlySpawnIfLoaded(ServerWorld world, Chunk chunk, BlockPos.Mutable pos, double squaredDistance) {
        return (cachedChunk = ChunkManager.getChunkIfLoaded(world, pos)) != null && isAcceptableSpawnPosition(world, chunk, pos, squaredDistance);
    }

    // Caches biome & spawnEntries.
    @Inject(method = "spawnEntitiesInChunk(Lnet/minecraft/entity/SpawnGroup;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/world/chunk/Chunk;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/SpawnHelper$Checker;Lnet/minecraft/world/SpawnHelper$Runner;)V", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/world/SpawnHelper;isAcceptableSpawnPosition(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/world/chunk/Chunk;Lnet/minecraft/util/math/BlockPos$Mutable;D)Z"))
    private static void cacheValues(SpawnGroup group, ServerWorld world, Chunk chunk, BlockPos pos, SpawnHelper.Checker checker, SpawnHelper.Runner runner, CallbackInfo ci, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, int i, BlockPos.Mutable mutable, int j, int k, int l, int m, int n, SpawnSettings.SpawnEntry spawnEntry, EntityData entityData, int o, int p, int q, double d, double e, PlayerEntity playerEntity, double f, Chunk var30, BlockPos.Mutable var31, double var32) {
        cachedBiome = getBiomeDirectly(pos, cachedChunk);
        cachedSpawnEntries = getSpawnEntries(world, structureAccessor, chunkGenerator, group, mutable, cachedBiome);
    }

    // Use the cached biome.
    @Redirect(method = "getSpawnEntries", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;getBiome(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/world/biome/Biome;"))
    private static Biome useCachedBiome$1(ServerWorld serverWorld, BlockPos pos) {
        return cachedBiome;
    }

    @Redirect(method = "pickRandomSpawnEntry", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;getBiome(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/world/biome/Biome;"))
    private static Biome useCachedBiome$2(ServerWorld serverWorld, BlockPos pos) {
        return cachedBiome;
    }

    // Use the cached spawnEntries.
    @Redirect(method = "containsSpawnEntry", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/SpawnHelper;getSpawnEntries(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/world/gen/StructureAccessor;Lnet/minecraft/world/gen/chunk/ChunkGenerator;Lnet/minecraft/entity/SpawnGroup;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/biome/Biome;)Lnet/minecraft/util/collection/Pool;"))
    private static Pool<SpawnSettings.SpawnEntry> useCachedSpawnEntries$1(ServerWorld world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, SpawnGroup spawnGroup, BlockPos pos, Biome biome) {
        return cachedSpawnEntries;
    }

    @Redirect(method = "pickRandomSpawnEntry", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/SpawnHelper;getSpawnEntries(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/world/gen/StructureAccessor;Lnet/minecraft/world/gen/chunk/ChunkGenerator;Lnet/minecraft/entity/SpawnGroup;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/biome/Biome;)Lnet/minecraft/util/collection/Pool;"))
    private static Pool<SpawnSettings.SpawnEntry> useCachedSpawnEntries$2(ServerWorld world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, SpawnGroup spawnGroup, BlockPos pos, Biome biome) {
        return cachedSpawnEntries;
    }
}