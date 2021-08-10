package org.provim.servercore.utils;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ChunkTicketManager;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.WorldView;
import net.minecraft.world.chunk.Chunk;
import org.provim.servercore.mixin.accessor.ServerChunkManagerAccessor;

public final class ChunkManager {

    private ChunkManager() {
    }

    /**
     * Returns the BlockState at {@param pos} in {@param world} if the location is loaded.
     */

    public static BlockState getStateIfLoaded(WorldView world, BlockPos pos) {
        final Chunk chunk = getChunkIfLoaded(world, pos);
        return chunk != null ? chunk.getBlockState(pos) : Blocks.VOID_AIR.getDefaultState();
    }

    /**
     * Returns the chunk at {@param pos} in {@param world} if the location is loaded.
     */

    public static Chunk getChunkIfLoaded(WorldView world, BlockPos pos) {
        final ChunkHolder holder = getChunkHolder(world, pos);
        return holder != null ? holder.getCurrentChunk() : null;
    }

    /**
     * Returns the chunk at {@param pos} in {@param world} if the location is visible.
     */

    public static BlockState getStateIfVisible(WorldView world, BlockPos pos) {
        final Chunk chunk = getChunkIfVisible(world, pos);
        return chunk != null ? chunk.getBlockState(pos) : Blocks.VOID_AIR.getDefaultState();
    }

    public static Chunk getChunkIfVisible(WorldView world, BlockPos pos) {
        final ChunkHolder holder = getChunkHolder(world, pos);
        return holder != null ? holder.getAccessibleFuture().getNow(ChunkHolder.UNLOADED_WORLD_CHUNK).left().orElse(null) : null;
    }

    /**
     * Returns a boolean that decides whether or not the chunk at {@param pos} in {@param world} is visible.
     */

    public static boolean isChunkVisible(WorldView world, BlockPos pos) {
        return world instanceof ServerWorld serverWorld && isChunkVisible(serverWorld.getChunkManager(), pos.getX() >> 4, pos.getZ() >> 4);
    }

    public static boolean isChunkVisible(ServerChunkManager manager, int x, int z) {
        return manager.isTickingFutureReady(ChunkPos.toLong(x, z));
    }

    /**
     * Returns the ChunkHolder at {@param pos} in {@param world} if the location is loaded.
     */

    public static ChunkHolder getChunkHolder(WorldView world, BlockPos pos) {
        return world instanceof ServerWorld serverWorld ? getChunkHolder(serverWorld.getChunkManager(), pos.getX() >> 4, pos.getZ() >> 4) : null;
    }

    public static ChunkHolder getChunkHolder(ServerChunkManager manager, int x, int z) {
        return ((ServerChunkManagerAccessor) manager).getHolder(ChunkPos.toLong(x, z));
    }

    /**
     * Returns the ChunkTicketManager from {@param world}.
     */

    public static ChunkTicketManager getTicketManager(WorldView world) {
        return world instanceof ServerWorld serverWorld ? getTicketManager(serverWorld.getChunkManager()) : null;
    }

    public static ChunkTicketManager getTicketManager(ServerChunkManager manager) {
        return ((ServerChunkManagerAccessor) manager).getTicketManager();
    }
}
