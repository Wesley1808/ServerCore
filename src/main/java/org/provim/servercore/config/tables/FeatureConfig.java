package org.provim.servercore.config.tables;

import org.provim.servercore.config.ConfigEntry;

public final class FeatureConfig {
    public static final ConfigEntry<Boolean> DISABLE_SPAWN_CHUNKS = new ConfigEntry<>(true, "(Default = true) Stops the server from loading spawn chunks.");
    public static final ConfigEntry<Boolean> USE_CHUNK_TICK_DISTANCE = new ConfigEntry<>(false, "(Default = true) Allows ServerCore to modify the distance the server will perform chunk ticks at.\nChunk ticks include randomticks and mobspawning.");
    public static final ConfigEntry<Boolean> FAST_XP_MERGING = new ConfigEntry<>(false, "(Default = false) Allows xp orbs to merge with others that have different experience amounts.\nThis will also allow players to pickup xp much faster.");
    public static final ConfigEntry<Boolean> LOBOTOMIZE_VILLAGERS = new ConfigEntry<>(false, "(Default = false) Makes villagers tick less often if they are stuck in a 1x1 space.");
    public static final ConfigEntry<Integer> LOBOTOMIZED_TICK_INTERVAL = new ConfigEntry<>(20, i -> i >= 2, "(Default = 20) Decides the interval in between villager ticks when lobotomized.");
    public static final ConfigEntry<Integer> AUTO_SAVE_INTERVAL = new ConfigEntry<>(5, i -> i >= 1, "(Default = 5) The amount of minutes in between auto-save intervals when /save-on is active.");
    public static final ConfigEntry<Double> ITEM_MERGE_RADIUS = new ConfigEntry<>(0.5D, d -> d >= 0.5, "(Default = 0.5) Decides the radius in blocks that items / xp will merge at.");
    public static final ConfigEntry<Double> XP_MERGE_RADIUS = new ConfigEntry<>(0.5D, d -> d >= 0.5);
}
