package org.provim.servercore.config;

public class Config {
    protected static Config instance = new Config();
    public boolean perPlayerSpawns = true;
    public boolean noSpawnChunks = true;
    public boolean useTickDistance = true;
    public double itemMergeRadius = 0.5;
    public boolean useEntityLimits = false;
    public int villagerLimit = 24;
    public int villagerLimitRange = 64;
    public int animalLimit = 32;
    public int animalLimitRange = 64;
    public boolean useDynamicPerformance = true;
    public int maxTickDistance = 10;
    public int minTickDistance = 2;
    public int maxViewDistance = 10;
    public int minViewDistance = 2;
    public double maxMobcap = 1.0;
    public double minMobcap = 0.3;

    public static Config instance() {
        return instance;
    }
}
