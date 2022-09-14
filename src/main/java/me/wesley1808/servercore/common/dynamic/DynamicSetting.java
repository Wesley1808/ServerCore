package me.wesley1808.servercore.common.dynamic;

import java.math.BigDecimal;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;

import static me.wesley1808.servercore.common.config.tables.DynamicConfig.*;

public enum DynamicSetting {
    VIEW_DISTANCE(
            VIEW_DISTANCE_UPDATE_RATE::get,
            VIEW_DISTANCE_INCREMENT::get,
            MIN_VIEW_DISTANCE::get,
            MAX_VIEW_DISTANCE::get,
            (value) -> DynamicManager.modifyViewDistance((int) value)
    ),

    SIMULATION_DISTANCE(
            UPDATE_RATE::get,
            SIMULATION_DISTANCE_INCREMENT::get,
            MIN_SIMULATION_DISTANCE::get,
            MAX_SIMULATION_DISTANCE::get,
            (value) -> DynamicManager.modifySimulationDistance((int) value)
    ),

    MOBCAP_MULTIPLIER(
            UPDATE_RATE::get,
            MOBCAP_INCREMENT::get,
            MIN_MOBCAP::get,
            MAX_MOBCAP::get,
            DynamicManager::modifyMobcaps
    ),

    CHUNK_TICK_DISTANCE(
            UPDATE_RATE::get,
            CHUNK_TICK_DISTANCE_INCREMENT::get,
            MIN_CHUNK_TICK_DISTANCE::get,
            MAX_CHUNK_TICK_DISTANCE::get
    );

    private final DoubleConsumer onChanged;
    private final DoubleSupplier increment;
    private final DoubleSupplier min;
    private final DoubleSupplier max;
    private final IntSupplier interval;
    private BigDecimal value;
    private double cachedValue;

    private DynamicSetting prev;
    private DynamicSetting next;

    DynamicSetting(IntSupplier interval, DoubleSupplier increment, DoubleSupplier min, DoubleSupplier max) {
        this(interval, increment, min, max, null);
    }

    DynamicSetting(IntSupplier interval, DoubleSupplier increment, DoubleSupplier min, DoubleSupplier max, DoubleConsumer onChanged) {
        this.interval = interval;
        this.onChanged = onChanged;
        this.increment = increment;
        this.min = min;
        this.max = max;
        this.set(max.getAsDouble(), false);
    }

    public void initialize(DynamicSetting prev, DynamicSetting next) {
        this.prev = prev;
        this.next = next;
    }

    public double get() {
        return this.cachedValue;
    }

    public boolean shouldRun(int count) {
        return count % this.interval.getAsInt() == 0;
    }

    public void set(double value, boolean triggerChanges) {
        this.set(BigDecimal.valueOf(value), triggerChanges);
    }

    public void set(BigDecimal value, boolean triggerChanges) {
        this.value = value;
        this.cachedValue = value.doubleValue();

        if (this.onChanged != null && triggerChanges) {
            this.onChanged.accept(this.cachedValue);
        }
    }

    public boolean modify(boolean increase) {
        BigDecimal value = this.newValue(increase);
        if (this.shouldModify(value)) {
            this.set(value, true);
            return true;
        }
        return false;
    }

    private boolean shouldModify(BigDecimal value) {
        int compared = value.compareTo(this.value);
        return compared != 0 && (this.next != null || this.prev != null)
                && (compared < 0 || (!this.isMaximum() && (this.next == null || this.next.isMaximum())))
                && (compared > 0 || (!this.isMinimum() && (this.prev == null || this.prev.isMinimum())));
    }

    private BigDecimal newValue(boolean increase) {
        BigDecimal increment = BigDecimal.valueOf(this.increment.getAsDouble());
        BigDecimal value;

        if (increase) {
            value = this.value.add(increment);
            double maximum = this.max.getAsDouble();
            if (value.doubleValue() > maximum) {
                return BigDecimal.valueOf(maximum);
            }
        } else {
            value = this.value.subtract(increment);
            double minimum = this.min.getAsDouble();
            if (value.doubleValue() < minimum) {
                return BigDecimal.valueOf(minimum);
            }
        }

        return value;
    }

    private boolean isMinimum() {
        return this.cachedValue <= this.min.getAsDouble();
    }

    private boolean isMaximum() {
        return this.cachedValue >= this.max.getAsDouble();
    }
}
