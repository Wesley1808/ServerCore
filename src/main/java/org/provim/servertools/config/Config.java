package org.provim.servertools.config;

public class Config {
    protected static Config instance = new Config();
    public boolean perPlayerSpawns = true;
    public boolean useTickDistance = true;
    public boolean useEntityLimits = false;
    public boolean noSpawnChunks = false;
    public int tickDistance = 10;
    public double mobcapModifier = 1.0;
    public double itemMergeRadius = 0.5;
    public int villagerLimit = 24;
    public int villagerLimitRange = 64;
    public int animalLimit = 32;
    public int animalLimitRange = 64;

    public static Config instance() {
        return instance;
    }
}
