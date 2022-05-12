package me.wesley1808.servercore.common.interfaces;

import me.wesley1808.servercore.common.collections.PooledHashSets;
import net.minecraft.server.level.ServerPlayer;

public interface IServerPlayer {
    PooledHashSets.PooledObjectLinkedOpenHashSet<ServerPlayer> getCachedSingleMobDistanceMap();
}
