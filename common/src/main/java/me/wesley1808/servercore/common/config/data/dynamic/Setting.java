package me.wesley1808.servercore.common.config.data.dynamic;

import me.wesley1808.servercore.common.dynamic.DynamicSetting;
import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.annote.IntegerRange;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter.Order;

public interface Setting {
    @Order(1)
    @ConfKey("setting")
    DynamicSetting setting();

    @Order(2)
    @ConfKey("max")
    @IntegerRange(min = 1)
    int max();

    @Order(3)
    @ConfKey("min")
    @IntegerRange(min = 1)
    int min();

    @Order(4)
    @ConfKey("increment")
    @IntegerRange(min = 1)
    int increment();

    static Setting of(DynamicSetting setting, int max, int min, int increment) {
        return new Setting() {
            @Override
            public DynamicSetting setting() {
                return setting;
            }

            @Override
            public int max() {
                return max;
            }

            @Override
            public int min() {
                return min;
            }

            @Override
            public int increment() {
                return increment;
            }
        };
    }
}
