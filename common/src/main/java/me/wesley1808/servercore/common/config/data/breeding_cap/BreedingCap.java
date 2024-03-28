package me.wesley1808.servercore.common.config.data.breeding_cap;

import space.arim.dazzleconf.annote.ConfDefault.DefaultInteger;
import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.annote.IntegerRange;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter.Order;

public interface BreedingCap {
    @Order(1)
    @ConfKey("count")
    @DefaultInteger(32)
    int count();

    @Order(2)
    @ConfKey("range")
    @DefaultInteger(64)
    @IntegerRange(min = 1)
    int range();
}
