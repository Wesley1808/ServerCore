package me.wesley1808.servercore.common.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Utility methods for getting chunks, blockstates and biomes.
 *
 * @author Wesley1808
 */
public final class ChunkManager {

    @Nullable
    public static LevelChunk getChunkIfLoaded(Level level, BlockPos pos) {
        return getChunkIfLoaded(level, pos.getX() >> 4, pos.getZ() >> 4);
    }

    @Nullable
    public static LevelChunk getChunkIfLoaded(Level level, int chunkX, int chunkZ) {
        if (!level.isClientSide) {
            return getChunkFromHolder(getChunkHolder(level, chunkX, chunkZ));
        } else {
            return level.getChunk(chunkX, chunkZ);
        }
    }

    @Nullable
    public static LevelChunk getChunkFromHolder(ChunkHolder holder) {
        if (holder == null || holder.getFullChunkFuture() == ChunkHolder.UNLOADED_LEVEL_CHUNK_FUTURE) {
            return null;
        }

        return holder.getFullChunk();
    }

    @Nullable
    private static ChunkHolder getChunkHolder(Level level, int chunkX, int chunkZ) {
        if (level.getChunkSource() instanceof ServerChunkCache chunkCache) {
            return chunkCache.getVisibleChunkIfPresent(ChunkPos.asLong(chunkX, chunkZ));
        }

        return null;
    }

    @NotNull
    public static BlockState getStateIfLoaded(Level level, BlockPos pos) {
        final LevelChunk chunk = getChunkIfLoaded(level, pos);
        return chunk != null ? chunk.getBlockState(pos) : Blocks.AIR.defaultBlockState();
    }

    public static Holder<Biome> getRoughBiome(ChunkAccess chunk, BlockPos pos) {
        return chunk.getNoiseBiome(pos.getX() >> 2, pos.getY() >> 2, pos.getZ() >> 2);
    }

    public static boolean isChunkLoaded(Level level, BlockPos pos) {
        return isChunkLoaded(level, pos.getX() >> 4, pos.getZ() >> 4);
    }

    public static boolean isChunkLoaded(Level level, int chunkX, int chunkZ) {
        return level.isClientSide || isChunkLoaded(getChunkHolder(level, chunkX, chunkZ));
    }

    public static boolean isChunkLoaded(ChunkHolder holder) {
        return getChunkFromHolder(holder) != null;
    }

    public static boolean isTouchingUnloadedChunk(Level level, AABB box) {
        final int minX = Mth.floor(box.minX) >> 4;
        final int maxX = Mth.ceil(box.maxX) >> 4;
        final int minZ = Mth.floor(box.minZ) >> 4;
        final int maxZ = Mth.ceil(box.maxZ) >> 4;

        for (int chunkX = minX; chunkX <= maxX; chunkX++) {
            for (int chunkZ = minZ; chunkZ <= maxZ; chunkZ++) {
                if (!ChunkManager.isChunkLoaded(level, chunkX, chunkZ)) {
                    return true;
                }
            }
        }
        return false;
    }
}
