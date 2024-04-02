package me.wesley1808.servercore.common.config.data.mob_spawning;

import space.arim.dazzleconf.annote.ConfDefault.DefaultBoolean;
import space.arim.dazzleconf.annote.ConfDefault.DefaultDouble;
import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.annote.NumericRange;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter.Order;

public interface EnforcedMobcap {
    @Order(1)
    @ConfKey("enforce-mobcap")
    @DefaultBoolean(false)
    boolean enforcesMobcap();

    @Order(2)
    @ConfKey("mobcap-modifier")
    @DefaultDouble(1.5)
    @NumericRange(min = 0)
    double mobcapModifier();
}
