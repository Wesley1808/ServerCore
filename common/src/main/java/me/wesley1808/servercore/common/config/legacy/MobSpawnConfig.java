package me.wesley1808.servercore.common.config.legacy;

import net.minecraft.world.entity.MobCategory;

public class MobSpawnConfig {

    public static final ConfigEntry<Integer> MONSTER_MOBCAP = new ConfigEntry<>(
            MobCategory.MONSTER.getMaxInstancesPerChunk(), (value) -> value >= 0 && value <= 1024,
            "The maximum amount of entities in the same category that can spawn near a player."
    );

    public static final ConfigEntry<Integer> CREATURE_MOBCAP = new ConfigEntry<>(
            MobCategory.CREATURE.getMaxInstancesPerChunk(), (value) -> value >= 0 && value <= 1024
    );

    public static final ConfigEntry<Integer> AMBIENT_MOBCAP = new ConfigEntry<>(
            MobCategory.AMBIENT.getMaxInstancesPerChunk(), (value) -> value >= 0 && value <= 1024
    );

    public static final ConfigEntry<Integer> AXOLOTLS_MOBCAP = new ConfigEntry<>(
            MobCategory.AXOLOTLS.getMaxInstancesPerChunk(), (value) -> value >= 0 && value <= 1024
    );

    public static final ConfigEntry<Integer> UNDERGROUND_WATER_CREATURE_MOBCAP = new ConfigEntry<>(
            MobCategory.UNDERGROUND_WATER_CREATURE.getMaxInstancesPerChunk(), (value) -> value >= 0 && value <= 1024
    );

    public static final ConfigEntry<Integer> WATER_CREATURE_MOBCAP = new ConfigEntry<>(
            MobCategory.WATER_CREATURE.getMaxInstancesPerChunk(), (value) -> value >= 0 && value <= 1024
    );

    public static final ConfigEntry<Integer> WATER_AMBIENT_MOBCAP = new ConfigEntry<>(
            MobCategory.WATER_AMBIENT.getMaxInstancesPerChunk(), (value) -> value >= 0 && value <= 1024
    );

    public static final ConfigEntry<Integer> MONSTER_SPAWN_INTERVAL = new ConfigEntry<>(
            defaultSpawnInterval(MobCategory.MONSTER), (value) -> value >= 1,
            "The interval between spawn attempts in ticks. Higher values mean less frequent spawn attempts."
    );

    public static final ConfigEntry<Integer> CREATURE_SPAWN_INTERVAL = new ConfigEntry<>(
            defaultSpawnInterval(MobCategory.CREATURE), (value) -> value >= 1
    );

    public static final ConfigEntry<Integer> AMBIENT_SPAWN_INTERVAL = new ConfigEntry<>(
            defaultSpawnInterval(MobCategory.AMBIENT), (value) -> value >= 1
    );

    public static final ConfigEntry<Integer> AXOLOTLS_SPAWN_INTERVAL = new ConfigEntry<>(
            defaultSpawnInterval(MobCategory.AXOLOTLS), (value) -> value >= 1
    );

    public static final ConfigEntry<Integer> UNDERGROUND_WATER_CREATURE_SPAWN_INTERVAL = new ConfigEntry<>(
            defaultSpawnInterval(MobCategory.UNDERGROUND_WATER_CREATURE), (value) -> value >= 1
    );

    public static final ConfigEntry<Integer> WATER_CREATURE_SPAWN_INTERVAL = new ConfigEntry<>(
            defaultSpawnInterval(MobCategory.WATER_CREATURE), (value) -> value >= 1
    );

    public static final ConfigEntry<Integer> WATER_AMBIENT_SPAWN_INTERVAL = new ConfigEntry<>(
            defaultSpawnInterval(MobCategory.WATER_AMBIENT), (value) -> value >= 1
    );

    private static int defaultSpawnInterval(MobCategory category) {
        return category.isPersistent() ? 400 : 1;
    }
}
