package me.wesley1808.servercore.mixin.optimizations.ticking.chunk.iteration;

import me.wesley1808.servercore.common.collections.ChunkArrayList;
import me.wesley1808.servercore.common.interfaces.IChunkMap;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(ChunkMap.class)
public class ChunkMapMixin implements IChunkMap {
    @Unique
    private final ChunkArrayList<ServerChunkCache.ChunkAndHolder> tickingChunks = new ChunkArrayList<>();

    @Unique
    @Override
    public List<ServerChunkCache.ChunkAndHolder> getTickingChunks() {
        return this.tickingChunks;
    }

    @Unique
    @Override
    public void addTickingChunk(ServerChunkCache.ChunkAndHolder chunkAndHolder) {
        this.tickingChunks.addChunk(chunkAndHolder);
    }

    @Unique
    @Override
    public void removeTickingChunk(ChunkPos pos) {
        this.tickingChunks.removeChunk(chunkAndHolder -> pos.equals(chunkAndHolder.holder().getPos()));
    }
}