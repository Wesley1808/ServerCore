package org.provim.servercore.config.tables;

import org.provim.servercore.config.ConfigEntry;

public final class DynamicConfig {
    public static final ConfigEntry<Boolean> ENABLED = new ConfigEntry<>(false, "(Default = false) Enables this feature.");
    public static final ConfigEntry<Integer> TARGET_MSPT = new ConfigEntry<>(40, i -> i >= 1, "(Default = 40) The average MSPT to target.");
    public static final ConfigEntry<Integer> MAX_CHUNK_TICK_DISTANCE = new ConfigEntry<>(10, i -> i >= 1 && i <= 32, "(Default = [Max: 10, Min: 2]) Distance in which random ticks and mobspawning can happen.");
    public static final ConfigEntry<Integer> MIN_CHUNK_TICK_DISTANCE = new ConfigEntry<>(2, i -> i >= 1 && i <= 32);
    public static final ConfigEntry<Integer> MAX_SIMULATION_DISTANCE = new ConfigEntry<>(10, i -> i >= 1 && i <= 32, "(Default = [Max: 10, Min: 2]) Distance in which the world will tick, similar to no-tick-vd.");
    public static final ConfigEntry<Integer> MIN_SIMULATION_DISTANCE = new ConfigEntry<>(2, i -> i >= 1 && i <= 32);
    public static final ConfigEntry<Integer> MAX_VIEW_DISTANCE = new ConfigEntry<>(10, i -> i >= 2 && i <= 32, "(Default = [Max: 10, Min: 2]) Distance in which the world will render.");
    public static final ConfigEntry<Integer> MIN_VIEW_DISTANCE = new ConfigEntry<>(2, i -> i >= 2 && i <= 32);
    public static final ConfigEntry<Double> MAX_MOBCAP = new ConfigEntry<>(1.0D, d -> d >= 0.1 && d <= 10.0, "(Default = [Max: 1.0, Min: 0.3]) Global multiplier that decides the percentage of the mobcap to be used.");
    public static final ConfigEntry<Double> MIN_MOBCAP = new ConfigEntry<>(0.3D, d -> d >= 0.1 && d <= 10.0);
}
