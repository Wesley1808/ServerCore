package me.wesley1808.servercore.config.tables;

import me.wesley1808.servercore.config.ConfigEntry;

public final class CommandConfig {
    public static final ConfigEntry<Boolean> COMMAND_STATUS = new ConfigEntry<>(true, "Enables / disables the /servercore status command.");
    public static final ConfigEntry<Boolean> COMMAND_MOBCAPS = new ConfigEntry<>(true, "Enables / disables the /mobcaps command.\nForcefully set to false by: VMP");
    public static final ConfigEntry<String> MOBCAP_TITLE = new ConfigEntry<>("<dark_aqua>%LINE% <aqua>Mobcaps</aqua> (<aqua>%MODIFIER%</aqua>) %LINE%</dark_aqua>", "The title for the /mobcaps command.");
    public static final ConfigEntry<String> MOBCAP_CONTENT = new ConfigEntry<>("<dark_gray>» <dark_aqua>%NAME%:</dark_aqua> <green>%CURRENT%</green> / <green>%CAPACITY%</green></dark_gray>", "The content for the /mobcaps command. This is displayed for every existing spawngroup.");
    public static final ConfigEntry<String> STATUS_TITLE = new ConfigEntry<>("<dark_aqua>%LINE% <aqua>ServerCore</aqua> %LINE%</dark_aqua>", "The title for the /servercore status command.");
    public static final ConfigEntry<String> STATUS_CONTENT = new ConfigEntry<>("<dark_gray>» <dark_aqua>Version:</dark_aqua> <green>%VERSION%</green>\n» <dark_aqua>Chunk-Tick Distance:</dark_aqua> <green>%CHUNK_TICK_DISTANCE%</green>\n» <dark_aqua>Simulation Distance:</dark_aqua> <green>%SIMULATION_DISTANCE%</green>\n» <dark_aqua>View Distance:</dark_aqua> <green>%VIEW_DISTANCE%</green>\n» <dark_aqua>Mobcap Multiplier:</dark_aqua> <green>%MOBCAPS%</green></dark_gray>", "The content for the /servercore status command.");

    public static final ConfigEntry<String> STATS_TITLE = new ConfigEntry<>("<dark_aqua>%LINE% <aqua>Statistics</aqua> %LINE%</dark_aqua>", "The title for the /statistics command.");
    public static final ConfigEntry<String> STATS_CONTENT = new ConfigEntry<>("<dark_gray>» <dark_aqua>TPS:</dark_aqua> <green>%TPS%</green> - <dark_aqua>MSPT:</dark_aqua> <green>%MSPT%</green>\n» <dark_aqua>Total chunk count:</dark_aqua> <green>%CHUNK_COUNT%</green>\n» <dark_aqua>Total entity count:</dark_aqua> <green>%ENTITY_COUNT%</green>\n» <dark_aqua>Total block entity count:</dark_aqua> <green>%BLOCK_ENTITY_COUNT%</green></dark_gray>", "The content for the /statistics command.");
    public static final ConfigEntry<String> STATS_PAGE_TITLE = new ConfigEntry<>("<dark_aqua>%LINE% <aqua>%TITLE%</aqua> by <aqua>%TYPE%</aqua> %LINE%</dark_aqua>", "The title for the /statistics (block) entities command.");
    public static final ConfigEntry<String> STATS_PAGE_TITLE_PLAYER = new ConfigEntry<>("<dark_aqua>%LINE% <aqua>%TITLE%</aqua> for <aqua>%PLAYER%</aqua> %LINE%</dark_aqua>");
    public static final ConfigEntry<String> STATS_PAGE_CONTENT = new ConfigEntry<>("<green>%INDEX%. <dark_aqua>%NAME%</dark_aqua> %COUNT%</green>", "The content for the /statistics (block) entities command. This is displayed for every entry.");
    public static final ConfigEntry<String> STATS_PAGE_FOOTER = new ConfigEntry<>("<dark_aqua>%LINE% <green>%PREV_PAGE%</green> Page <aqua>%PAGE%</aqua> of <aqua>%PAGE_COUNT%</aqua> <green>%NEXT_PAGE%</green> %LINE%", "The footer for the /statistics (block) entities command.");
}