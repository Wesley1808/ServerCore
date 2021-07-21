package org.provim.perplayerspawns.utils;

import net.minecraft.server.network.ServerPlayerEntity;

public interface ServerPlayerEntityInterface {

    PooledHashSets.PooledObjectLinkedOpenHashSet<ServerPlayerEntity> getCachedSingleMobDistanceMap();

    int[] getMobCounts();
}
