package me.wesley1808.servercore.mixin.optimizations.ticking.chunk.cache;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.datafixers.DataFixer;
import me.wesley1808.servercore.common.collections.FilteredList;
import me.wesley1808.servercore.common.utils.Util;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.entity.ChunkStatusUpdateListener;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

@Mixin(value = ServerChunkCache.class, priority = 900)
public class ServerChunkCacheMixin {
    @Mutable
    @Shadow
    @Final
    private List<LevelChunk> spawningChunks;
    @Unique
    private int servercore$tickCount;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void servercore$onInit(ServerLevel serverLevel, LevelStorageSource.LevelStorageAccess levelStorageAccess, DataFixer dataFixer, StructureTemplateManager structureTemplateManager, Executor executor, ChunkGenerator chunkGenerator, int i, int j, boolean bl, ChunkProgressListener chunkProgressListener, ChunkStatusUpdateListener chunkStatusUpdateListener, Supplier<?> supplier, CallbackInfo ci) {
        this.spawningChunks = new FilteredList<>(1024, chunk -> chunk.loaded);
        this.servercore$tickCount = Util.WORLD_COUNTER.getAndIncrement();
    }

    // TODO: Check for moonrise compatibility on 1.21.5 release
    // TODO: Profile performance of new separate chunk iteration for random ticks
    @WrapWithCondition(
            method = "tickChunks(Lnet/minecraft/util/profiling/ProfilerFiller;J)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ChunkMap;collectSpawningChunks(Ljava/util/List;)V"
            )
    )
    private boolean servercore$useCachedChunks(ChunkMap chunkMap, List<LevelChunk> list) {
        if (this.servercore$tickCount++ % 20 == 0) {
            list.clear();
            return true;
        }
        return false;
    }

    // Don't shuffle the chunk list.
    @Redirect(
            method = "tickChunks(Lnet/minecraft/util/profiling/ProfilerFiller;J)V",
            require = 0,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/Util;shuffle(Ljava/util/List;Lnet/minecraft/util/RandomSource;)V"
            )
    )
    private void servercore$cancelShuffle(List<LevelChunk> list, RandomSource randomSource) {
        // NO-OP
    }

    @Redirect(
            method = "tickChunks(Lnet/minecraft/util/profiling/ProfilerFiller;J)V",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/List;clear()V"
            )
    )
    private void servercore$cancelListClear(List<LevelChunk> list) {
        // NO-OP
    }
}