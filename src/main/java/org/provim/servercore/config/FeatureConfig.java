package org.provim.servercore.config;

import com.moandjiezana.toml.Toml;

public final class FeatureConfig {
    public boolean perPlayerSpawns;
    public boolean disableSpawnChunks;
    public boolean fastXpMerging;
    public boolean noChunkTick;
    public boolean lobotomizeVillagers;
    public int lobotomizedTickInterval;
    public int autoSaveInterval;
    public double xpMergeRadius;
    public double itemMergeRadius;

    public FeatureConfig(Toml defaultToml) {
        Toml toml = defaultToml.getTable("features");
        this.perPlayerSpawns = toml.getBoolean("per_player_spawns", false);
        this.disableSpawnChunks = toml.getBoolean("disable_spawn_chunks", false);
        this.fastXpMerging = toml.getBoolean("fast_xp_merging", false);
        this.noChunkTick = toml.getBoolean("no_chunk_tick", false);
        this.lobotomizeVillagers = toml.getBoolean("lobotomize_villagers", false);
        this.lobotomizedTickInterval = Math.toIntExact(toml.getLong("lobotomized_tick_interval", 20L));
        this.autoSaveInterval = Math.toIntExact(toml.getLong("autosave_interval_minutes", 5L));
        this.xpMergeRadius = toml.getDouble("xp_merge_radius", 0.5D);
        this.itemMergeRadius = toml.getDouble("item_merge_radius", 0.5D);
    }
}
