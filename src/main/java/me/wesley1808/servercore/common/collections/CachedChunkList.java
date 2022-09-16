package me.wesley1808.servercore.common.collections;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.wesley1808.servercore.common.utils.ChunkManager;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;

public class CachedChunkList extends ObjectArrayList<ServerChunkCache.ChunkAndHolder> {
    private final ServerLevel level;
    private boolean trim;

    public CachedChunkList(ServerLevel level) {
        super(1024);
        this.level = level;
    }

    public void shouldTrim() {
        this.trim = true;
    }

    public void update(Iterable<ChunkHolder> holders) {
        if (this.level.getServer().getTickCount() % 20 == 0) {
            this.clear();

            for (ChunkHolder holder : holders) {
                LevelChunk chunk = ChunkManager.getChunkFromFuture(holder.getEntityTickingChunkFuture());
                if (chunk != null && this.level.getChunkSource().chunkMap.anyPlayerCloseEnoughForSpawning(holder.getPos())) {
                    this.add(new ServerChunkCache.ChunkAndHolder(chunk, holder));
                }
            }

            // Trim the list if necessary.
            if (this.trim) {
                this.trim(1024);
                this.trim = false;
            }
        }
    }
}
