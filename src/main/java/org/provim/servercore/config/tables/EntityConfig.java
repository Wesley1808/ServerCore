package org.provim.servercore.config.tables;

import org.provim.servercore.config.ConfigEntry;

public final class EntityConfig {
    public static final ConfigEntry<Boolean> ENABLED = new ConfigEntry<>(false, " (Default = false) Enables this feature.");
    public static final ConfigEntry<Integer> VILLAGER_COUNT = new ConfigEntry<>(24, " (Default = [Villager: 24, Animals: 32]) Maximum count before stopping entities of the same argumentType from breeding.");
    public static final ConfigEntry<Integer> ANIMAL_COUNT = new ConfigEntry<>(32);
    public static final ConfigEntry<Integer> VILLAGER_RANGE = new ConfigEntry<>(64, " (Default = [Villager: 64, Animals: 64]) The range it will check for entities of the same argumentType.");
    public static final ConfigEntry<Integer> ANIMAL_RANGE = new ConfigEntry<>(64);
}
