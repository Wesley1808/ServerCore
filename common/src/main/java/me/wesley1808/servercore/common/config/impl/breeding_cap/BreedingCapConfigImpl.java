package me.wesley1808.servercore.common.config.impl.breeding_cap;

import me.wesley1808.servercore.common.config.data.breeding_cap.BreedingCap;
import me.wesley1808.servercore.common.config.data.breeding_cap.BreedingCapConfig;

public class BreedingCapConfigImpl implements BreedingCapConfig {
    private final boolean enabled;
    private final BreedingCap villagers;
    private final BreedingCap happyGhasts;
    private final BreedingCap animals;

    public BreedingCapConfigImpl(BreedingCapConfig source) {
        this.enabled = source.enabled();
        this.villagers = new BreedingCapImpl(source.villagers());
        this.happyGhasts = new BreedingCapImpl(source.happyGhasts());
        this.animals = new BreedingCapImpl(source.animals());
    }

    @Override
    public boolean enabled() {
        return this.enabled;
    }

    @Override
    public BreedingCap villagers() {
        return this.villagers;
    }

    @Override
    public BreedingCap happyGhasts() {
        return this.happyGhasts;
    }

    @Override
    public BreedingCap animals() {
        return this.animals;
    }
}
