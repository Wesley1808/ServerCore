package org.provim.servercore.config;

import com.moandjiezana.toml.Toml;

public final class FeatureConfig {
    public boolean disableSpawnChunks;
    public boolean fastXpMerging;
    public boolean useDistanceMap;
    public boolean lobotomizeVillagers;
    public int lobotomizedTickInterval;
    public int autoSaveInterval;
    public double xpMergeRadius;
    public double itemMergeRadius;

    public FeatureConfig(Toml toml) {
        this.disableSpawnChunks = toml.getBoolean("disable_spawn_chunks", false);
        this.fastXpMerging = toml.getBoolean("fast_xp_merging", false);
        this.useDistanceMap = toml.getBoolean("use_distance_map", false);
        this.lobotomizeVillagers = toml.getBoolean("lobotomize_villagers", false);
        this.lobotomizedTickInterval = Math.toIntExact(toml.getLong("lobotomized_tick_interval", 20L));
        this.autoSaveInterval = Math.toIntExact(toml.getLong("autosave_interval_minutes", 5L));
        this.xpMergeRadius = toml.getDouble("xp_merge_radius", 0.5D);
        this.itemMergeRadius = toml.getDouble("item_merge_radius", 0.5D);
    }
}