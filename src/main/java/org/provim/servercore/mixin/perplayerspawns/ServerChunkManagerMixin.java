package org.provim.servercore.mixin.perplayerspawns;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.Nullable;
import org.provim.servercore.config.Config;
import org.provim.servercore.interfaces.ChunkHolderInterface;
import org.provim.servercore.interfaces.ServerPlayerEntityInterface;
import org.provim.servercore.interfaces.TACSInterface;
import org.provim.servercore.utils.perplayerspawns.PlayerMobDistanceMap;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Arrays;
import java.util.function.Consumer;


@Mixin(ServerChunkManager.class)
public abstract class ServerChunkManagerMixin extends ChunkManager {

    @Shadow
    @Final
    public ThreadedAnvilChunkStorage threadedAnvilChunkStorage;

    @Shadow
    @Final
    ServerWorld world;

    @Shadow @Nullable protected abstract ChunkHolder getChunkHolder(long pos);

    /**
     * Updates player-mob-distance maps and recalculates mob counts for each player.
     */

    @Redirect(method = "tickChunks", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/SpawnHelper;setupSpawn(ILjava/lang/Iterable;Lnet/minecraft/world/SpawnHelper$ChunkSource;)Lnet/minecraft/world/SpawnHelper$Info;"))
    private SpawnHelper.Info updateSpawnHelper(int spawningChunkCount, Iterable<Entity> entities, SpawnHelper.ChunkSource chunkSource) {
        if (Config.instance().perPlayerSpawns) {
            SpawnHelper.Info spawnHelperInfo;
            PlayerMobDistanceMap distanceMap = ((TACSInterface) threadedAnvilChunkStorage).getPlayerMobDistanceMap();
            if (distanceMap != null) {
                // update distance map
                distanceMap.update(this.world.getPlayers());
                // re-set mob counts
                for (ServerPlayerEntity player : this.world.getPlayers()) {
                    Arrays.fill(((ServerPlayerEntityInterface) player).getMobCounts(), 0);
                }

                spawnHelperInfo = setupSpawn(spawningChunkCount, entities, true);
            } else {
                spawnHelperInfo = setupSpawn(spawningChunkCount, entities, false);
            }

            return spawnHelperInfo;
        } else {
            return SpawnHelper.setupSpawn(spawningChunkCount, entities, chunkSource);
        }
    }

    private SpawnHelper.Info setupSpawn(int spawningChunkCount, Iterable<Entity> entities, boolean countMobs) {
        if (countMobs) {
            TACSInterface chunkStorage = (TACSInterface) world.getChunkManager().threadedAnvilChunkStorage;
            for (Entity entity : entities) {
                chunkStorage.updateMobCounts(entity);
            }
        }
        return SpawnHelper.setupSpawn(spawningChunkCount, entities, this::ifChunkActive);
    }

    private void ifChunkActive(long pos, Consumer<WorldChunk> chunkConsumer) {
        ChunkHolder holder = this.getChunkHolder(pos);
        if (holder != null && ((ChunkHolderInterface) holder).isActive()) {
            holder.getAccessibleFuture().getNow(ChunkHolder.UNLOADED_WORLD_CHUNK).left().ifPresent(chunkConsumer);
        }
    }
}