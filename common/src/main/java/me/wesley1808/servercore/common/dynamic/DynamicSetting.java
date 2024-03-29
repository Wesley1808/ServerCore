package me.wesley1808.servercore.common.dynamic;

import me.wesley1808.servercore.common.config.Config;
import me.wesley1808.servercore.common.config.data.dynamic.Setting;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiConsumer;

public enum DynamicSetting {
    VIEW_DISTANCE(2, 256, DynamicManager::modifyViewDistance),
    SIMULATION_DISTANCE(2, 256, DynamicManager::modifySimulationDistance),
    MOBCAP(1, 1024, (manager, value) -> DynamicManager.modifyMobcaps(value)),
    CHUNK_TICK_DISTANCE(2, 256);

    private final BiConsumer<DynamicManager, Integer> onChanged;
    private final int minimumBound;
    private final int maximumBound;
    private DynamicSetting prev;
    private DynamicSetting next;
    private int increment = -1;
    private int min = -1;
    private int max = -1;
    private int interval = -1;
    private int value = -1;

    DynamicSetting(int minimumBound, int maximumBound) {
        this(minimumBound, maximumBound, null);
    }

    DynamicSetting(int minimumBound, int maximumBound, BiConsumer<DynamicManager, Integer> onChanged) {
        this.onChanged = onChanged;
        this.minimumBound = minimumBound;
        this.maximumBound = maximumBound;
    }

    public static void reload() {
        List<Setting> settings = Config.get().dynamic().settings();
        for (int i = 0; i < settings.size(); i++) {
            Setting setting = settings.get(i);
            DynamicSetting dynamicSetting = setting.dynamicSetting();
            dynamicSetting.modifyConfiguration(setting);
            dynamicSetting.initialize(
                    i == 0 ? null : settings.get(i - 1).dynamicSetting(), // prev
                    i == settings.size() - 1 ? null : settings.get(i + 1).dynamicSetting() // next
            );
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

    public int get() {
        return this.value;
    }

    public boolean shouldRun(int count) {
        return this.interval > 0 && count % this.interval == 0;
    }

    public void reset() {
        this.set(this.max, null);
    }

    public void set(int value, @Nullable DynamicManager manager) {
        this.value = value;

        if (this.onChanged != null && manager != null) {
            this.onChanged.accept(manager, value);
        }
    }

    public boolean modify(boolean increase, DynamicManager manager) {
        int value = this.newValue(increase);
        if (this.shouldModify(value)) {
            this.set(value, manager);
            return true;
        }
        return false;
    }

    private boolean shouldModify(int value) {
        int compared = Integer.compare(value, this.value);
        return compared != 0 && (this.next != null || this.prev != null)
               && (compared < 0 || (!this.isMaximum() && (this.next == null || this.next.isMaximum())))
               && (compared > 0 || (!this.isMinimum() && (this.prev == null || this.prev.isMinimum())));
    }

    private int newValue(boolean increase) {
        return increase
                ? Math.min(this.value + this.increment, this.max)
                : Math.max(this.value - this.increment, this.min);
    }

    private boolean isMinimum() {
        return this.value <= this.min;
    }

    private boolean isMaximum() {
        return this.value >= this.max;
    }

    private void modifyConfiguration(Setting settings) {
        this.max = Math.min(settings.max(), this.maximumBound);
        this.min = Math.max(settings.min(), this.minimumBound);
        this.increment = settings.increment();
        this.interval = settings.interval();

        if (this.value < 0) {
            this.set(this.max, null);
        }
    }

    public int getLowerBound() {
        return this.minimumBound;
    }

    public int getUpperBound() {
        return this.maximumBound;
    }

    public int getMax() {
        if (this.max < 0) {
            throw new IllegalStateException(this.name() + " has not been initialized yet!");
        }
        return this.max;
    }
}
