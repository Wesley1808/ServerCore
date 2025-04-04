package me.wesley1808.servercore.common.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkResult;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

/**
 * Utility methods for getting chunks.
 *
 * @author Wesley1808
 */
public class ChunkManager {

    @NotNull
    public static Holder<Biome> getRoughBiome(Level level, BlockPos pos) {
        ChunkAccess chunk = getChunkNow(level, pos);
        int x = pos.getX() >> 2;
        int y = pos.getY() >> 2;
        int z = pos.getZ() >> 2;

        return chunk != null ? chunk.getNoiseBiome(x, y, z) : level.getUncachedNoiseBiome(x, y, z);
    }

    @NotNull
    public static BlockState getBlockState(Level level, BlockPos pos) {
        ChunkAccess chunk = getChunkNow(level, pos);
        return chunk != null ? chunk.getBlockState(pos) : Blocks.AIR.defaultBlockState();
    }

    @Nullable
    public static ChunkAccess getChunkNow(LevelReader levelReader, BlockPos pos) {
        return getChunkNow(levelReader, pos.getX() >> 4, pos.getZ() >> 4);
    }

    @Nullable
    public static ChunkAccess getChunkNow(LevelReader levelReader, int chunkX, int chunkZ) {
        if (levelReader instanceof ServerLevel level) {
            return Environment.MOD_MOONRISE
                    ? level.getChunkSource().getChunkNow(chunkX, chunkZ)
                    : getChunkFromHolder(getChunkHolder(level, chunkX, chunkZ));
        } else {
            return levelReader.getChunk(chunkX, chunkZ, ChunkStatus.FULL, false);
        }
    }

    @Nullable
    public static LevelChunk getChunkFromFuture(CompletableFuture<ChunkResult<LevelChunk>> chunkFuture) {
        ChunkResult<LevelChunk> chunkResult;
        if (chunkFuture == ChunkHolder.UNLOADED_LEVEL_CHUNK_FUTURE || (chunkResult = chunkFuture.getNow(null)) == null) {
            return null;
        }

        return chunkResult.orElse(null);
    }

    @Nullable
    private static LevelChunk getChunkFromHolder(ChunkHolder holder) {
        return holder != null ? getChunkFromFuture(holder.getFullChunkFuture()) : null;
    }

    @Nullable
    private static ChunkHolder getChunkHolder(ServerLevel level, int chunkX, int chunkZ) {
        return level.getChunkSource().getVisibleChunkIfPresent(ChunkPos.asLong(chunkX, chunkZ));
    }

    public static boolean hasChunk(Level level, BlockPos pos) {
        return hasChunk(level, pos.getX() >> 4, pos.getZ() >> 4);
    }

    public static boolean hasChunk(Level level, int chunkX, int chunkZ) {
        if (level instanceof ServerLevel serverLevel) {
            return Environment.MOD_MOONRISE
                    ? level.getChunkSource().hasChunk(chunkX, chunkZ)
                    : hasChunk(serverLevel, getChunkHolder(serverLevel, chunkX, chunkZ));
        } else {
            return true;
        }
    }

    public static boolean hasChunk(ServerLevel level, ChunkHolder holder) {
        if (Environment.MOD_MOONRISE) {
            ChunkPos pos = holder.getPos();
            return level.getChunkSource().hasChunk(pos.x, pos.z);
        }

        return getChunkFromHolder(holder) != null;
    }

    // Utility method from PaperMC (MC-Utils.patch)
    public static boolean areChunksLoadedForMove(ServerLevel level, AABB box) {
        int minBlockX = Mth.floor(box.minX - 1.0E-7D) - 3;
        int maxBlockX = Mth.floor(box.maxX + 1.0E-7D) + 3;

        int minBlockZ = Mth.floor(box.minZ - 1.0E-7D) - 3;
        int maxBlockZ = Mth.floor(box.maxZ + 1.0E-7D) + 3;

        int minChunkX = minBlockX >> 4;
        int maxChunkX = maxBlockX >> 4;

        int minChunkZ = minBlockZ >> 4;
        int maxChunkZ = maxBlockZ >> 4;

        for (int chunkX = minChunkX; chunkX <= maxChunkX; ++chunkX) {
            for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; ++chunkZ) {
                if (!hasChunk(level, chunkX, chunkZ)) {
                    return false;
                }
            }
        }

        return true;
    }
}