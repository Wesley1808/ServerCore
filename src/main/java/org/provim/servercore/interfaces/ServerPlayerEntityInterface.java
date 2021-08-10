package org.provim.servercore.interfaces;

import net.minecraft.server.network.ServerPlayerEntity;
import org.provim.servercore.utils.patches.PooledHashSets;

public interface ServerPlayerEntityInterface {

    PooledHashSets.PooledObjectLinkedOpenHashSet<ServerPlayerEntity> getCachedSingleMobDistanceMap();

    int[] getMobCounts();
}
