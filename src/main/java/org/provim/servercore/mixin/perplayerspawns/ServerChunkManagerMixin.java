package org.provim.servercore.mixin.perplayerspawns;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.chunk.ChunkManager;
import org.provim.servercore.config.Config;
import org.provim.servercore.mixin.accessor.TACSAccessor;
import org.provim.servercore.utils.perplayerspawns.PlayerMobDistanceMap;
import org.provim.servercore.utils.perplayerspawns.ServerPlayerEntityInterface;
import org.provim.servercore.utils.perplayerspawns.TACSInterface;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Arrays;


@Mixin(ServerChunkManager.class)
public abstract class ServerChunkManagerMixin extends ChunkManager {

    @Shadow
    @Final
    public ThreadedAnvilChunkStorage threadedAnvilChunkStorage;

    @Shadow
    @Final
    ServerWorld world;

    /**
     * Updates player-mob-distance maps and recalculates mob counts for each player.
     */

    @Redirect(method = "tickChunks", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/SpawnHelper;setupSpawn(ILjava/lang/Iterable;Lnet/minecraft/world/SpawnHelper$ChunkSource;)Lnet/minecraft/world/SpawnHelper$Info;"))
    public SpawnHelper.Info updateSpawnHelper(int spawningChunkCount, Iterable<Entity> entities, SpawnHelper.ChunkSource chunkSource) {
        SpawnHelper.Info spawnHelperInfo;
        PlayerMobDistanceMap distanceMap = ((TACSInterface) threadedAnvilChunkStorage).getPlayerMobDistanceMap();
        if (distanceMap != null && Config.instance().perPlayerSpawns) {
            // update distance map
            distanceMap.update(this.world.getPlayers(), ((TACSAccessor) threadedAnvilChunkStorage).getWatchDistance());
            // re-set mob counts
            for (ServerPlayerEntity player : this.world.getPlayers()) {
                Arrays.fill(((ServerPlayerEntityInterface) player).getMobCounts(), 0);
            }

            spawnHelperInfo = setupSpawn(spawningChunkCount, entities, chunkSource, true);
        } else {
            spawnHelperInfo = setupSpawn(spawningChunkCount, entities, chunkSource, false);
        }
        return spawnHelperInfo;
    }

    public SpawnHelper.Info setupSpawn(int spawningChunkCount, Iterable<Entity> entities, SpawnHelper.ChunkSource chunkSource, boolean countMobs) {
        if (countMobs) {
            for (Entity entity : entities) {
                ((TACSInterface) world.getChunkManager().threadedAnvilChunkStorage).updateMobCounts(entity);
            }
        }

        return SpawnHelper.setupSpawn(spawningChunkCount, entities, chunkSource);
    }
}