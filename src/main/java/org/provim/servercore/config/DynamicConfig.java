package org.provim.servercore.config;

import com.moandjiezana.toml.Toml;

public final class DynamicConfig {
    public boolean enabled;
    public int maxChunkTickDistance;
    public int minChunkTickDistance;
    public int maxSimulationDistance;
    public int minSimulationDistance;
    public int maxViewDistance;
    public int minViewDistance;
    public double maxMobcap;
    public double minMobcap;

    public DynamicConfig(Toml defaultToml) {
        final Toml toml = defaultToml.getTable("dynamic");
        this.enabled = toml.getBoolean("enabled", false);
        this.maxChunkTickDistance = Math.toIntExact(toml.getLong("max_chunk_tick_distance", 10L));
        this.minChunkTickDistance = Math.toIntExact(toml.getLong("min_chunk_tick_distance", 2L));
        this.maxSimulationDistance = Math.toIntExact(toml.getLong("max_simulation_distance", 10L));
        this.minSimulationDistance = Math.toIntExact(toml.getLong("min_simulation_distance", 2L));
        this.maxViewDistance = Math.toIntExact(toml.getLong("max_view_distance", 10L));
        this.minViewDistance = Math.toIntExact(toml.getLong("min_view_distance", 2L));
        this.maxMobcap = toml.getDouble("max_mobcap", 1.0D);
        this.minMobcap = toml.getDouble("min_mobcap", 0.3D);
    }
}