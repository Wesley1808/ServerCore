package me.wesley1808.servercore.common.config.tables;

import me.wesley1808.servercore.common.config.ConfigEntry;

public final class FeatureConfig {
    public static final ConfigEntry<Boolean> DISABLE_SPAWN_CHUNKS = new ConfigEntry<>(false, "(Default = false) Stops the server from loading spawn chunks.");
    public static final ConfigEntry<Boolean> FAST_XP_MERGING = new ConfigEntry<>(false, "(Default = false) Allows more xp orbs to merge with one another, to reduce xp orb lag.");
    public static final ConfigEntry<Boolean> PREVENT_MOVING_INTO_UNLOADED_CHUNKS = new ConfigEntry<>(false, "(Default = false) Prevents lagspikes caused by players moving into unloaded chunks.");
    public static final ConfigEntry<Integer> PORTAL_SEARCH_RADIUS = new ConfigEntry<>(128, "(Default = 128) The radius at which the game will look for other portals. Lower values can reduce lagspikes.\nDoes not work if lithium is installed.");
    public static final ConfigEntry<Integer> PORTAL_CREATE_RADIUS = new ConfigEntry<>(16, "(Default = 16) The radius at which the game will try to create new portals.\nIt is recommended to make this smaller than the search radius.");
    public static final ConfigEntry<Boolean> PORTAL_SEARCH_VANILLA_SCALING = new ConfigEntry<>(true, "(Default = true) Whether the custom portal radius will use the vanilla dimension scale.");
    public static final ConfigEntry<Boolean> LOBOTOMIZE_VILLAGERS = new ConfigEntry<>(false, "(Default = false) Makes villagers tick less often if they are stuck in a 1x1 space.");
    public static final ConfigEntry<Integer> LOBOTOMIZED_TICK_INTERVAL = new ConfigEntry<>(20, i -> i >= 2, "(Default = 20) Decides the interval in between villager ticks when lobotomized.");
    public static final ConfigEntry<Integer> CHUNK_SAVE_THRESHOLD = new ConfigEntry<>(-1, "(Default = -1) The threshold MSPT that the server is allowed to run mid-tick chunk saves at.\nSetting this value to negative will disable this threshold.");
    public static final ConfigEntry<Integer> AUTO_SAVE_INTERVAL = new ConfigEntry<>(5, i -> i >= 1, "(Default = 5) The amount of minutes in between auto-save intervals when /save-on is active.");
    public static final ConfigEntry<Double> ITEM_MERGE_RADIUS = new ConfigEntry<>(0.5D, d -> d >= 0.5, "(Default = 0.5) Decides the radius in blocks that items / xp will merge at.");
    public static final ConfigEntry<Double> XP_MERGE_RADIUS = new ConfigEntry<>(0.5D, d -> d >= 0.5);
}
