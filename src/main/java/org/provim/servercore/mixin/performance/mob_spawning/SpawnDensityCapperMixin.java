package org.provim.servercore.mixin.performance.mob_spawning;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.SpawnDensityCapper;
import org.provim.servercore.config.Config;
import org.provim.servercore.interfaces.IThreadedAnvilChunkStorage;
import org.provim.servercore.mixin.accessor.TACSAccessor;
import org.provim.servercore.utils.Helper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Iterator;
import java.util.List;

@Mixin(SpawnDensityCapper.class)
public abstract class SpawnDensityCapperMixin {

    @Shadow
    @Final
    private ThreadedAnvilChunkStorage threadedAnvilChunkStorage;

    @Shadow
    @Final
    private Long2ObjectMap<List<ServerPlayerEntity>> chunkPosToMobSpawnablePlayers;

    /**
     * @author Wesley1808
     * @reason Use PlayerMobDistanceMap instead of Mojang's default implementation.
     */

    @Overwrite
    private List<ServerPlayerEntity> getMobSpawnablePlayers(ChunkPos pos) {
        if (Config.getFeatureConfig().useDistanceMap) {
            return List.of(); // Return empty list as we won't be using it.
        }

        // Optimizes vanilla code by getting rid of stream allocations.
        final TACSAccessor chunkStorage = (TACSAccessor) this.threadedAnvilChunkStorage;
        return this.chunkPosToMobSpawnablePlayers.computeIfAbsent(pos.toLong(), l -> Helper.getNearbyPlayers(chunkStorage.getWorld(), pos, chunkStorage.getPlayerChunkWatchingManager().watchingPlayers.keySet()));
    }

    @Redirect(method = "canSpawn", at = @At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;"))
    private Iterator<ServerPlayerEntity> useDistanceMap$1(List<ServerPlayerEntity> list, SpawnGroup spawnGroup, ChunkPos chunkPos) {
        return Config.getFeatureConfig().useDistanceMap ? ((IThreadedAnvilChunkStorage) this.threadedAnvilChunkStorage).getDistanceMap().getPlayersInRange(chunkPos).iterator() : list.listIterator();
    }

    @Redirect(method = "increaseDensity", at = @At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;"))
    private Iterator<ServerPlayerEntity> useDistanceMap$2(List<ServerPlayerEntity> list, ChunkPos chunkPos, SpawnGroup spawnGroup) {
        return Config.getFeatureConfig().useDistanceMap ? ((IThreadedAnvilChunkStorage) this.threadedAnvilChunkStorage).getDistanceMap().getPlayersInRange(chunkPos).iterator() : list.listIterator();
    }
}