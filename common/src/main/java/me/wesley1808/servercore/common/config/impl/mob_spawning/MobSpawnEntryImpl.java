package me.wesley1808.servercore.common.config.impl.mob_spawning;

import me.wesley1808.servercore.common.config.data.mob_spawning.MobSpawnEntry;
import net.minecraft.world.entity.MobCategory;

public class MobSpawnEntryImpl implements MobSpawnEntry {
    private final MobCategory category;
    private final int capacity;
    private final int spawnInterval;

    public MobSpawnEntryImpl(MobSpawnEntry source) {
        this.category = source.category();
        this.capacity = source.capacity();
        this.spawnInterval = source.spawnInterval();
    }

    public MobSpawnEntryImpl(MobCategory category, int capacity, int spawnInterval) {
        this.category = category;
        this.capacity = capacity;
        this.spawnInterval = spawnInterval;
    }

    @Override
    public MobCategory category() {
        return this.category;
    }

    @Override
    public int capacity() {
        return this.capacity;
    }

    @Override
    public int spawnInterval() {
        return this.spawnInterval;
    }
}
