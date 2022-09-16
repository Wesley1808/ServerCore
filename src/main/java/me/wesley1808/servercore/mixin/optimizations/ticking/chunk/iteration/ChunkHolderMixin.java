package me.wesley1808.servercore.mixin.optimizations.ticking.chunk.iteration;

import com.mojang.datafixers.util.Either;
import me.wesley1808.servercore.common.interfaces.chunk.IServerChunkCache;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.LevelChunk;
import org.objectweb.asm.Opcodes;
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
    @Final
    ChunkPos pos;
    @Shadow
    @Final
    private LevelHeightAccessor levelHeightAccessor;
    @Shadow
    private volatile CompletableFuture<Either<LevelChunk, ChunkHolder.ChunkLoadingFailure>> tickingChunkFuture;

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
                ((IServerChunkCache) chunkMap.level.getChunkSource()).addTickingChunk(chunk, (ChunkHolder) (Object) this);
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
        ((IServerChunkCache) chunkMap.level.getChunkSource()).removeTickingChunk(this.pos);
    }

    @Inject(
            method = "blockChanged",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/server/level/ChunkHolder;hasChangedSections:Z",
                    opcode = Opcodes.PUTFIELD
            )
    )
    private void servercore$onBlockChanged(BlockPos blockPos, CallbackInfo ci) {
        this.addToBroadcast();
    }

    @Inject(
            method = "sectionLightChanged",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/BitSet;set(I)V"
            )
    )
    private void servercore$onLightChanged(LightLayer lightLayer, int i, CallbackInfo ci) {
        this.addToBroadcast();
    }

    private void addToBroadcast() {
        if (this.levelHeightAccessor instanceof ServerLevel serverLevel) {
            ((IServerChunkCache) serverLevel.getChunkSource()).addToBroadcast((ChunkHolder) (Object) this);
        }
    }
}
