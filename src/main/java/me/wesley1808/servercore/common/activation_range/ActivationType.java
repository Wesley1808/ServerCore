package me.wesley1808.servercore.common.activation_range;

import me.wesley1808.servercore.common.config.tables.ActivationRangeConfig;
import net.minecraft.world.phys.AABB;

import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;

public enum ActivationType {
    VILLAGER(ActivationRangeConfig.VILLAGER_ACTIVATION_RANGE::get, ActivationRangeConfig.VILLAGER_TICK_INACTIVE::get, Wakeup.VILLAGER),
    ZOMBIE(ActivationRangeConfig.ZOMBIE_ACTIVATION_RANGE::get, ActivationRangeConfig.ZOMBIE_TICK_INACTIVE::get, true, false, Wakeup.MONSTER),
    MONSTER(ActivationRangeConfig.MONSTER_ACTIVATION_RANGE::get, ActivationRangeConfig.MONSTER_TICK_INACTIVE::get, true, false, Wakeup.MONSTER),
    MONSTER_BELOW(ActivationRangeConfig.MONSTER_ACTIVATION_RANGE::get, ActivationRangeConfig.MONSTER_TICK_INACTIVE::get, true, true, Wakeup.MONSTER),
    NEUTRAL(ActivationRangeConfig.NEUTRAL_ACTIVATION_RANGE::get, ActivationRangeConfig.NEUTRAL_TICK_INACTIVE::get, Wakeup.ANIMAL),
    ANIMAL(ActivationRangeConfig.ANIMAL_ACTIVATION_RANGE::get, ActivationRangeConfig.ANIMAL_TICK_INACTIVE::get, Wakeup.ANIMAL),
    WATER(ActivationRangeConfig.WATER_ACTIVATION_RANGE::get, ActivationRangeConfig.WATER_TICK_INACTIVE::get),
    FLYING(ActivationRangeConfig.FLYING_ACTIVATION_RANGE::get, ActivationRangeConfig.FLYING_TICK_INACTIVE::get, true, false, Wakeup.FLYING),
    RAIDER(ActivationRangeConfig.RAIDER_ACTIVATION_RANGE::get, ActivationRangeConfig.RAIDER_TICK_INACTIVE::get, true, false, Wakeup.MONSTER),
    MISC(ActivationRangeConfig.MISC_ACTIVATION_RANGE::get, ActivationRangeConfig.MISC_TICK_INACTIVE::get);

    public final Wakeup wakeup;
    public final IntSupplier activationRange;
    public final BooleanSupplier tickInactive;
    public final boolean extraHeightUp;
    public final boolean extraHeightDown;
    public AABB boundingBox = new AABB(0, 0, 0, 0, 0, 0);

    ActivationType(IntSupplier activationRange, BooleanSupplier tickInactive, boolean extraHeightUp, boolean extraHeightDown, Wakeup wakeup) {
        this.activationRange = activationRange;
        this.tickInactive = tickInactive;
        this.extraHeightUp = extraHeightUp;
        this.extraHeightDown = extraHeightDown;
        this.wakeup = wakeup;
    }

    ActivationType(IntSupplier activationRange, BooleanSupplier tickInactive, boolean extraHeightUp, boolean extraHeightDown) {
        this(activationRange, tickInactive, extraHeightUp, extraHeightDown, null);
    }

    ActivationType(IntSupplier activationRange, BooleanSupplier tickInactive, Wakeup wakeup) {
        this(activationRange, tickInactive, false, false, wakeup);
    }

    ActivationType(IntSupplier activationRange, BooleanSupplier tickInactive) {
        this(activationRange, tickInactive, false, false);
    }

    public enum Wakeup {
        MONSTER(ActivationRangeConfig.MONSTER_WAKEUP_INTERVAL::get, ActivationRangeConfig.MONSTER_WAKEUP_MAX::get),
        VILLAGER(ActivationRangeConfig.VILLAGER_WAKEUP_INTERVAL::get, ActivationRangeConfig.VILLAGER_WAKEUP_MAX::get),
        ANIMAL(ActivationRangeConfig.ANIMAL_WAKEUP_INTERVAL::get, ActivationRangeConfig.ANIMAL_WAKEUP_MAX::get),
        FLYING(ActivationRangeConfig.FLYING_WAKEUP_INTERVAL::get, ActivationRangeConfig.FLYING_WAKEUP_MAX::get);

        public final IntSupplier interval;
        public final IntSupplier max;

        Wakeup(IntSupplier interval, IntSupplier max) {
            this.interval = interval;
            this.max = max;
        }
    }
}