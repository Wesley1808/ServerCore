package org.provim.servercore.utils;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ChunkTicketManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.provim.servercore.mixin.accessor.ServerChunkManagerAccessor;

public final class ChunkManager {

    private ChunkManager() {
    }

    /**
     * Returns the BlockState at {@param pos} in {@param world} if the position is loaded.
     */

    public static BlockState getStateIfLoaded(World world, BlockPos pos) {
        final WorldChunk chunk = getChunkIfLoaded(world, pos);
        return chunk != null ? chunk.getBlockState(pos) : Blocks.VOID_AIR.getDefaultState();
    }

    /**
     * Returns the chunk at {@param pos} in {@param world} if the position is loaded.
     */

    public static WorldChunk getChunkIfLoaded(World world, BlockPos pos) {
        final ChunkHolder holder = getChunkHolder(world, pos);
        return holder != null ? holder.getAccessibleFuture().getNow(ChunkHolder.UNLOADED_WORLD_CHUNK).left().orElse(null) : null;
    }

    /**
     * Returns a boolean that decides whether the chunk at {@param pos} in {@param world} is loaded.
     */

    public static boolean isChunkLoaded(World world, BlockPos pos) {
        return isChunkLoaded(getChunkHolder(world, pos));
    }

    public static boolean isChunkLoaded(ChunkHolder holder) {
        return holder != null && holder.getAccessibleFuture().getNow(ChunkHolder.UNLOADED_WORLD_CHUNK).left().isPresent();
    }

    /**
     * Returns the ChunkHolder at {@param pos} in {@param world} if the location is loaded.
     */

    public static ChunkHolder getChunkHolder(World world, BlockPos pos) {
        return world.getChunkManager() instanceof ServerChunkManagerAccessor manager ? manager.getHolder(ChunkPos.toLong(pos.getX() >> 4, pos.getZ() >> 4)) : null;
    }

    /**
     * Returns the ChunkTicketManager from {@param world}.
     */

    public static ChunkTicketManager getTicketManager(World world) {
        return world.getChunkManager() instanceof ServerChunkManagerAccessor manager ? manager.getTicketManager() : null;
    }
}
