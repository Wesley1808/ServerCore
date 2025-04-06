package me.wesley1808.servercore.mixin.optimizations.ticking.chunk.cache;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.datafixers.DataFixer;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.wesley1808.servercore.common.utils.Util;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.entity.ChunkStatusUpdateListener;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Mixin(value = ServerChunkCache.class, priority = 900)
public class ServerChunkCacheMixin {
    @Unique
    private final ObjectArrayList<LevelChunk> servercore$blockTickingChunks = new ObjectArrayList<>();
    @Unique
    private boolean servercore$refreshTickingCache = false;
    @Unique
    private int servercore$tickCount;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void servercore$onInit(ServerLevel serverLevel, LevelStorageSource.LevelStorageAccess levelStorageAccess, DataFixer dataFixer, StructureTemplateManager structureTemplateManager, Executor executor, ChunkGenerator chunkGenerator, int i, int j, boolean bl, ChunkProgressListener chunkProgressListener, ChunkStatusUpdateListener chunkStatusUpdateListener, Supplier<?> supplier, CallbackInfo ci) {
        this.servercore$tickCount = Util.WORLD_COUNTER.getAndIncrement();
    }

    @Inject(method = "tickChunks(Lnet/minecraft/util/profiling/ProfilerFiller;J)V", at = @At("HEAD"))
    private void servercore$beforeChunkTicks(CallbackInfo ci) {
        this.servercore$refreshTickingCache = this.servercore$tickCount++ % 20 == 0;
    }

    @Redirect(
            method = "tickChunks(Lnet/minecraft/util/profiling/ProfilerFiller;J)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ChunkMap;forEachBlockTickingChunk(Ljava/util/function/Consumer;)V"
            )
    )
    private void servercore$cacheBlockTickingChunks$notTheCauseOfTickLag(ChunkMap chunkMap, Consumer<LevelChunk> consumer) {
        if (this.servercore$refreshTickingCache) {
            this.servercore$blockTickingChunks.clear();
            chunkMap.forEachBlockTickingChunk(this.servercore$blockTickingChunks::add);
        }

        for (LevelChunk chunk : this.servercore$blockTickingChunks) {
            if (chunk.loaded) consumer.accept(chunk);
        }
    }

    @WrapWithCondition(
            method = "tickChunks(Lnet/minecraft/util/profiling/ProfilerFiller;J)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ChunkMap;collectSpawningChunks(Ljava/util/List;)V"
            )
    )
    private boolean servercore$cacheSpawnTickingChunks(ChunkMap chunkMap, List<LevelChunk> spawningChunks) {
        if (this.servercore$refreshTickingCache) {
            spawningChunks.clear();
            return true;
        }
        return false;
    }

    // Don't try to tick cached unloaded chunks
    @WrapWithCondition(
            method = "tickChunks(Lnet/minecraft/util/profiling/ProfilerFiller;J)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerChunkCache;tickSpawningChunk(Lnet/minecraft/world/level/chunk/LevelChunk;JLjava/util/List;Lnet/minecraft/world/level/NaturalSpawner$SpawnState;)V"
            )
    )
    private boolean servercore$ignoreUnloadedSpawnTicks(ServerChunkCache chunkCache, LevelChunk chunk, long l, List<MobCategory> categories, NaturalSpawner.SpawnState spawnState) {
        return chunk.loaded;
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
    private void servercore$cancelShuffle(List<LevelChunk> spawningChunks, RandomSource randomSource) {
        // NO-OP
    }

    @Redirect(
            method = "tickChunks(Lnet/minecraft/util/profiling/ProfilerFiller;J)V",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/List;clear()V"
            )
    )
    private void servercore$cancelListClear(List<LevelChunk> spawningChunks) {
        // NO-OP
    }
}