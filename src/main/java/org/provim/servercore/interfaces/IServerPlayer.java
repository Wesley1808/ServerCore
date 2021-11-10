package org.provim.servercore.interfaces;

import net.minecraft.server.level.ServerPlayer;
import org.provim.servercore.utils.data.PooledHashSets;

public interface IServerPlayer {
    PooledHashSets.PooledObjectLinkedOpenHashSet<ServerPlayer> getCachedSingleMobDistanceMap();
}
