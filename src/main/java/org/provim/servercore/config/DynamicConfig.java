package org.provim.servercore.config;

import com.electronwill.nightconfig.core.CommentedConfig;

public final class DynamicConfig {
    private static final String ENABLED = "enabled";
    private static final String MAX_CHUNK_TICK_DISTANCE = "max_chunk_tick_distance";
    private static final String MIN_CHUNK_TICK_DISTANCE = "min_chunk_tick_distance";
    private static final String MAX_SIMULATION_DISTANCE = "max_simulation_distance";
    private static final String MIN_SIMULATION_DISTANCE = "min_simulation_distance";
    private static final String MAX_VIEW_DISTANCE = "max_view_distance";
    private static final String MIN_VIEW_DISTANCE = "min_view_distance";
    private static final String MAX_MOBCAP = "max_mobcap";
    private static final String MIN_MOBCAP = "min_mobcap";

    public boolean enabled;
    public int maxChunkTickDistance;
    public int minChunkTickDistance;
    public int maxSimulationDistance;
    public int minSimulationDistance;
    public int maxViewDistance;
    public int minViewDistance;
    public double maxMobcap;
    public double minMobcap;

    public DynamicConfig(CommentedConfig config) {
        config.setComment(ENABLED, " (Default = false) Enables this feature.");
        this.enabled = config.getOrElse(ENABLED, false);

        config.setComment(MAX_CHUNK_TICK_DISTANCE, " (Default = [Max: 10, Min: 2]) Distance in which random ticks and mobspawning can happen.");
        this.maxChunkTickDistance = config.getIntOrElse(MAX_CHUNK_TICK_DISTANCE, 10);
        this.minChunkTickDistance = config.getIntOrElse(MIN_CHUNK_TICK_DISTANCE, 2);

        config.setComment(MAX_SIMULATION_DISTANCE, " (Default = [Max: 10, Min: 2]) Distance in which the world will tick, similar to no-tick-vd.");
        this.maxSimulationDistance = config.getIntOrElse(MAX_SIMULATION_DISTANCE, 10);
        this.minSimulationDistance = config.getIntOrElse(MIN_SIMULATION_DISTANCE, 2);

        config.setComment(MAX_VIEW_DISTANCE, " (Default = [Max: 10, Min: 2]) Distance in which the world will render.");
        this.maxViewDistance = config.getIntOrElse(MAX_VIEW_DISTANCE, 10);
        this.minViewDistance = config.getIntOrElse(MIN_VIEW_DISTANCE, 2);

        config.setComment(MAX_MOBCAP, " (Default = [Max: 1.0, Min: 0.3]) Global multiplier that decides the percentage of the mobcap to be used.");
        this.maxMobcap = config.getOrElse(MAX_MOBCAP, 1.0D);
        this.minMobcap = config.getOrElse(MIN_MOBCAP, 0.3D);
    }

    public void save(CommentedConfig config) {
        config.set(ENABLED, this.enabled);
        config.set(MAX_CHUNK_TICK_DISTANCE, this.maxChunkTickDistance);
        config.set(MIN_CHUNK_TICK_DISTANCE, this.minChunkTickDistance);
        config.set(MAX_SIMULATION_DISTANCE, this.maxSimulationDistance);
        config.set(MIN_SIMULATION_DISTANCE, this.minSimulationDistance);
        config.set(MAX_VIEW_DISTANCE, this.maxViewDistance);
        config.set(MIN_VIEW_DISTANCE, this.minViewDistance);
        config.set(MAX_MOBCAP, this.maxMobcap);
        config.set(MIN_MOBCAP, this.minMobcap);
    }
}