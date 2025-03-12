package me.wesley1808.servercore.common.config.impl.dynamic;

import me.wesley1808.servercore.common.config.data.dynamic.DynamicConfig;
import me.wesley1808.servercore.common.config.data.dynamic.Setting;
import me.wesley1808.servercore.common.dynamic.DynamicSetting;
import me.wesley1808.servercore.common.utils.Util;

import java.util.List;
import java.util.Map;

public class DynamicConfigImpl implements DynamicConfig {
    private final boolean enabled;
    private final int targetMspt;
    private final Map<DynamicSetting, Integer> defaultValues;
    private final List<Setting> settings;

    public DynamicConfigImpl(DynamicConfig source) {
        this.enabled = source.enabled();
        this.targetMspt = source.targetMspt();
        this.defaultValues = source.defaultValues();
        this.settings = Util.map(source.settings(), SettingImpl::new);
    }

    @Override
    public boolean enabled() {
        return this.enabled;
    }

    @Override
    public int targetMspt() {
        return this.targetMspt;
    }

    @Override
    public Map<DynamicSetting, Integer> defaultValues() {
        return this.defaultValues;
    }

    @Override
    public List<Setting> settings() {
        return this.settings;
    }
}
