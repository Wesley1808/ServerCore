package me.wesley1808.servercore.mixin.optimizations.ticking.chunk.cache;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.wesley1808.servercore.common.collections.CachedChunkList;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
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
import java.util.List;

@Mixin(value = ServerChunkCache.class, priority = 900)
public class ServerChunkCacheMixin {
    @Unique
    private final CachedChunkList servercore$cachedChunks = new CachedChunkList();
    @Shadow
    @Final
    public ChunkMap chunkMap;

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
            index = 6,
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
    @ModifyExpressionValue(
            method = "tickChunks",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ChunkMap;getChunks()Ljava/lang/Iterable;",
                    ordinal = 0
            )
    )
    private Iterable<ChunkHolder> servercore$updateCachedChunks(Iterable<ChunkHolder> original) {
        this.servercore$cachedChunks.update(this.chunkMap, original);
        return Collections::emptyIterator;
    }

    // Don't shuffle the chunk list.
    @Redirect(
            method = "tickChunks",
            require = 0,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/Util;shuffle(Ljava/util/List;Lnet/minecraft/util/RandomSource;)V"
            )
    )
    private void servercore$cancelShuffle(List<?> list, RandomSource randomSource) {
        // NO-OP
    }

    @Redirect(
            method = "tickChunks",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;isNaturalSpawningAllowed(Lnet/minecraft/world/level/ChunkPos;)Z"
            )
    )
    private boolean servercore$skipUnloadedChunks(ServerLevel level, ChunkPos pos, @Local(ordinal = 0) LevelChunk chunk) {
        return chunk.loaded;
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