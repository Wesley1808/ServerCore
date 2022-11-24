package me.wesley1808.servercore.common.config.tables;

import me.wesley1808.servercore.common.config.ConfigEntry;

public class FeatureConfig {
    public static final ConfigEntry<Boolean> FIX_CLIENT_LAG_ON_CHUNKBORDERS = new ConfigEntry<>(true, "(Default = true) Fixes MC-162253, preventing long freezes on the client when crossing chunkborders.\nPerforming this workaround does require a tiny bit more CPU on the server than it would without it.");
    public static final ConfigEntry<Boolean> DISABLE_SPAWN_CHUNKS = new ConfigEntry<>(false, "(Default = false) Stops the server from loading spawn chunks.");
    public static final ConfigEntry<Boolean> PREVENT_MOVING_INTO_UNLOADED_CHUNKS = new ConfigEntry<>(false, "(Default = false) Prevents lagspikes caused by players moving into unloaded chunks.");
    public static final ConfigEntry<Boolean> LOBOTOMIZE_VILLAGERS = new ConfigEntry<>(false, "(Default = false) Makes villagers tick less often if they are stuck in a 1x1 space.");
    public static final ConfigEntry<Integer> LOBOTOMIZED_TICK_INTERVAL = new ConfigEntry<>(20, i -> i >= 2, "(Default = 20) Decides the interval in between villager ticks when lobotomized.");
    public static final ConfigEntry<Integer> AUTOSAVE_INTERVAL = new ConfigEntry<>(6000, i -> i >= 1, "(Default = 6000) The amount of ticks between auto-saves when /save-on is active.");
    public static final ConfigEntry<Integer> XP_MERGE_CHANCE = new ConfigEntry<>(40, (i) -> i >= 1, "(Default = 40) Decides the chance of XP orbs being able to merge together (1 in X).");
    public static final ConfigEntry<Double> ITEM_MERGE_RADIUS = new ConfigEntry<>(0.5D, d -> d >= 0.5, "(Default = 0.5) Decides the radius in blocks that items / xp will merge at.");
    public static final ConfigEntry<Double> XP_MERGE_RADIUS = new ConfigEntry<>(0.5D, d -> d >= 0.5);
}
