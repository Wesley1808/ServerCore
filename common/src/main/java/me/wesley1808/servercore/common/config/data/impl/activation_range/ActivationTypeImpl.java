package me.wesley1808.servercore.common.config.data.impl.activation_range;

import me.wesley1808.servercore.common.config.data.activation_range.ActivationType;

public class ActivationTypeImpl implements ActivationType {
    private final int activationRange;
    private final int tickInterval;
    private final int wakeupInterval;
    private final boolean extraHeightUp;
    private final boolean extraHeightDown;

    public ActivationTypeImpl(ActivationType source) {
        this.activationRange = source.activationRange();
        this.tickInterval = source.tickInterval();
        this.wakeupInterval = source.wakeupInterval();
        this.extraHeightUp = source.extraHeightUp();
        this.extraHeightDown = source.extraHeightDown();
    }

    public ActivationTypeImpl(
            int activationRange,
            int tickInterval,
            int wakeupInterval,
            boolean extraHeightUp,
            boolean extraHeightDown
    ) {
        this.activationRange = activationRange;
        this.tickInterval = tickInterval;
        this.wakeupInterval = wakeupInterval;
        this.extraHeightUp = extraHeightUp;
        this.extraHeightDown = extraHeightDown;
    }

    @Override
    public int activationRange() {
        return this.activationRange;
    }

    @Override
    public int tickInterval() {
        return this.tickInterval;
    }

    @Override
    public int wakeupInterval() {
        return this.wakeupInterval;
    }

    @Override
    public boolean extraHeightUp() {
        return this.extraHeightUp;
    }

    @Override
    public boolean extraHeightDown() {
        return this.extraHeightDown;
    }
}
