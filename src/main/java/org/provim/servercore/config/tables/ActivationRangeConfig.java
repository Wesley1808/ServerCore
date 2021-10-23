package org.provim.servercore.config.tables;

import org.provim.servercore.config.ConfigEntry;

public final class ActivationRangeConfig {
    public static final ConfigEntry<Boolean> ENABLED = new ConfigEntry<>(false, "(Default = false) Enables this feature.");
    public static final ConfigEntry<Boolean> USE_VERTICAL_RANGE = new ConfigEntry<>(false, "(Default = false) Enables vertical range checks. By default, activation ranges only work horizontally.");

    public static final ConfigEntry<Boolean> VILLAGER_TICK_ALWAYS = new ConfigEntry<>(false, "(Default = false) Allows villagers to tick regardless of the activation range.");
    public static final ConfigEntry<Boolean> VILLAGER_TICK_PANIC = new ConfigEntry<>(true, "(Default = true) Allows villagers to tick regardless of the activation range when panicking.");
    public static final ConfigEntry<Integer> VILLAGER_WORK_IMMUNITY_AFTER = new ConfigEntry<>(20, "(Default = 20) The time in seconds that a villager needs to be inactive for before obtaining work immunity (if it has work tasks).");
    public static final ConfigEntry<Integer> VILLAGER_WORK_IMMUNITY_FOR = new ConfigEntry<>(20, "(Default = 20) The amount of ticks an inactive villager will wake up for when it has work immunity.");

    public static final ConfigEntry<Integer> VILLAGER_RANGE = new ConfigEntry<>(16, "The range an entity is required to be in from a player to tick.");
    public static final ConfigEntry<Integer> MONSTER_RANGE = new ConfigEntry<>(32);
    public static final ConfigEntry<Integer> ANIMAL_RANGE = new ConfigEntry<>(16);
    public static final ConfigEntry<Integer> NEUTRAL_RANGE = new ConfigEntry<>(24);
    public static final ConfigEntry<Integer> WATER_RANGE = new ConfigEntry<>(16);
    public static final ConfigEntry<Integer> ZOMBIE_RANGE = new ConfigEntry<>(16);
    public static final ConfigEntry<Integer> FLYING_RANGE = new ConfigEntry<>(32);
    public static final ConfigEntry<Integer> RAIDER_RANGE = new ConfigEntry<>(48);
    public static final ConfigEntry<Integer> MISC_RANGE = new ConfigEntry<>(16);

    public static final ConfigEntry<Boolean> VILLAGER_TICK_INACTIVE = new ConfigEntry<>(false, "Decides if the entity is allowed to tick once a second whilst inactive.");
    public static final ConfigEntry<Boolean> MONSTER_TICK_INACTIVE = new ConfigEntry<>(true);
    public static final ConfigEntry<Boolean> ANIMAL_TICK_INACTIVE = new ConfigEntry<>(false);
    public static final ConfigEntry<Boolean> NEUTRAL_TICK_INACTIVE = new ConfigEntry<>(false);
    public static final ConfigEntry<Boolean> WATER_TICK_INACTIVE = new ConfigEntry<>(false);
    public static final ConfigEntry<Boolean> ZOMBIE_TICK_INACTIVE = new ConfigEntry<>(true);
    public static final ConfigEntry<Boolean> FLYING_TICK_INACTIVE = new ConfigEntry<>(true);
    public static final ConfigEntry<Boolean> RAIDER_TICK_INACTIVE = new ConfigEntry<>(true);
    public static final ConfigEntry<Boolean> MISC_TICK_INACTIVE = new ConfigEntry<>(false);

    public static final ConfigEntry<Integer> VILLAGER_WAKEUP_INTERVAL = new ConfigEntry<>(30, "The interval between inactive entity wake ups in seconds.");
    public static final ConfigEntry<Integer> MONSTER_WAKEUP_INTERVAL = new ConfigEntry<>(20);
    public static final ConfigEntry<Integer> ANIMAL_WAKEUP_INTERVAL = new ConfigEntry<>(60);
    public static final ConfigEntry<Integer> NEUTRAL_WAKEUP_INTERVAL = new ConfigEntry<>(30);
    public static final ConfigEntry<Integer> WATER_WAKEUP_INTERVAL = new ConfigEntry<>(60);
    public static final ConfigEntry<Integer> ZOMBIE_WAKEUP_INTERVAL = new ConfigEntry<>(60);
    public static final ConfigEntry<Integer> FLYING_WAKEUP_INTERVAL = new ConfigEntry<>(10);
    public static final ConfigEntry<Integer> RAIDER_WAKEUP_INTERVAL = new ConfigEntry<>(20);
    public static final ConfigEntry<Integer> MISC_WAKEUP_INTERVAL = new ConfigEntry<>(60);
}
