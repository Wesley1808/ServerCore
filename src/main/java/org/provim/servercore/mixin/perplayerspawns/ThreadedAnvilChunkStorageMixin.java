package org.provim.servercore.mixin.perplayerspawns;

import net.minecraft.entity.Entity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.world.GameRules;
import org.provim.servercore.config.Config;
import org.provim.servercore.interfaces.IServerPlayerEntity;
import org.provim.servercore.interfaces.IThreadedAnvilChunkStorage;
import org.provim.servercore.utils.patches.PlayerMobDistanceMap;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ThreadedAnvilChunkStorage.class)
public class ThreadedAnvilChunkStorageMixin implements IThreadedAnvilChunkStorage {
    @Shadow
    @Final
    ServerWorld world;

    @Unique
    private PlayerMobDistanceMap distanceMap = new PlayerMobDistanceMap();

    @Inject(method = "save(Z)V", at = @At("TAIL"))
    private void resetDistanceMap(boolean flush, CallbackInfo ci) {
        if (Config.getFeatureConfig().perPlayerSpawns && this.world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING)) {
            // Resets the PlayerMobDistanceMap whenever the worlds save.
            // The PlayerMobDistanceMap may rarely fail to remove a player object from the HashSet.
            // This causes areas affected by this to be unable to spawn any mobs.
            this.distanceMap = new PlayerMobDistanceMap();
        }
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

        for (ServerPlayerEntity player : this.distanceMap.getPlayersInRange(chunkX, chunkZ)) {
            ++((IServerPlayerEntity) player).getMobCounts()[index];
        }
    }

    @Override
    public int getMobCountNear(ServerPlayerEntity player, SpawnGroup group) {
        return ((IServerPlayerEntity) player).getMobCounts()[group.ordinal()];
    }

    @Override
    public PlayerMobDistanceMap getDistanceMap() {
        return this.distanceMap;
    }
}