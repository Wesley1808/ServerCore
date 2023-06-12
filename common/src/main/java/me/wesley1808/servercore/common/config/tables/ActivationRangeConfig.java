package me.wesley1808.servercore.common.config.tables;

import me.wesley1808.servercore.common.config.ConfigEntry;

public class ActivationRangeConfig {
    public static final ConfigEntry<Boolean> ENABLED = new ConfigEntry<>(false, "(Default = false) Enables this feature.");

    public static final ConfigEntry<Boolean> USE_VERTICAL_RANGE = new ConfigEntry<>(
            false, """
            (Default = false) Enables vertical range checks. By default, activation ranges only work horizontally.
            This can greatly improve performance on taller worlds, but might break a few very specific ai-based mobfarms."""
    );

    public static final ConfigEntry<Boolean> SKIP_NON_IMMUNE = new ConfigEntry<>(
            false, """
            (Default = false) Skips 1/4th of entity ticks whilst not immune.
            This affects entities that are within the activation range, but not immune (for example by falling or being in water)."""
    );

    public static final ConfigEntry<Boolean> VILLAGER_TICK_PANIC = new ConfigEntry<>(
            true, "(Default = true) Allows villagers to tick regardless of the activation range when panicking."
    );

    public static final ConfigEntry<Boolean> VILLAGER_TICK_ALWAYS = new ConfigEntry<>(
            false, "(Default = false) Allows villagers to tick regardless of the activation range."
    );

    public static final ConfigEntry<Integer> VILLAGER_WORK_IMMUNITY_AFTER = new ConfigEntry<>(
            20, "(Default = 20) The time in seconds that a villager needs to be inactive for before obtaining work immunity (if it has work tasks)."
    );

    public static final ConfigEntry<Integer> VILLAGER_WORK_IMMUNITY_FOR = new ConfigEntry<>(
            20, "(Default = 20) The amount of ticks an inactive villager will wake up for when it has work immunity."
    );

    public static final ConfigEntry<Integer> VILLAGER_ACTIVATION_RANGE = new ConfigEntry<>(
            16, """
            Activation Range = The range an entity is required to be in from a player to be activated.
            Tick Interval = The interval between 'active' ticks whilst the entity is inactive. Negative values will disable these active ticks.
            Wakeup Max = The maximum amount of entities in the same group and world that are allowed to be awakened at the same time.
            Wakeup Interval = The interval between inactive entity wake ups in seconds.
            Activation range settings for villagers."""
    );

    public static final ConfigEntry<Integer> VILLAGER_TICK_INTERVAL = new ConfigEntry<>(20);
    public static final ConfigEntry<Integer> VILLAGER_WAKEUP_MAX = new ConfigEntry<>(4);
    public static final ConfigEntry<Integer> VILLAGER_WAKEUP_INTERVAL = new ConfigEntry<>(30);

    public static final ConfigEntry<Integer> MONSTER_ACTIVATION_RANGE = new ConfigEntry<>(32, "Activation range settings for monsters.");
    public static final ConfigEntry<Integer> MONSTER_TICK_INTERVAL = new ConfigEntry<>(20);
    public static final ConfigEntry<Integer> MONSTER_WAKEUP_MAX = new ConfigEntry<>(8);
    public static final ConfigEntry<Integer> MONSTER_WAKEUP_INTERVAL = new ConfigEntry<>(20);

    public static final ConfigEntry<Integer> ANIMAL_ACTIVATION_RANGE = new ConfigEntry<>(16, "Activation range settings for animals.");
    public static final ConfigEntry<Integer> ANIMAL_TICK_INTERVAL = new ConfigEntry<>(20);
    public static final ConfigEntry<Integer> ANIMAL_WAKEUP_MAX = new ConfigEntry<>(4);
    public static final ConfigEntry<Integer> ANIMAL_WAKEUP_INTERVAL = new ConfigEntry<>(60);

    public static final ConfigEntry<Integer> FLYING_ACTIVATION_RANGE = new ConfigEntry<>(48, "Activation range settings for flying mobs.");
    public static final ConfigEntry<Integer> FLYING_TICK_INTERVAL = new ConfigEntry<>(20);
    public static final ConfigEntry<Integer> FLYING_WAKEUP_MAX = new ConfigEntry<>(8);
    public static final ConfigEntry<Integer> FLYING_WAKEUP_INTERVAL = new ConfigEntry<>(10);

    public static final ConfigEntry<Integer> WATER_ACTIVATION_RANGE = new ConfigEntry<>(16, "Activation range settings for water mobs.");
    public static final ConfigEntry<Integer> WATER_TICK_INTERVAL = new ConfigEntry<>(20);

    public static final ConfigEntry<Integer> NEUTRAL_ACTIVATION_RANGE = new ConfigEntry<>(24, "Activation range settings for neutral mobs.");
    public static final ConfigEntry<Integer> NEUTRAL_TICK_INTERVAL = new ConfigEntry<>(20);

    public static final ConfigEntry<Integer> ZOMBIE_ACTIVATION_RANGE = new ConfigEntry<>(16, "Activation range settings for zombies.");
    public static final ConfigEntry<Integer> ZOMBIE_TICK_INTERVAL = new ConfigEntry<>(20);

    public static final ConfigEntry<Integer> RAIDER_ACTIVATION_RANGE = new ConfigEntry<>(48, "Activation range settings for raider mobs.");
    public static final ConfigEntry<Integer> RAIDER_TICK_INTERVAL = new ConfigEntry<>(20);

    public static final ConfigEntry<Integer> MISC_ACTIVATION_RANGE = new ConfigEntry<>(16, "Activation range settings for miscellaneous entities.");
    public static final ConfigEntry<Integer> MISC_TICK_INTERVAL = new ConfigEntry<>(20);
}
