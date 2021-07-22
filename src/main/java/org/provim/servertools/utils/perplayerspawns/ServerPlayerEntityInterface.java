package org.provim.servertools.utils.perplayerspawns;

import net.minecraft.server.network.ServerPlayerEntity;

public interface ServerPlayerEntityInterface {

    PooledHashSets.PooledObjectLinkedOpenHashSet<ServerPlayerEntity> getCachedSingleMobDistanceMap();

    int[] getMobCounts();
}
