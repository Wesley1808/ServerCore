package me.wesley1808.servercore.common.config.data.mob_spawning;

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
}
