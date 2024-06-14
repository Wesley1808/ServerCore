package me.wesley1808.servercore.common.dynamic;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.wesley1808.servercore.common.config.Config;
import me.wesley1808.servercore.common.config.data.dynamic.Setting;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.IntFunction;

public enum DynamicSetting {
    MOBCAP_PERCENTAGE(1, 1024,
            "Mobcap percentage",
            (value) -> String.format("%d%%", value),
            (manager, value) -> DynamicManager.modifyMobcaps(value)
    ),
    CHUNK_TICK_DISTANCE(2, 256,
            "Chunk-tick distance",
            String::valueOf
    ),
    SIMULATION_DISTANCE(2, 256,
            "Simulation distance",
            String::valueOf,
            DynamicManager::modifySimulationDistance
    ),
    VIEW_DISTANCE(2, 256,
            "View distance",
            String::valueOf,
            DynamicManager::modifyViewDistance
    );

    private final BiConsumer<DynamicManager, Integer> onChanged;
    private final IntFunction<String> valueFormatter;
    private final String formattedName;
    private final int minimumBound;
    private final int maximumBound;
    private DynamicSetting prev;
    private DynamicSetting next;
    private boolean enabled;
    private int increment = -1;
    private int min = -1;
    private int max = -1;
    private int interval = -1;
    private int value = -1;

    DynamicSetting(int minimumBound, int maximumBound, String formattedName, IntFunction<String> valueFormatter) {
        this(minimumBound, maximumBound, formattedName, valueFormatter, null);
    }

    DynamicSetting(int minimumBound, int maximumBound, String formattedName, IntFunction<String> valueFormatter, BiConsumer<DynamicManager, Integer> onChanged) {
        this.onChanged = onChanged;
        this.valueFormatter = valueFormatter;
        this.formattedName = formattedName;
        this.minimumBound = minimumBound;
        this.maximumBound = maximumBound;
    }

    public static void reload() {
        List<Setting> settings = Config.get().dynamic().settings();
        List<DynamicSetting> activeSettings = new ObjectArrayList<>();
        for (Setting setting : settings) {
            DynamicSetting dynamicSetting = setting.dynamicSetting();
            dynamicSetting.modifyConfiguration(setting);
            if (setting.enabled()) {
                activeSettings.add(dynamicSetting);
            }
        }

        for (int i = 0; i < activeSettings.size(); i++) {
            DynamicSetting setting = activeSettings.get(i);
            setting.initialize(
                    i == 0 ? null : activeSettings.get(i - 1), // prev
                    i == activeSettings.size() - 1 ? null : activeSettings.get(i + 1) // next
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
        return this.enabled && this.interval > 0 && count % this.interval == 0;
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
        this.enabled = settings.enabled();
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

    public boolean isEnabled() {
        return this.enabled;
    }

    public int getMax() {
        if (this.max < 0) {
            throw new IllegalStateException(this.name() + " has not been initialized yet!");
        }
        return this.max;
    }

    public String getFormattedName() {
        return this.formattedName;
    }

    public String getFormattedValue() {
        return this.valueFormatter.apply(this.value);
    }
}
