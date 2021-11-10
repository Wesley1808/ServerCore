package org.provim.servercore.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.Nullable;

public final class ChunkManager {

    /**
     * Returns the BlockState at {@param pos} in {@param level} if the position is loaded.
     */

    @Nullable
    public static BlockState getStateIfLoaded(Level level, BlockPos pos) {
        final LevelChunk chunk = getChunkIfLoaded(level, pos);
        return chunk != null ? chunk.getBlockState(pos) : Blocks.VOID_AIR.defaultBlockState();
    }

    /**
     * Returns the chunk at {@param pos} in {@param level} if the position is loaded.
     */

    @Nullable
    public static LevelChunk getChunkIfLoaded(Level level, BlockPos pos) {
        final ChunkHolder holder = getChunkHolder(level, pos);
        return holder != null ? holder.getFullChunkFuture().getNow(ChunkHolder.UNLOADED_LEVEL_CHUNK).left().orElse(null) : null;
    }

    /**
     * Returns a boolean that decides whether the chunk at {@param pos} in {@param level} is loaded.
     */

    public static boolean isChunkLoaded(Level level, BlockPos pos) {
        return isChunkLoaded(getChunkHolder(level, pos));
    }

    public static boolean isChunkLoaded(ChunkHolder holder) {
        return holder != null && holder.getFullChunkFuture().getNow(ChunkHolder.UNLOADED_LEVEL_CHUNK).left().isPresent();
    }

    /**
     * Returns the ChunkHolder at {@param pos} in {@param level} if the location is loaded.
     */

    @Nullable
    public static ChunkHolder getChunkHolder(Level level, BlockPos pos) {
        return level.getChunkSource() instanceof ServerChunkCache chunkCache ? chunkCache.getVisibleChunkIfPresent(ChunkPos.asLong(pos.getX() >> 4, pos.getZ() >> 4)) : null;
    }
}
