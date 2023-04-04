package me.wesley1808.servercore.common.config.tables;

import me.wesley1808.servercore.common.config.ConfigEntry;

public class EntityLimitConfig {
    public static final ConfigEntry<Boolean> ENABLED = new ConfigEntry<>(false, "(Default = false) Enables this feature.");

    public static final ConfigEntry<Integer> VILLAGER_COUNT = new ConfigEntry<>(
            24, "(Default = [Villager: 24, Animals: 32]) Maximum count before stopping entities of the same type from breeding."
    );

    public static final ConfigEntry<Integer> ANIMAL_COUNT = new ConfigEntry<>(32);

    public static final ConfigEntry<Integer> VILLAGER_RANGE = new ConfigEntry<>(
            64, "(Default = [Villager: 64, Animals: 64]) The range it will check for entities of the same type."
    );

    public static final ConfigEntry<Integer> ANIMAL_RANGE = new ConfigEntry<>(64);
}
