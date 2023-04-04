package me.wesley1808.servercore.mixin.optimizations.ticking.chunk.broadcast;

import it.unimi.dsi.fastutil.objects.ReferenceLinkedOpenHashSet;
import me.wesley1808.servercore.common.interfaces.chunk.IServerChunkCache;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.function.Consumer;

@Mixin(value = ServerChunkCache.class, priority = 900)
public class ServerChunkCacheMixin implements IServerChunkCache {
    @Unique
    private final ReferenceLinkedOpenHashSet<ChunkHolder> requiresBroadcast = new ReferenceLinkedOpenHashSet<>(128);

    @Redirect(
            method = "tickChunks",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V",
                    ordinal = 0
            )
    )
    private void servercore$broadcastChanges(List<ServerChunkCache.ChunkAndHolder> list, Consumer<ServerChunkCache.ChunkAndHolder> consumer) {
        for (ChunkHolder holder : this.requiresBroadcast) {
            LevelChunk chunk = holder.getTickingChunk();
            if (chunk != null) {
                holder.broadcastChanges(chunk);
            }
        }
        this.requiresBroadcast.clear();
    }

    @Override
    public void requiresBroadcast(ChunkHolder holder) {
        this.requiresBroadcast.add(holder);
    }
}