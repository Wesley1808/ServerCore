package me.wesley1808.servercore.common.interfaces;

import me.wesley1808.servercore.common.config.Config;
import me.wesley1808.servercore.common.config.data.mob_spawning.MobSpawnEntry;
import net.minecraft.world.entity.MobCategory;

public interface IMobCategory {
    int servercore$getSpawnInterval();

    int servercore$getOriginalCapacity();

    void servercore$modifyCapacity(double modifier);

    void servercore$modifySpawningConfig(int max, int interval);

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
            IMobCategory.modify(entry.category(), entry.capacity(), entry.spawnInterval());
        }
    }

    private static void modify(MobCategory category, int max, int interval) {
        IMobCategory.of(category).servercore$modifySpawningConfig(max, interval);
    }
}
