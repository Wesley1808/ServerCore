package me.wesley1808.servercore.common.config.data.dynamic;

import me.wesley1808.servercore.common.dynamic.DynamicSetting;
import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.annote.IntegerRange;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter.Order;

public interface Setting {
    @Order(1)
    @ConfKey("setting")
    DynamicSetting dynamicSetting();

    @Order(2)
    @ConfKey("enabled")
    boolean enabled();

    @Order(3)
    @ConfKey("max")
    @IntegerRange(min = 1)
    int max();

    @Order(4)
    @ConfKey("min")
    @IntegerRange(min = 1)
    int min();

    @Order(5)
    @ConfKey("increment")
    @IntegerRange(min = 1)
    int increment();

    @Order(6)
    @ConfKey("interval")
    @IntegerRange(min = 1)
    int interval();
}
