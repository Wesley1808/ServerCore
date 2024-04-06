package me.wesley1808.servercore.common.config.data.mob_spawning;

import space.arim.dazzleconf.annote.ConfDefault.DefaultBoolean;
import space.arim.dazzleconf.annote.ConfDefault.DefaultInteger;
import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.annote.NumericRange;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter.Order;

public interface EnforcedMobcap {
    @Order(1)
    @ConfKey("enforce-mobcap")
    @DefaultBoolean(false)
    boolean enforcesMobcap();

    @Order(2)
    @ConfKey("additional-capacity")
    @DefaultInteger(32)
    @NumericRange(min = 0)
    int additionalCapacity();
}
