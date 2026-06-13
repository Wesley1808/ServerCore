package me.wesley1808.servercore.common.interfaces;

import me.wesley1808.servercore.common.config.Config;
import me.wesley1808.servercore.common.config.data.mob_spawning.MobSpawnEntry;
import net.minecraft.world.entity.MobCategory;

public interface IMobCategory {
    int servercore$getSpawnInterval();

    int servercore$getOriginalCapacity();

    void servercore$modifyCapacity(double modifier);

    void servercore$modifySpawningConfig(MobSpawnEntry config);

    static IMobCategory of(MobCategory category) {
        return (IMobCategory) (Object) category;
    }

    static int getSpawnInterval(MobCategory category) {
        return IMobCategory.of(category).servercore$getSpawnInterval();
    }

    static int getOriginalCapacity(MobCategory category) {
        return IMobCategory.of(category).servercore$getOriginalCapacity();
    }

    static void modifyCapacity(MobCategory category, double modifier) {
        IMobCategory.of(category).servercore$modifyCapacity(modifier);
    }

    static void reload() {
        for (MobSpawnEntry entry : Config.get().mobSpawning().categories()) {
            IMobCategory.modify(entry);
        }
    }

    private static void modify(MobSpawnEntry config) {
        IMobCategory.of(config.category()).servercore$modifySpawningConfig(config);
    }
}
