package org.provim.servercore.config;

import com.electronwill.nightconfig.core.CommentedConfig;

public final class EntityConfig {
    private static final String ENABLED = "enabled";
    private static final String VILLAGER_COUNT = "villager_count";
    private static final String ANIMAL_COUNT = "animal_count";
    private static final String VILLAGER_RANGE = "villager_range";
    private static final String ANIMAL_RANGE = "animal_range";

    public boolean enabled;
    public int villagerCount;
    public int villagerRange;
    public int animalCount;
    public int animalRange;

    public EntityConfig(CommentedConfig config) {
        config.setComment(ENABLED, " (Default = false) Enables this feature.");
        this.enabled = config.getOrElse(ENABLED, false);

        config.setComment(VILLAGER_COUNT, " (Default = [Villager: 24, Animals: 32]) Maximum count before stopping entities of the same type from breeding.");
        this.villagerCount = config.getIntOrElse(VILLAGER_COUNT, 24);
        this.animalCount = config.getIntOrElse(ANIMAL_COUNT, 32);

        config.setComment(VILLAGER_RANGE, " (Default = [Villager: 64, Animals: 64]) The range it will check for entities of the same type.");
        this.villagerRange = config.getIntOrElse(VILLAGER_RANGE, 64);
        this.animalRange = config.getIntOrElse(ANIMAL_RANGE, 64);
    }

    public void save(CommentedConfig config) {
        config.set(ENABLED, this.enabled);
        config.set(VILLAGER_COUNT, this.villagerCount);
        config.set(ANIMAL_COUNT, this.animalCount);
        config.set(VILLAGER_RANGE, this.villagerRange);
        config.set(ANIMAL_RANGE, this.animalRange);
    }
}