package me.wesley1808.servercore.mixin.features.ticking;

import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import me.wesley1808.servercore.ServerCore;
import me.wesley1808.servercore.utils.TickManager;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.*;
import java.util.function.Consumer;

@Mixin(ServerChunkCache.class)
public abstract class ServerChunkCacheMixin {
    @Unique
    private final ObjectLinkedOpenHashSet<ServerChunkCache.ChunkAndHolder> active = new ObjectLinkedOpenHashSet<>();

    @Shadow
    @Final
    ServerLevel level;

    // Avoid massive array allocations.
    @Redirect(method = "tickChunks", require = 0, at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Lists;newArrayListWithCapacity(I)Ljava/util/ArrayList;", remap = false))
    private ArrayList<?> noList(int initialArraySize) {
        return null;
    }

    // Cancel any operations performed on the list.
    @Redirect(method = "tickChunks", at = @At(value = "INVOKE", target = "Ljava/util/Collections;shuffle(Ljava/util/List;)V"))
    private void cancelShuffle(List<?> list) {
    }

    // Replaces chunk filtering with our own implementation.
    @Redirect(method = "tickChunks", at = @At(value = "INVOKE", target = "Ljava/lang/Iterable;iterator()Ljava/util/Iterator;", ordinal = 0))
    private Iterator<ChunkHolder> updateFilteredChunks(Iterable<ChunkHolder> iterable) {
        this.updateActiveChunks(iterable);
        return Collections.emptyIterator();
    }

    // Only iterate through active chunks.
    @Redirect(method = "tickChunks", at = @At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;", ordinal = 0))
    private Iterator<?> onlyTickActiveChunks(List<?> list) {
        return this.active.iterator();
    }

    // Only flush active chunks.
    @Redirect(method = "tickChunks", at = @At(value = "INVOKE", target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V", ordinal = 0))
    private void flushActiveChunks(List<?> list, Consumer<ServerChunkCache.ChunkAndHolder> action) {
        this.active.forEach(action);
    }

    /**
     * Replaces the chunk filtering algorithm to support adjustable chunk-tick distances.
     * Random ticks and mob spawns will not happen outside of this distance.
     * This does not affect redstone and block updates.
     */

    private void updateActiveChunks(Iterable<ChunkHolder> holders) {
        // Updates active chunks once a second.
        if (ServerCore.getServer().getTickCount() % 20 == 0) {
            // Clear cached chunks
            this.active.clear();
            // Add active chunks
            for (ChunkHolder holder : holders) {
                Optional<LevelChunk> optional = holder.getTickingChunkFuture().getNow(ChunkHolder.UNLOADED_LEVEL_CHUNK).left();
                if (optional.isPresent()) {
                    if (TickManager.shouldTickChunk(holder.getPos(), this.level)) {
                        this.active.add(new ServerChunkCache.ChunkAndHolder(optional.get(), holder));
                    } else {
                        // Sends clients block updates from inactive chunks.
                        holder.broadcastChanges(optional.get());
                    }
                }
            }
        }
    }
}