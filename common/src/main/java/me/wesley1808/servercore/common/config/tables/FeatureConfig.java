package me.wesley1808.servercore.common.config.tables;

import me.wesley1808.servercore.common.config.ConfigEntry;

public class FeatureConfig {
    public static final ConfigEntry<Boolean> DISABLE_SPAWN_CHUNKS = new ConfigEntry<>(
            false, "(Default = false) Stops the server from loading spawn chunks."
    );

    public static final ConfigEntry<Boolean> PREVENT_MOVING_INTO_UNLOADED_CHUNKS = new ConfigEntry<>(
            false, "(Default = false) Prevents lagspikes caused by players moving into unloaded chunks."
    );

    public static final ConfigEntry<Boolean> LOBOTOMIZE_VILLAGERS = new ConfigEntry<>(
            false, "(Default = false) Makes villagers tick less often if they are stuck in a 1x1 space."
    );

    public static final ConfigEntry<Integer> LOBOTOMIZED_TICK_INTERVAL = new ConfigEntry<>(
            20, (value) -> value >= 2,
            "(Default = 20) Decides the interval in between villager ticks when lobotomized."
    );

    public static final ConfigEntry<Integer> AUTOSAVE_INTERVAL = new ConfigEntry<>(
            6000, (value) -> value >= 1,
            "(Default = 6000) The amount of ticks between auto-saves when /save-on is active."
    );

    public static final ConfigEntry<Integer> XP_MERGE_CHANCE = new ConfigEntry<>(
            40, (value) -> value >= 1,
            "(Default = 40) Decides the chance of XP orbs being able to merge together (1 in X)."
    );

    public static final ConfigEntry<Double> ITEM_MERGE_RADIUS = new ConfigEntry<>(
            0.5D, (value) -> value >= 0.5,
            "(Default = 0.5) Decides the radius in blocks that items / xp will merge at."
    );

    public static final ConfigEntry<Double> XP_MERGE_RADIUS = new ConfigEntry<>(0.5D, (value) -> value >= 0.5);
}
