package me.wesley1808.servercore.common.activation_range;

import me.wesley1808.servercore.common.config.legacy.ActivationRangeConfig;

import java.util.function.IntSupplier;

public enum Wakeup {
    MONSTER(ActivationRangeConfig.MONSTER_WAKEUP_INTERVAL::get, ActivationRangeConfig.MONSTER_WAKEUP_MAX::get),
    VILLAGER(ActivationRangeConfig.VILLAGER_WAKEUP_INTERVAL::get, ActivationRangeConfig.VILLAGER_WAKEUP_MAX::get),
    CREATURE(ActivationRangeConfig.ANIMAL_WAKEUP_INTERVAL::get, ActivationRangeConfig.ANIMAL_WAKEUP_MAX::get),
    WATER(ActivationRangeConfig.ANIMAL_WAKEUP_INTERVAL::get, ActivationRangeConfig.ANIMAL_WAKEUP_MAX::get),
    FLYING(ActivationRangeConfig.FLYING_WAKEUP_INTERVAL::get, ActivationRangeConfig.FLYING_WAKEUP_MAX::get);

    public final IntSupplier interval;
    public final IntSupplier max;

    Wakeup(IntSupplier interval, IntSupplier max) {
        this.interval = interval;
        this.max = max;
    }
}