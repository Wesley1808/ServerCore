package me.wesley1808.servercore.common.config.data.impl.mob_spawning;

import me.wesley1808.servercore.common.config.data.mob_spawning.MobSpawnEntry;
import me.wesley1808.servercore.common.interfaces.IMobCategory;
import net.minecraft.world.entity.MobCategory;

public class MobSpawnEntryImpl implements MobSpawnEntry {
    private final MobCategory category;
    private final int capacity;
    private final int spawnInterval;

    public MobSpawnEntryImpl(MobCategory category) {
        this.category = category;
        this.spawnInterval = category.isPersistent() ? 400 : 1;
        this.capacity = IMobCategory.getOriginalCapacity(category);
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
