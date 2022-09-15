package me.wesley1808.servercore.common.interfaces;

import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.List;

public interface IServerChunkCache {
    List<ServerChunkCache.ChunkAndHolder> getTickingChunks();

    void addTickingChunk(LevelChunk chunk, ChunkHolder holder);

    void removeTickingChunk(ChunkPos pos);

    void addToBroadcast(ChunkHolder holder);
}
