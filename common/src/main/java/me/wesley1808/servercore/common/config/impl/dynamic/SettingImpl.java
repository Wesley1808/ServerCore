package me.wesley1808.servercore.common.config.impl.dynamic;

import me.wesley1808.servercore.common.config.data.dynamic.Setting;
import me.wesley1808.servercore.common.dynamic.DynamicSetting;

public class SettingImpl implements Setting {
    private final DynamicSetting dynamicSetting;
    private final boolean enabled;
    private final int max;
    private final int min;
    private final int increment;
    private final int interval;

    public SettingImpl(Setting source) {
        this.dynamicSetting = source.dynamicSetting();
        this.enabled = source.enabled();
        this.max = source.max();
        this.min = source.min();
        this.increment = source.increment();
        this.interval = source.interval();
    }

    public SettingImpl(DynamicSetting dynamicSetting, boolean enabled, int max, int min, int increment, int interval) {
        this.dynamicSetting = dynamicSetting;
        this.enabled = enabled;
        this.max = max;
        this.min = min;
        this.increment = increment;
        this.interval = interval;
    }

    @Override
    public DynamicSetting dynamicSetting() {
        return this.dynamicSetting;
    }

    @Override
    public boolean enabled() {
        return this.enabled;
    }

    @Override
    public int max() {
        return this.max;
    }

    @Override
    public int min() {
        return this.min;
    }

    @Override
    public int increment() {
        return this.increment;
    }

    @Override
    public int interval() {
        return this.interval;
    }
}
