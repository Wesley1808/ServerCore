package org.provim.servercore.interfaces;

import org.provim.servercore.utils.PlayerMobDistanceMap;

public interface IThreadedAnvilChunkStorage {
    PlayerMobDistanceMap getPlayerMobDistanceMap();
}