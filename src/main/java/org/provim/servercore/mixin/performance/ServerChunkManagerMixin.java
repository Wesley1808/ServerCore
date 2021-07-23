package org.provim.servercore.mixin.performance;

import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.world.GameRules;
import net.minecraft.world.SpawnHelper;
import org.jetbrains.annotations.Nullable;
import org.provim.servercore.config.Config;
import org.provim.servercore.mixin.accessor.TACSAccessor;
import org.provim.servercore.utils.TickUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;

@Mixin(ServerChunkManager.class)
public abstract class ServerChunkManagerMixin {
    @Unique
    private final HashSet<ChunkHolder> active = new HashSet<>();
    @Shadow
    @Final
    public ThreadedAnvilChunkStorage threadedAnvilChunkStorage;
    @Shadow
    @Final
    ServerWorld world;
    @Shadow
    private boolean spawnMonsters;
    @Shadow
    private boolean spawnAnimals;
    @Shadow
    private long lastMobSpawningTime;
    @Shadow
    @Nullable
    private SpawnHelper.Info spawnInfo;
    private int count;

    /**
     * Stops chunk ticking and mob spawning if they are outside of the tick distance.
     *
     * @param list: The list of chunk holders
     */

    @Redirect(method = "tickChunks", at = @At(value = "INVOKE", target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V", ordinal = 0))
    private <T> void onlyTickActiveChunks(List<ChunkHolder> list, Consumer<? super T> action) {
        updateActiveChunks(list);
        tickActiveChunks(list);
    }

    private void updateActiveChunks(List<ChunkHolder> list) {
        if (Config.instance().useTickDistance) {
            if (this.count++ % 20 == 0) {
                for (ChunkHolder holder : list) {
                    if (holder.getTickingFuture().getNow(ChunkHolder.UNLOADED_WORLD_CHUNK).left().isPresent() && TickUtils.shouldTick(holder.getPos(), world)) {
                        this.active.add(holder);
                    }
                }
                this.active.removeIf(holder -> holder.getTickingFuture().getNow(ChunkHolder.UNLOADED_WORLD_CHUNK).left().isEmpty() || !TickUtils.shouldTick(holder.getPos(), world));
            }
        }
    }

    private void tickActiveChunks(List<ChunkHolder> list) {
        var chunkStorage = (TACSAccessor) this.threadedAnvilChunkStorage;
        long m = this.world.getTime() - this.lastMobSpawningTime;
        var rules = this.world.getGameRules();
        boolean rareSpawn = this.world.getLevelProperties().getTime() % 400L == 0L;
        for (ChunkHolder holder : Config.instance().useTickDistance ? this.active : list) {
            var chunk = holder.getTickingFuture().getNow(ChunkHolder.UNLOADED_WORLD_CHUNK).left().orElse(null);
            if (chunk != null) {
                var pos = chunk.getPos();
                if (this.world.method_37115(pos) && !chunkStorage.tooFarFromPlayersToSpawnMobs(pos)) {
                    chunk.setInhabitedTime(chunk.getInhabitedTime() + m);
                    if (rules.getBoolean(GameRules.DO_MOB_SPAWNING) && (this.spawnMonsters || this.spawnAnimals) && this.world.getWorldBorder().contains(pos)) {
                        SpawnHelper.spawn(this.world, chunk, this.spawnInfo, this.spawnAnimals, this.spawnMonsters, rareSpawn);
                    }
                    this.world.tickChunk(chunk, rules.getInt(GameRules.RANDOM_TICK_SPEED));
                }
                this.world.getProfiler().push("broadcast");
                holder.flushUpdates(chunk);
                this.world.getProfiler().pop();
            }
        }
    }

    @Redirect(method = "tickChunks", at = @At(value = "INVOKE", target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V", ordinal = 1))
    private <T> void cancelChunkFlushing(List<ChunkHolder> list, Consumer<? super T> action) {
    }
}