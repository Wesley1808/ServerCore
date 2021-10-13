package org.provim.servercore.config;

public final class FeatureConfig {
    public final ConfigEntry<Boolean> disableSpawnChunks = new ConfigEntry<>(false, " (Default = false) Stops the server from loading spawn chunks.");
    public final ConfigEntry<Boolean> fastXpMerging = new ConfigEntry<>(false, " (Default = false) Allows xp orbs to merge with others that have different experience amounts.\n This will also allow players to pickup xp much faster.");
    public final ConfigEntry<Boolean> useDistanceMap = new ConfigEntry<>(false, " (Default = false) Optimizes vanilla's per-player mobspawning by using PaperMC's PlayerMobDistanceMap.");
    public final ConfigEntry<Boolean> lobotomizeVillagers = new ConfigEntry<>(false, " (Default = false) Makes villagers tick less often if they are stuck in a 1x1 space.");
    public final ConfigEntry<Integer> lobotomizedTickInterval = new ConfigEntry<>(20, " (Default = 20) Decides the interval in between villager ticks when lobotomized.");
    public final ConfigEntry<Integer> autoSaveInterval = new ConfigEntry<>(5, " (Default = 5) The amount of minutes in between auto-save intervals when /save-on is active.");
    public final ConfigEntry<Double> itemMergeRadius = new ConfigEntry<>(0.5D, " (Default = 0.5) Decides the radius in blocks that items / xp will merge at.");
    public final ConfigEntry<Double> xpMergeRadius = new ConfigEntry<>(0.5D);
}
