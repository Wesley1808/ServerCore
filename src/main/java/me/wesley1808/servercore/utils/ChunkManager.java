package me.wesley1808.servercore.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

public final class ChunkManager {

    /**
     * Returns the BlockState at {@param pos} in {@param level} if the position is loaded.
     */

    public static BlockState getStateIfLoaded(Level level, BlockPos pos) {
        final LevelChunk chunk = getChunkIfLoaded(level, pos);
        return chunk != null ? chunk.getBlockState(pos) : Blocks.VOID_AIR.defaultBlockState();
    }

    /**
     * Returns the chunk at {@param pos} in {@param level} if the position is loaded.
     */

    @Nullable
    public static LevelChunk getChunkIfLoaded(Level level, BlockPos pos) {
        return getChunkIfLoaded(level, pos.getX() >> 4, pos.getZ() >> 4);
    }

    @Nullable
    public static LevelChunk getChunkIfLoaded(Level level, int chunkX, int chunkZ) {
        if (!level.isClientSide) {
            final ChunkHolder holder = getChunkHolder(level, chunkX, chunkZ);
            return holder != null ? holder.getFullChunkFuture().getNow(ChunkHolder.UNLOADED_LEVEL_CHUNK).left().orElse(null) : null;
        } else {
            return level.getChunk(chunkX, chunkZ);
        }
    }

    /**
     * Returns a boolean that decides whether the chunk at {@param pos} in {@param level} is loaded.
     */

    public static boolean isChunkLoaded(Level level, BlockPos pos) {
        return isChunkLoaded(level, pos.getX() >> 4, pos.getZ() >> 4);
    }

    public static boolean isChunkLoaded(Level level, int chunkX, int chunkZ) {
        return level.isClientSide || isChunkLoaded(getChunkHolder(level, chunkX, chunkZ));
    }

    private static boolean isChunkLoaded(ChunkHolder holder) {
        return holder != null && holder.getFullChunkFuture().getNow(ChunkHolder.UNLOADED_LEVEL_CHUNK).left().isPresent();
    }

    public static boolean isTouchingUnloadedChunk(Level level, AABB box) {
        final int minX = Mth.floor(box.minX) >> 4;
        final int maxX = Mth.ceil(box.maxX) >> 4;
        final int minZ = Mth.floor(box.minZ) >> 4;
        final int maxZ = Mth.ceil(box.maxZ) >> 4;

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                if (!ChunkManager.isChunkLoaded(level, x, z)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns the ChunkHolder at chunk coordinate X / Z in {@param level} if the location is loaded.
     */

    @Nullable
    private static ChunkHolder getChunkHolder(Level level, int chunkX, int chunkZ) {
        return level.getChunkSource() instanceof ServerChunkCache chunkCache ? chunkCache.getVisibleChunkIfPresent(ChunkPos.asLong(chunkX, chunkZ)) : null;
    }
}
