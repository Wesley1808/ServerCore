package org.provim.servercore.config;

public final class EntityConfig {
    public final ConfigEntry<Boolean> enabled = new ConfigEntry<>(false, " (Default = false) Enables this feature.");
    public final ConfigEntry<Integer> villagerCount = new ConfigEntry<>(24, " (Default = [Villager: 24, Animals: 32]) Maximum count before stopping entities of the same type from breeding.");
    public final ConfigEntry<Integer> villagerRange = new ConfigEntry<>(32);
    public final ConfigEntry<Integer> animalCount = new ConfigEntry<>(64, " (Default = [Villager: 64, Animals: 64]) The range it will check for entities of the same type.");
    public final ConfigEntry<Integer> animalRange = new ConfigEntry<>(64);
}