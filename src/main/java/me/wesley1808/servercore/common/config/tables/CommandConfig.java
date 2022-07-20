package me.wesley1808.servercore.common.config.tables;

import me.wesley1808.servercore.common.config.ConfigEntry;

public final class CommandConfig {
    public static final ConfigEntry<Boolean> COMMAND_STATUS = new ConfigEntry<>(true, "Enables / disables the /servercore status command.");
    public static final ConfigEntry<Boolean> COMMAND_MOBCAPS = new ConfigEntry<>(true, "Enables / disables the /mobcaps command.");
    public static final ConfigEntry<String> MOBCAP_TITLE = new ConfigEntry<>("<dark_aqua>${line} <aqua>Mobcaps</aqua> (<aqua>${mobcap_modifier}</aqua>) ${line}</dark_aqua>", "The title for the /mobcaps command.");
    public static final ConfigEntry<String> MOBCAP_CONTENT = new ConfigEntry<>("<dark_gray>» <dark_aqua>${name}:</dark_aqua> <green>${current}</green> / <green>${capacity}</green></dark_gray>", "The content for the /mobcaps command. This is displayed for every existing spawngroup.");
    public static final ConfigEntry<String> STATUS_TITLE = new ConfigEntry<>("<dark_aqua>${line} <aqua>ServerCore</aqua> ${line}</dark_aqua>", "The title for the /servercore status command.");
    public static final ConfigEntry<String> STATUS_CONTENT = new ConfigEntry<>("<dark_gray>» <dark_aqua>Version:</dark_aqua> <green>${version}</green>\n» <dark_aqua>Chunk-Tick Distance:</dark_aqua> <green>${chunk_tick_distance}</green>\n» <dark_aqua>Simulation Distance:</dark_aqua> <green>${simulation_distance}</green>\n» <dark_aqua>View Distance:</dark_aqua> <green>${view_distance}</green>\n» <dark_aqua>Mobcap Multiplier:</dark_aqua> <green>${mobcap_modifier}</green></dark_gray>", "The content for the /servercore status command.");

    public static final ConfigEntry<String> STATS_TITLE = new ConfigEntry<>("<dark_aqua>${line} <aqua>Statistics</aqua> ${line}</dark_aqua>", "The title for the /statistics command.");
    public static final ConfigEntry<String> STATS_CONTENT = new ConfigEntry<>("<dark_gray>» <dark_aqua>TPS:</dark_aqua> <green>${tps}</green> - <dark_aqua>MSPT:</dark_aqua> <green>${mspt}</green>\n» <dark_aqua>Total chunk count:</dark_aqua> <green>${chunk_count}</green>\n» <dark_aqua>Total entity count:</dark_aqua> <green>${entity_count}</green>\n» <dark_aqua>Total block entity count:</dark_aqua> <green>${block_entity_count}</green></dark_gray>", "The content for the /statistics command.");
    public static final ConfigEntry<String> STATS_PAGE_TITLE = new ConfigEntry<>("<dark_aqua>${line} <aqua>${title}</aqua> by <aqua>${type}</aqua> ${line}</dark_aqua>", "The title for the /statistics (block) entities command.");
    public static final ConfigEntry<String> STATS_PAGE_TITLE_PLAYER = new ConfigEntry<>("<dark_aqua>${line} <aqua>${title}</aqua> for <aqua>${player}</aqua> ${line}</dark_aqua>");
    public static final ConfigEntry<String> STATS_PAGE_CONTENT = new ConfigEntry<>("<green>${index}. <dark_aqua>${name}</dark_aqua> ${count}</green>", "The content for the /statistics (block) entities command. This is displayed for every entry.");
    public static final ConfigEntry<String> STATS_PAGE_FOOTER = new ConfigEntry<>("<dark_aqua>${line} <green>${prev_page}</green> Page <aqua>${page}</aqua> of <aqua>${page_count}</aqua> <green>${next_page}</green> ${line}", "The footer for the /statistics (block) entities command.");
}