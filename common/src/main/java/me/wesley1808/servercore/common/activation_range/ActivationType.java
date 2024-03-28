package me.wesley1808.servercore.common.activation_range;

import me.wesley1808.servercore.common.config.legacy.ActivationRangeConfig;
import net.minecraft.world.phys.AABB;

import java.util.function.IntSupplier;

public enum ActivationType {
    VILLAGER(ActivationRangeConfig.VILLAGER_ACTIVATION_RANGE::get, ActivationRangeConfig.VILLAGER_TICK_INTERVAL::get, Wakeup.VILLAGER),
    ZOMBIE(ActivationRangeConfig.ZOMBIE_ACTIVATION_RANGE::get, ActivationRangeConfig.ZOMBIE_TICK_INTERVAL::get, true, false, Wakeup.MONSTER),
    MONSTER(ActivationRangeConfig.MONSTER_ACTIVATION_RANGE::get, ActivationRangeConfig.MONSTER_TICK_INTERVAL::get, true, false, Wakeup.MONSTER),
    MONSTER_BELOW(ActivationRangeConfig.MONSTER_ACTIVATION_RANGE::get, ActivationRangeConfig.MONSTER_TICK_INTERVAL::get, true, true, Wakeup.MONSTER),
    NEUTRAL(ActivationRangeConfig.NEUTRAL_ACTIVATION_RANGE::get, ActivationRangeConfig.NEUTRAL_TICK_INTERVAL::get, Wakeup.CREATURE),
    ANIMAL(ActivationRangeConfig.ANIMAL_ACTIVATION_RANGE::get, ActivationRangeConfig.ANIMAL_TICK_INTERVAL::get, Wakeup.CREATURE),
    WATER(ActivationRangeConfig.WATER_ACTIVATION_RANGE::get, ActivationRangeConfig.WATER_TICK_INTERVAL::get, Wakeup.WATER),
    FLYING(ActivationRangeConfig.FLYING_ACTIVATION_RANGE::get, ActivationRangeConfig.FLYING_TICK_INTERVAL::get, true, false, Wakeup.FLYING),
    RAIDER(ActivationRangeConfig.RAIDER_ACTIVATION_RANGE::get, ActivationRangeConfig.RAIDER_TICK_INTERVAL::get, true, false, Wakeup.MONSTER),
    MISC(ActivationRangeConfig.MISC_ACTIVATION_RANGE::get, ActivationRangeConfig.MISC_TICK_INTERVAL::get);

    public final Wakeup wakeup;
    public final IntSupplier activationRange;
    public final IntSupplier tickInterval;
    public final boolean extraHeightUp;
    public final boolean extraHeightDown;
    public AABB boundingBox = new AABB(0, 0, 0, 0, 0, 0);

    ActivationType(IntSupplier activationRange, IntSupplier tickInterval, boolean extraHeightUp, boolean extraHeightDown, Wakeup wakeup) {
        this.activationRange = activationRange;
        this.tickInterval = tickInterval;
        this.extraHeightUp = extraHeightUp;
        this.extraHeightDown = extraHeightDown;
        this.wakeup = wakeup;
    }

    ActivationType(IntSupplier activationRange, IntSupplier tickInterval, boolean extraHeightUp, boolean extraHeightDown) {
        this(activationRange, tickInterval, extraHeightUp, extraHeightDown, null);
    }

    ActivationType(IntSupplier activationRange, IntSupplier tickInterval, Wakeup wakeup) {
        this(activationRange, tickInterval, false, false, wakeup);
    }

    ActivationType(IntSupplier activationRange, IntSupplier tickInterval) {
        this(activationRange, tickInterval, false, false);
    }
}