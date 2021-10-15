package org.provim.servercore.config.tables;

import org.provim.servercore.config.ConfigEntry;

public final class FeatureConfig {
    public static final ConfigEntry<Boolean> DISABLE_SPAWN_CHUNKS = new ConfigEntry<>(true, " (Default = true) Stops the server from loading spawn chunks.");
    public static final ConfigEntry<Boolean> USE_DISTANCE_MAP = new ConfigEntry<>(true, " (Default = true) Optimizes vanilla's per-player mobspawning by using PaperMC's PlayerMobDistanceMap.");
    public static final ConfigEntry<Boolean> FAST_XP_MERGING = new ConfigEntry<>(false, " (Default = false) Allows xp orbs to merge with others that have different experience amounts.\n This will also allow players to pickup xp much faster.");
    public static final ConfigEntry<Boolean> LOBOTOMIZE_VILLAGERS = new ConfigEntry<>(false, " (Default = false) Makes villagers tick less often if they are stuck in a 1x1 space.");
    public static final ConfigEntry<Integer> LOBOTOMIZED_TICK_INTERVAL = new ConfigEntry<>(20, " (Default = 20) Decides the interval in between villager ticks when lobotomized.", i -> i >= 2);
    public static final ConfigEntry<Integer> AUTO_SAVE_INTERVAL = new ConfigEntry<>(5, " (Default = 5) The amount of minutes in between auto-save intervals when /save-on is active.", i -> i >= 1);
    public static final ConfigEntry<Double> ITEM_MERGE_RADIUS = new ConfigEntry<>(0.5D, " (Default = 0.5) Decides the radius in blocks that items / xp will merge at.", d -> d >= 0.5);
    public static final ConfigEntry<Double> XP_MERGE_RADIUS = new ConfigEntry<>(0.5D, d -> d >= 0.5);
}
