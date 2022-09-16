package me.wesley1808.servercore.common.config.tables;

import me.wesley1808.servercore.common.config.ConfigEntry;

public final class OptimizationConfig {
    public static final ConfigEntry<Boolean> REDUCE_SYNC_LOADS = new ConfigEntry<>(true, "(Default = true) Prevents many different lagspikes caused by loading chunks synchronously.\nThis for example causes maps to only update loaded chunks, which depending on the viewdistance can be a smaller radius than vanilla.");
    public static final ConfigEntry<Boolean> FAST_BIOME_LOOKUPS = new ConfigEntry<>(false, "(Default = false) Can significantly reduce time spent on mobspawning, but isn't as accurate as vanilla on biome borders.\nThis may cause mobs from another biome to spawn a few blocks across a biome border (this does not affect structure spawning!).");
}
