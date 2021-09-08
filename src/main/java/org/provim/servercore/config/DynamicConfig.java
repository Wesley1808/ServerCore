package org.provim.servercore.config;

import com.moandjiezana.toml.Toml;

public final class DynamicConfig {
    public boolean enabled;
    public int maxTickDistance;
    public int minTickDistance;
    public int maxViewDistance;
    public int minViewDistance;
    public double maxMobcap;
    public double minMobcap;

    public DynamicConfig(Toml defaultToml) {
        final Toml toml = defaultToml.getTable("dynamic");
        this.enabled = toml.getBoolean("enabled", false);
        this.maxTickDistance = Math.toIntExact(toml.getLong("max_chunk_tick_distance", 10L));
        this.minTickDistance = Math.toIntExact(toml.getLong("min_chunk_tick_distance", 2L));
        this.maxViewDistance = Math.toIntExact(toml.getLong("max_view_distance", 10L));
        this.minViewDistance = Math.toIntExact(toml.getLong("min_view_distance", 2L));
        this.maxMobcap = toml.getDouble("max_mobcap", 1.0D);
        this.minMobcap = toml.getDouble("min_mobcap", 0.3D);
    }
}