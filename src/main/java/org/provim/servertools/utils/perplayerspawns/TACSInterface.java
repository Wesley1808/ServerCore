package org.provim.servertools.utils.perplayerspawns;

import net.minecraft.entity.Entity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.server.network.ServerPlayerEntity;

public interface TACSInterface {

    PlayerMobDistanceMap getPlayerMobDistanceMap();

    void updateMobCounts(Entity entity);

    int getMobCountNear(ServerPlayerEntity player, SpawnGroup spawnGroup);
}