package org.provim.servercore.utils.activation_range;

import net.minecraft.util.math.Box;

import static org.provim.servercore.config.tables.ActivationRangeConfig.*;

public enum ActivationType {
    VILLAGER(VILLAGER_ACTIVATION_RANGE.get(), VILLAGER_WAKEUP_INTERVAL.get() * 20, 100, 0, 0),
    MONSTER(MONSTER_ACTIVATION_RANGE.get(), MONSTER_WAKEUP_INTERVAL.get() * 20, 100, 96, 0),
    MONSTER_BELOW(MONSTER_ACTIVATION_RANGE.get(), MONSTER_WAKEUP_INTERVAL.get() * 20, 100, 96, 96),
    ANIMAL(ANIMAL_ACTIVATION_RANGE.get(), ANIMAL_WAKEUP_INTERVAL.get() * 20, 100, 0, 0),
    NEUTRAL(NEUTRAL_ACTIVATION_RANGE.get(), NEUTRAL_WAKEUP_INTERVAL.get() * 20, 100, 96, 0),
    WATER(WATER_ACTIVATION_RANGE.get(), WATER_WAKEUP_INTERVAL.get() * 20, 100, 0, 0),
    ZOMBIE(ZOMBIE_ACTIVATION_RANGE.get(), ZOMBIE_WAKEUP_INTERVAL.get() * 20, 100, 96, 0),
    FLYING(FLYING_ACTIVATION_RANGE.get(), FLYING_WAKEUP_INTERVAL.get() * 20, 100, 96, 0),
    RAIDER(RAIDER_ACTIVATION_RANGE.get(), RAIDER_WAKEUP_INTERVAL.get() * 20, 100, 96, 0),
    MISC(MISC_ACTIVATION_RANGE.get(), MISC_WAKEUP_INTERVAL.get() * 20, 100, 0, 0);

    private final int activationRange;
    private final int wakeUpInterval;
    private final int wakeUpFor;
    private final int extraHeightAbove;
    private final int extraHeightBelow;
    private Box boundingBox = new Box(0, 0, 0, 0, 0, 0);

    ActivationType(int activationRange, int wakeUpInterval, int wakeUpFor, int extraHeightAbove, int extraHeightBelow) {
        this.activationRange = activationRange;
        this.wakeUpInterval = wakeUpInterval;
        this.wakeUpFor = wakeUpFor;
        this.extraHeightAbove = extraHeightAbove;
        this.extraHeightBelow = extraHeightBelow;
    }

    public int getActivationRange() {
        return this.activationRange;
    }

    public Box getBoundingBox() {
        return this.boundingBox;
    }

    public void setBoundingBox(Box box) {
        this.boundingBox = box;
    }

    public int getExtraHeightAbove() {
        return this.extraHeightAbove;
    }

    public int getExtraHeightBelow() {
        return this.extraHeightBelow;
    }

    public int getWakeUpInterval() {
        return this.wakeUpInterval;
    }

    public int getWakeUpFor() {
        return this.wakeUpFor;
    }
}