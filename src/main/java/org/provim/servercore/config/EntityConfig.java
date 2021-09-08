package org.provim.servercore.config;

import com.moandjiezana.toml.Toml;

public final class EntityConfig {
    public boolean enabled;
    public int villagerCount;
    public int villagerRange;
    public int animalCount;
    public int animalRange;

    public EntityConfig(Toml defaultToml) {
        final Toml toml = defaultToml.getTable("entity_limits");
        this.enabled = toml.getBoolean("enabled", false);
        this.villagerCount = Math.toIntExact(toml.getLong("villager_count", 24L));
        this.villagerRange = Math.toIntExact(toml.getLong("villager_range", 64L));
        this.animalCount = Math.toIntExact(toml.getLong("animal_count", 32L));
        this.animalRange = Math.toIntExact(toml.getLong("animal_range", 64L));
    }
}