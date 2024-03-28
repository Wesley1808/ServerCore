package me.wesley1808.servercore.common.interfaces;

import me.wesley1808.servercore.common.config.legacy.MobSpawnConfig;
import me.wesley1808.servercore.common.dynamic.DynamicManager;
import me.wesley1808.servercore.common.dynamic.DynamicSetting;
import net.minecraft.world.entity.MobCategory;

public interface IMobCategory {
    int servercore$getSpawnInterval();

    void servercore$modifyCapacity(double modifier);

    void servercore$modifySpawningConfig(int max, int interval);

    static IMobCategory of(MobCategory category) {
        return (IMobCategory) (Object) category;
    }

    static int getSpawnInterval(MobCategory category) {
        return IMobCategory.of(category).servercore$getSpawnInterval();
    }

    static void modifyCapacity(MobCategory category, double modifier) {
        IMobCategory.of(category).servercore$modifyCapacity(modifier);
    }

    static void reload() {
        modify(MobCategory.MONSTER,
                MobSpawnConfig.MONSTER_MOBCAP.get(),
                MobSpawnConfig.MONSTER_SPAWN_INTERVAL.get()
        );

        modify(MobCategory.CREATURE,
                MobSpawnConfig.CREATURE_MOBCAP.get(),
                MobSpawnConfig.CREATURE_SPAWN_INTERVAL.get()
        );

        modify(MobCategory.AMBIENT,
                MobSpawnConfig.AMBIENT_MOBCAP.get(),
                MobSpawnConfig.AMBIENT_SPAWN_INTERVAL.get()
        );

        modify(MobCategory.AXOLOTLS,
                MobSpawnConfig.AXOLOTLS_MOBCAP.get(),
                MobSpawnConfig.AXOLOTLS_SPAWN_INTERVAL.get()
        );

        modify(MobCategory.UNDERGROUND_WATER_CREATURE,
                MobSpawnConfig.UNDERGROUND_WATER_CREATURE_MOBCAP.get(),
                MobSpawnConfig.UNDERGROUND_WATER_CREATURE_SPAWN_INTERVAL.get()
        );

        modify(MobCategory.WATER_CREATURE,
                MobSpawnConfig.WATER_CREATURE_MOBCAP.get(),
                MobSpawnConfig.WATER_CREATURE_SPAWN_INTERVAL.get()
        );

        modify(MobCategory.WATER_AMBIENT,
                MobSpawnConfig.WATER_AMBIENT_MOBCAP.get(),
                MobSpawnConfig.WATER_AMBIENT_SPAWN_INTERVAL.get()
        );

        DynamicManager.modifyMobcaps(DynamicSetting.MOBCAP.get());
    }

    private static void modify(MobCategory category, int max, int interval) {
        IMobCategory.of(category).servercore$modifySpawningConfig(max, interval);
    }
}
