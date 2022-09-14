package me.wesley1808.servercore.common.interfaces;

import net.minecraft.server.level.ChunkHolder;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;

public interface IServerChunkCache {
    void addTickingChunk(LevelChunk chunk, ChunkHolder holder);

    void removeTickingChunk(ChunkPos pos);
}
