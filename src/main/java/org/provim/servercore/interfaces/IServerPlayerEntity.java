package org.provim.servercore.interfaces;

import net.minecraft.server.network.ServerPlayerEntity;
import org.provim.servercore.utils.data_structures.PooledHashSets;

public interface IServerPlayerEntity {
    PooledHashSets.PooledObjectLinkedOpenHashSet<ServerPlayerEntity> getCachedSingleMobDistanceMap();
}
