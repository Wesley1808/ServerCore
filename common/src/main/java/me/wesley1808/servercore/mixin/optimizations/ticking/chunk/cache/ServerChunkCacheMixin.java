package me.wesley1808.servercore.mixin.optimizations.ticking.chunk.cache;

import me.wesley1808.servercore.common.collections.CachedChunkList;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.storage.LevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Mixin(value = ServerChunkCache.class, priority = 900)
public class ServerChunkCacheMixin {
    @Unique
    private final CachedChunkList servercore$cachedChunks = new CachedChunkList();
    @Unique
    private boolean servercore$isChunkLoaded;

    @Inject(method = "save", at = @At("RETURN"))
    private void servercore$onSave(boolean bl, CallbackInfo ci) {
        this.servercore$cachedChunks.shouldTrim();
    }

    // Avoids unnecessary array allocations.
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

    // Replaces the list variable with our own.
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
        return this.servercore$cachedChunks;
    }

    // Updates our own list and prevents vanilla from adding chunks to it.
    @Redirect(
            method = "tickChunks",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ChunkMap;getChunks()Ljava/lang/Iterable;",
                    ordinal = 0
            )
    )
    private Iterable<ChunkHolder> servercore$updateCachedChunks(ChunkMap chunkMap) {
        this.servercore$cachedChunks.update(chunkMap);
        return Collections::emptyIterator;
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

    @Inject(
            method = "tickChunks",
            locals = LocalCapture.CAPTURE_FAILHARD,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;isNaturalSpawningAllowed(Lnet/minecraft/world/level/ChunkPos;)Z",
                    shift = At.Shift.BEFORE
            )
    )
    private void servercore$setLoaded(CallbackInfo ci, long l, long m, boolean bl, LevelData levelData, ProfilerFiller profilerFiller, int i, boolean bl2, int j, NaturalSpawner.SpawnState spawnState, List<?> list, boolean bl3, Iterator<?> var14, ServerChunkCache.ChunkAndHolder chunkAndHolder, LevelChunk chunk, ChunkPos pos) {
        this.servercore$isChunkLoaded = chunk.loaded;
    }

    @Redirect(
            method = "tickChunks",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;isNaturalSpawningAllowed(Lnet/minecraft/world/level/ChunkPos;)Z"
            )
    )
    private boolean servercore$skipUnloadedChunks(ServerLevel level, ChunkPos pos) {
        return this.servercore$isChunkLoaded;
    }

    @Redirect(
            method = "tickChunks",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ChunkMap;anyPlayerCloseEnoughForSpawning(Lnet/minecraft/world/level/ChunkPos;)Z"
            )
    )
    private boolean servercore$skipCheck(ChunkMap chunkMap, ChunkPos pos) {
        return true;
    }
}