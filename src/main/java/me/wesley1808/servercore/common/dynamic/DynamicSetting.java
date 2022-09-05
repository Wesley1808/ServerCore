package me.wesley1808.servercore.common.dynamic;

import java.math.BigDecimal;
import java.util.function.Consumer;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

import static me.wesley1808.servercore.common.config.tables.DynamicConfig.*;

public enum DynamicSetting {
    VIEW_DISTANCE(VIEW_DISTANCE_UPDATE_RATE::get,
            () -> BigDecimal.valueOf(VIEW_DISTANCE_INCREMENT.get()),
            () -> BigDecimal.valueOf(MIN_VIEW_DISTANCE.get()),
            () -> BigDecimal.valueOf(MAX_VIEW_DISTANCE.get()),
            (value) -> DynamicManager.modifyViewDistance(value.intValue())
    ),

    SIMULATION_DISTANCE(UPDATE_RATE::get,
            () -> BigDecimal.valueOf(SIMULATION_DISTANCE_INCREMENT.get()),
            () -> BigDecimal.valueOf(MIN_SIMULATION_DISTANCE.get()),
            () -> BigDecimal.valueOf(MAX_SIMULATION_DISTANCE.get()),
            (value) -> DynamicManager.modifySimulationDistance(value.intValue())
    ),

    CHUNK_TICK_DISTANCE(UPDATE_RATE::get,
            () -> BigDecimal.valueOf(CHUNK_TICK_DISTANCE_INCREMENT.get()),
            () -> BigDecimal.valueOf(MIN_CHUNK_TICK_DISTANCE.get()),
            () -> BigDecimal.valueOf(MAX_CHUNK_TICK_DISTANCE.get())
    ),

    MOBCAP_MULTIPLIER(UPDATE_RATE::get,
            () -> BigDecimal.valueOf(MOBCAP_INCREMENT.get()),
            () -> BigDecimal.valueOf(MIN_MOBCAP.get()),
            () -> BigDecimal.valueOf(MAX_MOBCAP.get())
    );

    private final Consumer<BigDecimal> onChanged;
    private final Supplier<BigDecimal> min;
    private final Supplier<BigDecimal> max;
    private final Supplier<BigDecimal> increment;
    private final IntSupplier interval;
    private BigDecimal value;
    private double cachedValue;

    private DynamicSetting prev;
    private DynamicSetting next;

    DynamicSetting(IntSupplier interval, Supplier<BigDecimal> increment, Supplier<BigDecimal> min, Supplier<BigDecimal> max) {
        this(interval, increment, min, max, null);
    }

    DynamicSetting(IntSupplier interval, Supplier<BigDecimal> increment, Supplier<BigDecimal> min, Supplier<BigDecimal> max, Consumer<BigDecimal> onChanged) {
        this.interval = interval;
        this.onChanged = onChanged;
        this.increment = increment;
        this.min = min;
        this.max = max;
        this.set(max.get());
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

    public void set(double value) {
        this.set(BigDecimal.valueOf(value));
    }

    public void set(BigDecimal value) {
        this.value = value;
        this.cachedValue = value.doubleValue();
    }

    public boolean modify(boolean increase) {
        BigDecimal value = this.newValue(increase);
        if (this.shouldModify(value)) {
            this.set(value);

            if (this.onChanged != null) {
                this.onChanged.accept(value);
            }
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
        BigDecimal value;
        BigDecimal bound;
        if (increase) {
            value = this.value.add(this.increment.get());
            bound = this.max.get();
            if (value.compareTo(bound) > 0) {
                return bound;
            }
        } else {
            value = this.value.subtract(this.increment.get());
            bound = this.min.get();
            if (value.compareTo(bound) < 0) {
                return bound;
            }
        }

        return value;
    }

    private boolean isMinimum() {
        return this.cachedValue <= this.min.get().doubleValue();
    }

    private boolean isMaximum() {
        return this.cachedValue >= this.max.get().doubleValue();
    }
}
