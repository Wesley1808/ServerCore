package org.provim.servercore.utils;

import net.minecraft.util.math.Box;

import static org.provim.servercore.config.tables.ActivationRangeConfig.*;

public enum ActivationType {
    VILLAGER(VILLAGER_RANGE.get(), VILLAGER_TICK_INACTIVE.get(), VILLAGER_WAKEUP_INTERVAL.get() * 20, 100, 0, 0),
    MONSTER(MONSTER_RANGE.get(), MONSTER_TICK_INACTIVE.get(), MONSTER_WAKEUP_INTERVAL.get() * 20, 100, 96, 0),
    MONSTER_BELOW(MONSTER_RANGE.get(), MONSTER_TICK_INACTIVE.get(), MONSTER_WAKEUP_INTERVAL.get() * 20, 100, 96, 96),
    ANIMAL(ANIMAL_RANGE.get(), ANIMAL_TICK_INACTIVE.get(), ANIMAL_WAKEUP_INTERVAL.get() * 20, 100, 0, 0),
    NEUTRAL(NEUTRAL_RANGE.get(), NEUTRAL_TICK_INACTIVE.get(), NEUTRAL_WAKEUP_INTERVAL.get() * 20, 100, 96, 0),
    WATER(WATER_RANGE.get(), WATER_TICK_INACTIVE.get(), WATER_WAKEUP_INTERVAL.get() * 20, 100, 0, 0),
    ZOMBIE(ZOMBIE_RANGE.get(), ZOMBIE_TICK_INACTIVE.get(), ZOMBIE_WAKEUP_INTERVAL.get() * 20, 100, 96, 0),
    FLYING(FLYING_RANGE.get(), FLYING_TICK_INACTIVE.get(), FLYING_WAKEUP_INTERVAL.get() * 20, 100, 96, 0),
    RAIDER(RAIDER_RANGE.get(), RAIDER_TICK_INACTIVE.get(), RAIDER_WAKEUP_INTERVAL.get() * 20, 100, 96, 0),
    MISC(MISC_RANGE.get(), MISC_TICK_INACTIVE.get(), MISC_WAKEUP_INTERVAL.get() * 20, 100, 0, 0);

    private final int activationRange;
    private final int wakeUpInterval;
    private final int wakeUpFor;
    private final int extraHeightAbove;
    private final int extraHeightBelow;
    private final boolean shouldTickInactive;
    private Box boundingBox = new Box(0, 0, 0, 0, 0, 0);

    ActivationType(int activationRange, boolean shouldTickInactive, int wakeUpInterval, int wakeUpFor, int extraHeightAbove, int extraHeightBelow) {
        this.activationRange = activationRange;
        this.shouldTickInactive = shouldTickInactive;
        this.wakeUpInterval = wakeUpInterval;
        this.wakeUpFor = wakeUpFor;
        this.extraHeightAbove = extraHeightAbove;
        this.extraHeightBelow = extraHeightBelow;
    }

    public boolean shouldTickInactive() {
        return this.shouldTickInactive;
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