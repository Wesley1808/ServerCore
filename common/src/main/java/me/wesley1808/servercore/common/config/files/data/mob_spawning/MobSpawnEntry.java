package me.wesley1808.servercore.common.config.files.data.mob_spawning;

import net.minecraft.world.entity.MobCategory;
import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.annote.IntegerRange;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter.Order;

public interface MobSpawnEntry {
    @Order(1)
    @ConfKey("category")
    MobCategory category();

    @Order(2)
    @ConfKey("mobcap")
    @IntegerRange(min = 0)
    int capacity();

    @Order(3)
    @ConfKey("spawn-interval")
    @IntegerRange(min = 1)
    int spawnInterval();

    static MobSpawnEntry of(MobCategory category) {
        int spawnInterval = category.isPersistent() ? 400 : 1;
        int capacity = category.getMaxInstancesPerChunk();
        return new MobSpawnEntry() {
            @Override
            public MobCategory category() {
                return category;
            }

            @Override
            public int capacity() {
                return capacity;
            }

            @Override
            public int spawnInterval() {
                return spawnInterval;
            }
        };
    }
}
