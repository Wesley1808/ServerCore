package me.wesley1808.servercore.common.config.impl.dynamic;

import me.wesley1808.servercore.common.config.data.dynamic.DynamicConfig;
import me.wesley1808.servercore.common.config.data.dynamic.Setting;
import me.wesley1808.servercore.common.utils.Util;

import java.util.List;

public class DynamicConfigImpl implements DynamicConfig {
    private final boolean enabled;
    private final int targetMspt;
    private final List<Setting> settings;

    public DynamicConfigImpl(DynamicConfig source) {
        this.enabled = source.enabled();
        this.targetMspt = source.targetMspt();
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
    public List<Setting> settings() {
        return this.settings;
    }
}
