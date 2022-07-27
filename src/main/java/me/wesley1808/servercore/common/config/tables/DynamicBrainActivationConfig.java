package me.wesley1808.servercore.common.config.tables;

import me.wesley1808.servercore.common.config.ConfigEntry;

public final class DynamicBrainActivationConfig {
    public static final ConfigEntry<Boolean> ENABLED = new ConfigEntry<>(false, "(Default = false) Enables this feature.");
    public static final ConfigEntry<Integer> START_DISTANCE = new ConfigEntry<>(12, "(Default = 12) How far an entity needs to be from a player to start being affected by dynamic brain activation.");
    public static final ConfigEntry<Integer> DISTANCE_MODIFIER = new ConfigEntry<>(8, "(Default = 8) Defines how much the distance from the player affects the tick frequency of the entity's pathfinding and behavior ticks.\nFrequency = (distanceToPlayer^2) / (2^value) -> The lower the value, the faster the tick frequency reduces.");
    public static final ConfigEntry<Integer> MAX_ACTIVATION_PRIORITY = new ConfigEntry<>(20, "(Default = 20) The maximum interval in ticks between the pathfinding and behavior ticks of an entity.");
}
