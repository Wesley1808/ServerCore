package me.wesley1808.servercore.common.config.impl.mob_spawning;

import me.wesley1808.servercore.common.config.data.mob_spawning.EnforcedMobcap;
import me.wesley1808.servercore.common.config.data.mob_spawning.MobSpawnConfig;
import me.wesley1808.servercore.common.config.data.mob_spawning.MobSpawnEntry;
import me.wesley1808.servercore.common.utils.Util;

import java.util.List;

public class MobSpawnConfigImpl implements MobSpawnConfig {
    private final EnforcedMobcap zombieReinforcements;
    private final EnforcedMobcap portalRandomTicks;
    private final EnforcedMobcap monsterSpawner;
    private final EnforcedMobcap potionEffects;
    private final List<MobSpawnEntry> categories;

    public MobSpawnConfigImpl(MobSpawnConfig source) {
        this.zombieReinforcements = new EnforcedMobcapImpl(source.zombieReinforcements());
        this.portalRandomTicks = new EnforcedMobcapImpl(source.portalRandomTicks());
        this.monsterSpawner = new EnforcedMobcapImpl(source.monsterSpawner());
        this.potionEffects = new EnforcedMobcapImpl(source.potionEffects());
        this.categories = Util.map(source.categories(), MobSpawnEntryImpl::new);
    }

    @Override
    public EnforcedMobcap zombieReinforcements() {
        return this.zombieReinforcements;
    }

    @Override
    public EnforcedMobcap portalRandomTicks() {
        return this.portalRandomTicks;
    }

    @Override
    public EnforcedMobcap monsterSpawner() {
        return this.monsterSpawner;
    }

    @Override
    public EnforcedMobcap potionEffects() {
        return this.potionEffects;
    }

    @Override
    public List<MobSpawnEntry> categories() {
        return this.categories;
    }
}
