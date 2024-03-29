package me.wesley1808.servercore.common.config.impl;

import me.wesley1808.servercore.common.config.MainConfig;
import me.wesley1808.servercore.common.config.data.CommandConfig;
import me.wesley1808.servercore.common.config.data.FeatureConfig;
import me.wesley1808.servercore.common.config.data.activation_range.ActivationRangeConfig;
import me.wesley1808.servercore.common.config.data.breeding_cap.BreedingCapConfig;
import me.wesley1808.servercore.common.config.data.dynamic.DynamicConfig;
import me.wesley1808.servercore.common.config.data.mob_spawning.MobSpawnConfig;
import me.wesley1808.servercore.common.config.impl.activation_range.ActivationRangeConfigImpl;
import me.wesley1808.servercore.common.config.impl.breeding_cap.BreedingCapConfigImpl;
import me.wesley1808.servercore.common.config.impl.dynamic.DynamicConfigImpl;

public class MainConfigImpl implements MainConfig {
    private final FeatureConfig features;
    private final DynamicConfig dynamic;
    private final BreedingCapConfig breedingCap;
    private final MobSpawnConfig mobSpawning;
    private final CommandConfig commands;
    private final ActivationRangeConfig activationRange;

    public MainConfigImpl(MainConfig source) {
        this.features = new FeatureConfigImpl(source.features());
        this.dynamic = new DynamicConfigImpl(source.dynamic());
        this.breedingCap = new BreedingCapConfigImpl(source.breedingCap());
        this.activationRange = new ActivationRangeConfigImpl(source.activationRange());
        this.mobSpawning = source.mobSpawning();
        this.commands = source.commands();
    }

    @Override
    public FeatureConfig features() {
        return this.features;
    }

    @Override
    public DynamicConfig dynamic() {
        return this.dynamic;
    }

    @Override
    public BreedingCapConfig breedingCap() {
        return this.breedingCap;
    }

    @Override
    public MobSpawnConfig mobSpawning() {
        return this.mobSpawning;
    }

    @Override
    public CommandConfig commands() {
        return this.commands;
    }

    @Override
    public ActivationRangeConfig activationRange() {
        return this.activationRange;
    }
}
