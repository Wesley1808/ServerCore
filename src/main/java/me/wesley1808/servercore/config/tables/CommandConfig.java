package me.wesley1808.servercore.config.tables;

import me.wesley1808.servercore.config.ConfigEntry;

public final class CommandConfig {
    public static final ConfigEntry<Boolean> COMMAND_STATUS = new ConfigEntry<>(true, "Enables / disables the /servercore status command.");
    public static final ConfigEntry<String> STATUS_CONTENT = new ConfigEntry<>("§3>----( §bServerCore §3)----<\n§7>§8> §3Chunk-Tick Distance: §a%CHUNK_TICK_DISTANCE%\n§7>§8> §3Simulation Distance: §a%SIMULATION_DISTANCE%\n§7>§8> §3View Distance: §a%VIEW_DISTANCE%\n§7>§8> §3Mobcap Multiplier: §a%MOBCAPS%", "The content for the /servercore status command.");
    public static final ConfigEntry<Boolean> COMMAND_MOBCAPS = new ConfigEntry<>(true, "Enables / disables the /mobcaps command.\nForcefully set to false by: VMP");
    public static final ConfigEntry<String> MOBCAP_TITLE = new ConfigEntry<>("§3Per Player Mobcaps (§a%MODIFIER%§3)", "The title for the /mobcaps command.");
    public static final ConfigEntry<String> MOBCAP_CONTENT = new ConfigEntry<>("§8- §3%NAME%: §a%CURRENT% §8/ §a%CAPACITY%", "The content for the /mobcaps command. This is displayed for every existing spawngroup.");
    public static final ConfigEntry<String> STATS_OVERVIEW_CONTENT = new ConfigEntry<>("§3>----(§b Statistics - Overview §3)----<\n§8» §3TPS: §a%TPS% §8- §3MSPT: §a%MSPT%\n§8» §3Total chunk count: §a%CHUNK_COUNT%\n§8» §3Total entity count: §a%ENTITY_COUNT%\n§8» §3Total block entity count: §a%BLOCK_ENTITY_COUNT%", "The content for the /statistics command.");
    public static final ConfigEntry<String> STATS_PAGE_TITLE = new ConfigEntry<>("§3>----(§b %TITLE% - Page %PAGE% §3)----<", "The title for the /statistics (block) entities command.");
    public static final ConfigEntry<String> STATS_PAGE_CONTENT = new ConfigEntry<>("§a%INDEX%. §3%NAME% §a%COUNT%", "The content for the /statistics (block) entities command. This is displayed for every entity type.");
}
