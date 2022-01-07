package me.wesley1808.servercore.interfaces;

import me.wesley1808.servercore.utils.data.PooledHashSets;
import net.minecraft.server.level.ServerPlayer;

public interface IServerPlayer {
    PooledHashSets.PooledObjectLinkedOpenHashSet<ServerPlayer> getCachedSingleMobDistanceMap();
}
