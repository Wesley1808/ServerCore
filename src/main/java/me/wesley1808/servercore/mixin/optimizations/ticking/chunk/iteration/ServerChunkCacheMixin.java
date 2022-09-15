package me.wesley1808.servercore.mixin.optimizations.ticking.chunk.iteration;

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import me.wesley1808.servercore.common.collections.ChunkArrayList;
import me.wesley1808.servercore.common.interfaces.IServerChunkCache;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

@Mixin(value = ServerChunkCache.class, priority = 900)
public class ServerChunkCacheMixin implements IServerChunkCache {
    @Unique
    private final ReferenceOpenHashSet<ChunkHolder> requiresBroadcast = new ReferenceOpenHashSet<>();
    @Unique
    private final ChunkArrayList<ServerChunkCache.ChunkAndHolder> tickingChunks = new ChunkArrayList<>();

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
        return this.tickingChunks;
    }

    // Don't add chunks to the list.
    @Redirect(
            method = "tickChunks",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ChunkMap;getChunks()Ljava/lang/Iterable;",
                    ordinal = 0
            )
    )
    private Iterable<ChunkHolder> servercore$cancelAddTickingChunks(ChunkMap chunkMap) {
        return Collections::emptyIterator;
    }

    // Don't shuffle the chunk list.
    @Redirect(
            method = "tickChunks",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Collections;shuffle(Ljava/util/List;)V"
            )
    )
    private void servercore$cancelShuffle(List<?> list) {
        // NO-OP
    }

    @Redirect(
            method = "tickChunks",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ChunkMap;anyPlayerCloseEnoughForSpawning(Lnet/minecraft/world/level/ChunkPos;)Z"
            )
    )
    private boolean servercore$shouldTickChunk(ChunkMap chunkMap, ChunkPos pos) {
        return chunkMap.getDistanceManager().inBlockTickingRange(pos.toLong()) && chunkMap.anyPlayerCloseEnoughForSpawning(pos);
    }

    @Redirect(
            method = "tickChunks",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;shouldTickBlocksAt(J)Z"
            )
    )
    private boolean servercore$skipRangeCheck(ServerLevel level, long l) {
        return true;
    }

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
    public List<ServerChunkCache.ChunkAndHolder> getTickingChunks() {
        return this.tickingChunks;
    }

    @Override
    public void addTickingChunk(LevelChunk chunk, ChunkHolder holder) {
        this.tickingChunks.addChunk(new ServerChunkCache.ChunkAndHolder(chunk, holder));
    }

    @Override
    public void removeTickingChunk(ChunkPos pos) {
        this.tickingChunks.removeChunk(chunkAndHolder -> pos.equals(chunkAndHolder.holder().getPos()));
    }

    @Override
    public void addToBroadcast(ChunkHolder holder) {
        this.requiresBroadcast.add(holder);
    }
}