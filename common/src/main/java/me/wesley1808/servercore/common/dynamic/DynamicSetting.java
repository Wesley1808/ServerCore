package me.wesley1808.servercore.common.dynamic;

import me.wesley1808.servercore.common.config.Config;
import me.wesley1808.servercore.common.config.data.dynamic.Setting;
import me.wesley1808.servercore.common.utils.Environment;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.IntFunction;

public enum DynamicSetting {
    MOBCAP_PERCENTAGE(0, 1024, 100, true,
            "Mobcap percentage",
            (value) -> String.format("%d%%", value),
            (manager, value) -> DynamicManager.modifyMobcaps(value)
    ),
    CHUNK_TICK_DISTANCE(2, 256, 10, true,
            "Chunk-tick distance",
            String::valueOf
    ),
    SIMULATION_DISTANCE(Environment.CLIENT ? 5 : 2, 256, 10, false,
            "Simulation distance",
            String::valueOf,
            DynamicManager::modifySimulationDistance
    ),
    VIEW_DISTANCE(2, 256, 10, false,
            "View distance",
            String::valueOf,
            DynamicManager::modifyViewDistance
    );

    private final BiConsumer<DynamicManager, Integer> onChanged;
    private final IntFunction<String> valueFormatter;
    private final String formattedName;
    private final int minimumBound;
    private final int maximumBound;
    private final int defaultValue;
    private final boolean requiresInit;
    private boolean initialized;
    private int maxValue;
    private int value;

    DynamicSetting(int minimumBound, int maximumBound, int defaultValue, boolean requiresInit, String formattedName, IntFunction<String> valueFormatter) {
        this(minimumBound, maximumBound, defaultValue, requiresInit, formattedName, valueFormatter, null);
    }

    DynamicSetting(int minimumBound, int maximumBound, int defaultValue, boolean requiresInit, String formattedName, IntFunction<String> valueFormatter, BiConsumer<DynamicManager, Integer> onChanged) {
        this.onChanged = onChanged;
        this.valueFormatter = valueFormatter;
        this.formattedName = formattedName;
        this.minimumBound = minimumBound;
        this.maximumBound = maximumBound;
        this.defaultValue = defaultValue;
        this.requiresInit = requiresInit;
        this.maxValue = defaultValue;
        this.value = defaultValue;
    }

    public static void initDefaultValues(DynamicManager manager) {
        Map<DynamicSetting, Integer> defaultValues = Config.get().dynamic().defaultValues();
        defaultValues.forEach((setting, value) -> {
            int clampedValue = Mth.clamp(value, setting.minimumBound, setting.maximumBound);
            setting.set(clampedValue, manager);
        });

        // Ensure dynamic setting values are always initialized.
        for (DynamicSetting setting : values()) {
            if (setting.requiresInit && !defaultValues.containsKey(setting)) {
                setting.notifyChanged(manager);
            }
        }
    }

    public static void recalculateValues(List<Setting> settings) {
        for (DynamicSetting setting : values()) {
            setting.maxValue = 0;
        }

        for (Setting setting : settings) {
            DynamicSetting dynamicSetting = setting.dynamicSetting();
            int max = setting.max();
            if (max > dynamicSetting.maxValue) {
                dynamicSetting.maxValue = max;
            }
        }

        for (DynamicSetting setting : values()) {
            if (setting.maxValue <= 0) {
                setting.maxValue = setting.defaultValue;
            }

            if (!setting.initialized) {
                setting.set(setting.maxValue, null);
                setting.initialized = true;
            }
        }
    }

    public static void resetAll() {
        for (DynamicSetting setting : values()) {
            setting.initialized = false;
        }
    }

    public void set(int value, @Nullable DynamicManager manager) {
        this.value = value;
        this.notifyChanged(manager);
    }

    public void notifyChanged(@Nullable DynamicManager manager) {
        if (this.onChanged != null && manager != null) {
            this.onChanged.accept(manager, this.value);
        }
    }

    public int get() {
        return this.value;
    }

    public int getLowerBound() {
        return this.minimumBound;
    }

    public int getUpperBound() {
        return this.maximumBound;
    }

    public int getDefaultValue() {
        return this.defaultValue;
    }

    public String getFormattedName() {
        return this.formattedName;
    }

    public String getFormattedValue() {
        return this.valueFormatter.apply(this.value);
    }
}
