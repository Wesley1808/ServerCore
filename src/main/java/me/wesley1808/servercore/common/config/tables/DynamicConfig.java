package me.wesley1808.servercore.common.config.tables;

import me.wesley1808.servercore.common.config.ConfigEntry;

public final class DynamicConfig {
    public static final ConfigEntry<Boolean> ENABLED = new ConfigEntry<>(false, "(Default = false) Enables this feature.");
    public static final ConfigEntry<Integer> TARGET_MSPT = new ConfigEntry<>(35, i -> i >= 1, "(Default = 35) The average MSPT to target.");
    public static final ConfigEntry<Integer> UPDATE_RATE = new ConfigEntry<>(15, i -> i >= 1, "(Default = 15) The amount of seconds between dynamic performance updates.");
    public static final ConfigEntry<Integer> VIEW_DISTANCE_UPDATE_RATE = new ConfigEntry<>(150, i -> i >= 1, "(Default = 150) The amount of seconds between dynamic viewdistance updates.\nThis value is separate from the other checks because it makes all clients reload their chunks.");
    public static final ConfigEntry<Integer> MAX_CHUNK_TICK_DISTANCE = new ConfigEntry<>(10, i -> i >= 1, "(Default = [Max: 10, Min: 2]) Distance in which random ticks and mobspawning can happen.");
    public static final ConfigEntry<Integer> MIN_CHUNK_TICK_DISTANCE = new ConfigEntry<>(2, i -> i >= 1);
    public static final ConfigEntry<Integer> MAX_SIMULATION_DISTANCE = new ConfigEntry<>(10, i -> i >= 1, "(Default = [Max: 10, Min: 2]) Distance in which the world will tick, similar to no-tick-vd.");
    public static final ConfigEntry<Integer> MIN_SIMULATION_DISTANCE = new ConfigEntry<>(2, i -> i >= 1);
    public static final ConfigEntry<Integer> MAX_VIEW_DISTANCE = new ConfigEntry<>(10, i -> i >= 2, "(Default = [Max: 10, Min: 2]) Distance in which the world will render.");
    public static final ConfigEntry<Integer> MIN_VIEW_DISTANCE = new ConfigEntry<>(2, i -> i >= 2);
    public static final ConfigEntry<Double> MAX_MOBCAP = new ConfigEntry<>(1.0D, d -> d >= 0.1 && d <= 10.0, "(Default = [Max: 1.0, Min: 0.3]) Global multiplier that decides the percentage of the mobcap to be used.");
    public static final ConfigEntry<Double> MIN_MOBCAP = new ConfigEntry<>(0.3D, d -> d >= 0.1 && d <= 10.0);
}
