package me.wesley1808.servercore.common.dynamic;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.function.BiConsumer;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;

import static me.wesley1808.servercore.common.config.tables.DynamicConfig.*;

public enum DynamicSetting {
    VIEW_DISTANCE(
            VIEW_DISTANCE_UPDATE_RATE::get,
            VIEW_DISTANCE_INCREMENT::get,
            MIN_VIEW_DISTANCE::get,
            MAX_VIEW_DISTANCE::get,
            (manager, value) -> manager.modifyViewDistance(value.intValue())
    ),

    SIMULATION_DISTANCE(
            UPDATE_RATE::get,
            SIMULATION_DISTANCE_INCREMENT::get,
            MIN_SIMULATION_DISTANCE::get,
            MAX_SIMULATION_DISTANCE::get,
            (manager, value) -> manager.modifySimulationDistance(value.intValue())
    ),

    MOBCAP_MULTIPLIER(
            UPDATE_RATE::get,
            MOBCAP_INCREMENT::get,
            MIN_MOBCAP::get,
            MAX_MOBCAP::get,
            (manager, value) -> DynamicManager.modifyMobcaps(value)
    ),

    CHUNK_TICK_DISTANCE(
            UPDATE_RATE::get,
            CHUNK_TICK_DISTANCE_INCREMENT::get,
            MIN_CHUNK_TICK_DISTANCE::get,
            MAX_CHUNK_TICK_DISTANCE::get
    );

    private final BiConsumer<DynamicManager, Double> onChanged;
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

    DynamicSetting(IntSupplier interval, DoubleSupplier increment, DoubleSupplier min, DoubleSupplier max, BiConsumer<DynamicManager, Double> onChanged) {
        this.interval = interval;
        this.onChanged = onChanged;
        this.increment = increment;
        this.min = min;
        this.max = max;
        this.reset();
    }

    public static void loadCustomOrder() {
        ObjectArrayList<DynamicSetting> settings = new ObjectArrayList<>();
        for (String key : SETTING_ORDER.get()) {
            settings.add(DynamicSetting.valueOf(key.toUpperCase()));
        }

        for (int i = 0; i < settings.size(); i++) {
            DynamicSetting setting = settings.get(i);
            DynamicSetting prev = i == 0 ? null : settings.get(i - 1);
            DynamicSetting next = i == settings.size() - 1 ? null : settings.get(i + 1);
            setting.initialize(prev, next);
        }
    }

    public static void resetAll() {
        for (DynamicSetting setting : values()) {
            setting.reset();
        }
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

    public void reset() {
        this.set(this.max.getAsDouble(), null);
    }

    public void set(double value, @Nullable DynamicManager manager) {
        this.set(BigDecimal.valueOf(value), manager);
    }

    public void set(BigDecimal value, @Nullable DynamicManager manager) {
        this.value = value;
        this.cachedValue = value.doubleValue();

        if (this.onChanged != null && manager != null) {
            this.onChanged.accept(manager, this.cachedValue);
        }
    }

    public boolean modify(boolean increase, DynamicManager manager) {
        BigDecimal value = this.newValue(increase);
        if (this.shouldModify(value)) {
            this.set(value, manager);
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
