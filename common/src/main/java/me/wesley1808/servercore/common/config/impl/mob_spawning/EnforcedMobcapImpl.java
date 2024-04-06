package me.wesley1808.servercore.common.config.impl.mob_spawning;

import me.wesley1808.servercore.common.config.data.mob_spawning.EnforcedMobcap;

public class EnforcedMobcapImpl implements EnforcedMobcap {
    private final boolean enforcesMobcap;
    private final int additionalCapacity;

    public EnforcedMobcapImpl(EnforcedMobcap source) {
        this.enforcesMobcap = source.enforcesMobcap();
        this.additionalCapacity = source.additionalCapacity();
    }

    @Override
    public boolean enforcesMobcap() {
        return this.enforcesMobcap;
    }

    @Override
    public int additionalCapacity() {
        return this.additionalCapacity;
    }
}
