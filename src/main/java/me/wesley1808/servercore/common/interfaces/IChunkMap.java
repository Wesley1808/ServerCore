package me.wesley1808.servercore.common.interfaces;

import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.world.level.ChunkPos;

import java.util.List;

public interface IChunkMap {
    List<ServerChunkCache.ChunkAndHolder> getTickingChunks();

    void addTickingChunk(ServerChunkCache.ChunkAndHolder chunkAndHolder);

    void removeTickingChunk(ChunkPos pos);
}
