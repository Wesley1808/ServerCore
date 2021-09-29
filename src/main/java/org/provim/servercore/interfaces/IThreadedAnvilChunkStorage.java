package org.provim.servercore.interfaces;

import org.provim.servercore.utils.data.PlayerMobDistanceMap;

public interface IThreadedAnvilChunkStorage {
    PlayerMobDistanceMap getDistanceMap();
}