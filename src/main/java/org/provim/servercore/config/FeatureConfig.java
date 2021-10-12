package org.provim.servercore.config;

import com.electronwill.nightconfig.core.CommentedConfig;

public final class FeatureConfig {
    private static final String DISABLE_SPAWN_CHUNKS = "disable_spawn_chunks";
    private static final String FAST_XP_MERGING = "fast_xp_merging";
    private static final String USE_DISTANCE_MAP = "use_distance_map";
    private static final String LOBOTOMIZE_VILLAGERS = "lobotomize_villagers";
    private static final String LOBOTOMIZED_TICK_INTERVAL = "lobotomized_tick_interval";
    private static final String AUTO_SAVE_INTERVAL = "autosave_interval_minutes";
    private static final String ITEM_MERGE_RADIUS = "item_merge_radius";
    private static final String XP_MERGE_RADIUS = "xp_merge_radius";

    public boolean disableSpawnChunks;
    public boolean fastXpMerging;
    public boolean useDistanceMap;
    public boolean lobotomizeVillagers;
    public int lobotomizedTickInterval;
    public int autoSaveInterval;
    public double xpMergeRadius;
    public double itemMergeRadius;

    public FeatureConfig(CommentedConfig config) {
        config.setComment(DISABLE_SPAWN_CHUNKS, " (Default = false) Stops the server from loading spawn chunks.");
        this.disableSpawnChunks = config.getOrElse(DISABLE_SPAWN_CHUNKS, false);

        config.setComment(FAST_XP_MERGING, " (Default = false) Allows xp orbs to merge with others that have different experience amounts.\n This will also allow players to pickup xp much faster.");
        this.fastXpMerging = config.getOrElse(FAST_XP_MERGING, false);

        config.setComment(USE_DISTANCE_MAP, " (Default = false) Optimizes vanilla's per-player mobspawning by using PaperMC's PlayerMobDistanceMap.\n Note: this might slightly increase memory usage.");
        this.useDistanceMap = config.getOrElse(USE_DISTANCE_MAP, false);

        config.setComment(LOBOTOMIZE_VILLAGERS, " (Default = false) Makes villagers tick less often if they are stuck in a 1x1 space.");
        this.lobotomizeVillagers = config.getOrElse(LOBOTOMIZE_VILLAGERS, false);

        config.setComment(LOBOTOMIZED_TICK_INTERVAL, " (Default = 20) Decides the interval in between villager ticks when lobotomized.");
        this.lobotomizedTickInterval = config.getIntOrElse(LOBOTOMIZED_TICK_INTERVAL, 20);

        config.setComment(AUTO_SAVE_INTERVAL, " (Default = 5) The amount of minutes in between auto-save intervals when /save-on is active.");
        this.autoSaveInterval = config.getIntOrElse(AUTO_SAVE_INTERVAL, 5);

        config.setComment(ITEM_MERGE_RADIUS, " (Default = 0.5) Decides the radius in blocks that items / xp will merge at.");
        this.itemMergeRadius = config.getOrElse(ITEM_MERGE_RADIUS, 0.5D);
        this.xpMergeRadius = config.getOrElse(XP_MERGE_RADIUS, 0.5D);
    }

    public void save(CommentedConfig config) {
        config.set(DISABLE_SPAWN_CHUNKS, this.disableSpawnChunks);
        config.set(FAST_XP_MERGING, this.fastXpMerging);
        config.set(USE_DISTANCE_MAP, this.useDistanceMap);
        config.set(LOBOTOMIZE_VILLAGERS, this.lobotomizeVillagers);
        config.set(LOBOTOMIZED_TICK_INTERVAL, this.lobotomizedTickInterval);
        config.set(AUTO_SAVE_INTERVAL, this.autoSaveInterval);
        config.set(ITEM_MERGE_RADIUS, this.itemMergeRadius);
        config.set(XP_MERGE_RADIUS, this.xpMergeRadius);
    }
}
