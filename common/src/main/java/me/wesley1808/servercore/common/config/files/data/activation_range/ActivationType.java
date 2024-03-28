package me.wesley1808.servercore.common.config.files.data.activation_range;

import space.arim.dazzleconf.annote.ConfDefault.DefaultBoolean;
import space.arim.dazzleconf.annote.ConfDefault.DefaultInteger;
import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter.Order;

public interface ActivationType {
    @Order(1)
    @ConfKey("activation-range")
    @DefaultInteger(16)
    int activationRange();

    @Order(2)
    @ConfKey("tick-interval")
    @DefaultInteger(20)
    int tickInterval();

    @Order(3)
    @ConfKey("wakeup-interval")
    @DefaultInteger(-1)
    int wakeupInterval();

    @Order(4)
    @ConfKey("extra-height-up")
    @DefaultBoolean(false)
    boolean extraHeightUp();

    @Order(5)
    @ConfKey("extra-height-down")
    @DefaultBoolean(false)
    boolean extraHeightDown();
}
