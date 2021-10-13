package org.provim.servercore.config;

public final class DynamicConfig {
    public ConfigEntry<Boolean> enabled = new ConfigEntry<>(false, " (Default = false) Enables this feature.");
    public ConfigEntry<Integer> maxChunkTickDistance = new ConfigEntry<>(10, " (Default = [Max: 10, Min: 2]) Distance in which random ticks and mobspawning can happen.");
    public ConfigEntry<Integer> minChunkTickDistance = new ConfigEntry<>(2);
    public ConfigEntry<Integer> maxSimulationDistance = new ConfigEntry<>(10, " (Default = [Max: 10, Min: 2]) Distance in which the world will tick, similar to no-tick-vd.");
    public ConfigEntry<Integer> minSimulationDistance = new ConfigEntry<>(2);
    public ConfigEntry<Integer> maxViewDistance = new ConfigEntry<>(10, " (Default = [Max: 10, Min: 2]) Distance in which the world will render.");
    public ConfigEntry<Integer> minViewDistance = new ConfigEntry<>(2);
    public ConfigEntry<Double> maxMobcap = new ConfigEntry<>(1.0D, " (Default = [Max: 1.0, Min: 0.3]) Global multiplier that decides the percentage of the mobcap to be used.");
    public ConfigEntry<Double> minMobcap = new ConfigEntry<>(0.3D);
}