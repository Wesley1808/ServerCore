package org.provim.servercore.interfaces;

import net.minecraft.entity.Entity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.server.network.ServerPlayerEntity;
import org.provim.servercore.utils.patches.PlayerMobDistanceMap;

public interface IThreadedAnvilChunkStorage {

    PlayerMobDistanceMap getDistanceMap();

    void updateMobCounts(Entity entity);

    int getMobCountNear(ServerPlayerEntity player, SpawnGroup spawnGroup);
}