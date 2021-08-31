package org.provim.servercore.config;

public class Config {
    protected static Config instance = new Config();
    public boolean perPlayerSpawns = false;
    public boolean noSpawnChunks = false;
    public boolean useChunkTickDistance = false;
    public double itemMergeRadius = 0.5;
    public int autoSaveInterval = 5;

    // Lobotomize villagers (Purpur)
    public boolean lobotomizeTrappedVillagers = false;
    public int lobotomizedVillagerTickInterval = 20;

    // Entity limits
    public boolean useEntityLimits = false;
    public int villagerLimit = 24;
    public int villagerLimitRange = 64;
    public int animalLimit = 32;
    public int animalLimitRange = 64;

    // Dynamic performance checks
    public boolean runPerformanceChecks = false;
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
