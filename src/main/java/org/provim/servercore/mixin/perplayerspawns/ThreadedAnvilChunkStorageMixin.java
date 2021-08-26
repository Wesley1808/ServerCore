package org.provim.servercore.mixin.perplayerspawns;

import com.mojang.datafixers.DataFixer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.thread.ThreadExecutor;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.ChunkStatusChangeListener;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.level.storage.LevelStorage;
import org.provim.servercore.ServerCore;
import org.provim.servercore.interfaces.ServerPlayerEntityInterface;
import org.provim.servercore.interfaces.TACSInterface;
import org.provim.servercore.utils.patches.PlayerMobDistanceMap;
import org.provim.servercore.utils.patches.PooledHashSets;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.Executor;
import java.util.function.Supplier;

@Mixin(ThreadedAnvilChunkStorage.class)
public class ThreadedAnvilChunkStorageMixin implements TACSInterface {
    @Shadow
    @Final
    ServerWorld world;
    PlayerMobDistanceMap playerMobDistanceMap = null;

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    public void init(ServerWorld world, LevelStorage.Session session, DataFixer dataFixer, StructureManager structureManager, Executor executor, ThreadExecutor<Runnable> mainThreadExecutor, ChunkProvider chunkProvider, ChunkGenerator chunkGenerator, WorldGenerationProgressListener worldGenerationProgressListener, ChunkStatusChangeListener chunkStatusChangeListener, Supplier<PersistentStateManager> persistentStateManagerFactory, int viewDistance, boolean dsync, CallbackInfo ci) {
        this.playerMobDistanceMap = new PlayerMobDistanceMap();
    }

    @Override
    public PlayerMobDistanceMap getPlayerMobDistanceMap() {
        return playerMobDistanceMap;
    }

    /**
     * Adds entity to the per-player mobcap of nearby players.
     *
     * @param entity: The entity to add
     */

    @Override
    public void updateMobCounts(Entity entity) {
        int chunkX = (int) Math.floor(entity.getPos().getX()) >> 4;
        int chunkZ = (int) Math.floor(entity.getPos().getZ()) >> 4;
        int index = entity.getType().getSpawnGroup().ordinal();

        for (ServerPlayerEntity player : this.playerMobDistanceMap.getPlayersInRange(chunkX, chunkZ)) {
            ++((ServerPlayerEntityInterface) player).getMobCounts()[index];
        }
    }

    /**
     * Removes any player entries in the PlayerMobDistanceMap that are invalid.
     * The PlayerMobDistanceMap may rarely fail to remove a player object from the hashset.
     * This causes areas affected by this to be unable to spawn any mobs.
     */

    @Override
    public void invalidatePlayers() {
        try {
            if (this.playerMobDistanceMap != null) {
                for (PooledHashSets.PooledObjectLinkedOpenHashSet<ServerPlayerEntity> pooledHashSet : this.playerMobDistanceMap.getPlayerMap().values()) {
                    pooledHashSet.getHashSet().removeIf(player -> player == null || !this.world.getPlayers().contains(player));
                }
            }
        } catch (Exception e) {
            ServerCore.getLogger().error("Exception thrown whilst invalidating distance maps!", e);
        }
    }

    @Override
    public int getMobCountNear(ServerPlayerEntity player, SpawnGroup group) {
        return ((ServerPlayerEntityInterface) player).getMobCounts()[group.ordinal()];
    }
}