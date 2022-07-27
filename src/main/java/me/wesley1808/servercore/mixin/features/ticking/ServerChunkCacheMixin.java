package me.wesley1808.servercore.mixin.features.ticking;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.wesley1808.servercore.common.ServerCore;
import me.wesley1808.servercore.common.utils.DynamicManager;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

@Mixin(ServerChunkCache.class)
public abstract class ServerChunkCacheMixin {
    @Unique
    private final ObjectArrayList<ServerChunkCache.ChunkAndHolder> active = new ObjectArrayList<>();
    @Unique
    private boolean trim;
    @Shadow
    @Final
    ServerLevel level;

    // Avoid unnecessary array allocations.
    @Redirect(
            method = "tickChunks",
            require = 0,
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/google/common/collect/Lists;newArrayListWithCapacity(I)Ljava/util/ArrayList;",
                    remap = false
            )
    )
    private ArrayList<?> servercore$noList(int initialArraySize) {
        return null;
    }

    // Cancel any operations performed on the list.
    @Redirect(
            method = "tickChunks",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Collections;shuffle(Ljava/util/List;)V"
            )
    )
    private void servercore$cancelShuffle(List<?> list) {
    }

    // Replaces chunk filtering with our own implementation.
    @Redirect(
            method = "tickChunks",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/lang/Iterable;iterator()Ljava/util/Iterator;",
                    ordinal = 0
            )
    )
    private Iterator<ChunkHolder> servercore$filterChunks(Iterable<ChunkHolder> holders) {
        this.updateActiveChunks(holders);
        return Collections.emptyIterator();
    }

    // Only iterate through active chunks.
    @Redirect(
            method = "tickChunks",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/List;iterator()Ljava/util/Iterator;",
                    ordinal = 0
            )
    )
    private Iterator<?> servercore$onlyTickActiveChunks(List<?> list) {
        return this.active.iterator();
    }

    // Only flush active chunks.
    @Redirect(
            method = "tickChunks",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V",
                    ordinal = 0
            )
    )
    private void servercore$flushActiveChunks(List<?> list, Consumer<ServerChunkCache.ChunkAndHolder> action) {
        this.active.forEach(action);
    }

    // Trim the active chunk ticking list periodically.
    @Inject(method = "save", at = @At("TAIL"))
    private void servercore$onSave(boolean bl, CallbackInfo ci) {
        this.trim = true;
    }

    private void updateActiveChunks(Iterable<ChunkHolder> holders) {
        // Update active chunks once a second.
        if (ServerCore.getServer().getTickCount() % 20 == 0) {
            // Clear cached chunks
            this.active.clear();

            // Add active chunks
            for (ChunkHolder holder : holders) {
                LevelChunk chunk = holder.getTickingChunk();
                if (chunk != null) {
                    if (DynamicManager.shouldTickChunk(holder.getPos(), this.level)) {
                        this.active.add(new ServerChunkCache.ChunkAndHolder(chunk, holder));
                    } else {
                        // Send clients block updates from inactive chunks.
                        holder.broadcastChanges(chunk);
                    }
                }
            }

            // Trim the list if necessary.
            if (this.trim) {
                this.active.trim();
                this.trim = false;
            }
        }
    }
}