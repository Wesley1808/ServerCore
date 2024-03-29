package me.wesley1808.servercore.common.config.data.impl.breeding_cap;

import me.wesley1808.servercore.common.config.data.breeding_cap.BreedingCap;

public class BreedingCapImpl implements BreedingCap {
    private final int limit;
    private final int range;

    public BreedingCapImpl(BreedingCap source) {
        this.limit = source.limit();
        this.range = source.range();
    }

    @Override
    public int limit() {
        return this.limit;
    }

    @Override
    public int range() {
        return this.range;
    }
}
