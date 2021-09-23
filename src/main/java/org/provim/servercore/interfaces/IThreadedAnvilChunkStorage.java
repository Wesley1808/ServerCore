package org.provim.servercore.interfaces;

import org.provim.servercore.utils.data_structures.PlayerMobDistanceMap;

public interface IThreadedAnvilChunkStorage {
    PlayerMobDistanceMap getDistanceMap();
}