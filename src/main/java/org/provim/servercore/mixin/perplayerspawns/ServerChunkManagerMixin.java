package org.provim.servercore.mixin.perplayerspawns;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.chunk.ChunkManager;
import org.provim.servercore.config.Config;
import org.provim.servercore.interfaces.ServerPlayerEntityInterface;
import org.provim.servercore.interfaces.TACSInterface;
import org.provim.servercore.utils.patches.PlayerMobDistanceMap;
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
    private SpawnHelper.Info updateSpawnHelper(int spawningChunkCount, Iterable<Entity> entities, SpawnHelper.ChunkSource chunkSource) {
        SpawnHelper.Info spawnHelperInfo;
        PlayerMobDistanceMap distanceMap = ((TACSInterface) threadedAnvilChunkStorage).getPlayerMobDistanceMap();
        if (distanceMap != null && Config.instance().perPlayerSpawns) {
            // Update distance map -> by using a constant 10 as view distance we prevent the situations where:
            // 1 - No mobs will spawn because there's another player with mobs 500 blocks away (High view distance).
            // 2 - There will be 140 mobs in a small area because there's another player 50 blocks away (Low view distance).

            distanceMap.update(this.world.getPlayers(), 10);
            // Re-set mob counts
            for (ServerPlayerEntity player : this.world.getPlayers()) {
                Arrays.fill(((ServerPlayerEntityInterface) player).getMobCounts(), 0);
            }

            spawnHelperInfo = setupSpawn(spawningChunkCount, entities, true, chunkSource);
        } else {
            spawnHelperInfo = setupSpawn(spawningChunkCount, entities, false, chunkSource);
        }
        return spawnHelperInfo;
    }

    private SpawnHelper.Info setupSpawn(int spawningChunkCount, Iterable<Entity> entities, boolean countMobs, SpawnHelper.ChunkSource chunkSource) {
        if (countMobs) {
            TACSInterface chunkStorage = (TACSInterface) world.getChunkManager().threadedAnvilChunkStorage;
            for (Entity entity : entities) {
                chunkStorage.updateMobCounts(entity);
            }
        }
        return SpawnHelper.setupSpawn(spawningChunkCount, entities, chunkSource);
    }
}