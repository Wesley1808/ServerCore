package me.wesley1808.servercore.mixin.features.ticking;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.wesley1808.servercore.common.dynamic.DynamicManager;
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
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Mixin(value = ServerChunkCache.class, priority = 900)
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

    // Replace the list variable with our own.
    @ModifyVariable(
            method = "tickChunks",
            index = 12,
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/lang/Iterable;iterator()Ljava/util/Iterator;",
                    shift = At.Shift.BEFORE,
                    ordinal = 0
            )
    )
    private List<?> servercore$replaceList(List<?> list) {
        return this.active;
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

    // Don't shuffle the chunk list.
    @Redirect(
            method = "tickChunks",
            require = 0,
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Collections;shuffle(Ljava/util/List;)V"
            )
    )
    private void servercore$cancelShuffle(List<?> list) {
        // NO-OP
    }

    // Trim the active chunk ticking list periodically.
    @Inject(method = "save", at = @At("TAIL"))
    private void onSave(boolean bl, CallbackInfo ci) {
        this.trim = true;
    }

    private void updateActiveChunks(Iterable<ChunkHolder> holders) {
        // Update active chunks once a second.
        if (this.level.getServer().getTickCount() % 20 == 0) {
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