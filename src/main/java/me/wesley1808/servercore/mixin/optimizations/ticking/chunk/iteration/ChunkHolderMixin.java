package me.wesley1808.servercore.mixin.optimizations.ticking.chunk.iteration;

import com.mojang.datafixers.util.Either;
import me.wesley1808.servercore.common.interfaces.IChunkMap;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(ChunkHolder.class)
public abstract class ChunkHolderMixin {
    @Shadow
    private volatile CompletableFuture<Either<LevelChunk, ChunkHolder.ChunkLoadingFailure>> tickingChunkFuture;

    @Shadow
    @Final
    ChunkPos pos;

    @Inject(
            method = "updateFutures",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ChunkHolder;updateChunkToSave(Ljava/util/concurrent/CompletableFuture;Ljava/lang/String;)V",
                    ordinal = 1
            )
    )
    private void servercore$startTickingChunk(ChunkMap chunkMap, Executor executor, CallbackInfo ci) {
        this.tickingChunkFuture.thenAccept(either -> {
            either.ifLeft(chunk -> {
                ((IChunkMap) chunkMap).addTickingChunk(new ServerChunkCache.ChunkAndHolder(chunk, (ChunkHolder) (Object) this));
            });
        });
    }

    @Inject(
            method = "updateFutures",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/concurrent/CompletableFuture;complete(Ljava/lang/Object;)Z",
                    ordinal = 1
            )
    )
    private void servercore$stopTickingChunk(ChunkMap chunkMap, Executor executor, CallbackInfo ci) {
        ((IChunkMap) chunkMap).removeTickingChunk(this.pos);
    }
}
