package me.wesley1808.servercore.common.utils;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

/**
 * Utility methods for getting chunks.
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
    public static LevelChunk getChunkFromHolder(@Nullable ChunkHolder holder) {
        return holder != null ? getChunkFromFuture(holder.getFullChunkFuture()) : null;
    }

    @Nullable
    public static LevelChunk getChunkFromFuture(CompletableFuture<Either<LevelChunk, ChunkHolder.ChunkLoadingFailure>> chunkFuture) {
        Either<LevelChunk, ChunkHolder.ChunkLoadingFailure> either;
        if (chunkFuture == ChunkHolder.UNLOADED_LEVEL_CHUNK_FUTURE || (either = chunkFuture.getNow(null)) == null) {
            return null;
        }

        return either.left().orElse(null);
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

    public static boolean isChunkLoaded(Level level, BlockPos pos) {
        return isChunkLoaded(level, pos.getX() >> 4, pos.getZ() >> 4);
    }

    public static boolean isChunkLoaded(Level level, int chunkX, int chunkZ) {
        return level.isClientSide || isChunkLoaded(getChunkHolder(level, chunkX, chunkZ));
    }

    public static boolean isChunkLoaded(ChunkHolder holder) {
        return getChunkFromHolder(holder) != null;
    }

    public static void disableSpawnChunks(MinecraftServer server) {
        ServerLevel level = server.overworld();
        ChunkPos pos = new ChunkPos(new BlockPos(level.getLevelData().getXSpawn(), 0, level.getLevelData().getZSpawn()));
        level.getChunkSource().removeRegionTicket(TicketType.START, pos, 11, Unit.INSTANCE);
    }

    // Utility method from PaperMC (MC-Utils.patch)
    public static boolean areChunksLoadedForMove(Level level, AABB box) {
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
                if (!isChunkLoaded(level, chunkX, chunkZ)) {
                    return false;
                }
            }
        }

        return true;
    }
}