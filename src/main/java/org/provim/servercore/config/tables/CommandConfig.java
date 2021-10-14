package org.provim.servercore.config.tables;

import org.provim.servercore.config.ConfigEntry;

public final class CommandConfig {
    public static final ConfigEntry<Boolean> COMMAND_MOBCAPS = new ConfigEntry<>(true, " Enables / disables the /mobcaps command.");
    public static final ConfigEntry<String> MOBCAP_TITLE = new ConfigEntry<>("§3Per Player Mobcaps (§a%.1f§3)", " The title for the /mobcaps command.\n Arguments: The current global mobcap modifier (%.1f).");
    public static final ConfigEntry<String> MOBCAP_SPAWN_GROUP = new ConfigEntry<>("§8- §3%s: §a%d §8/ §a%d", " The content for the /mobcaps command. This is displayed for every existing spawngroup.\n Arguments: The name of the spawngroup, the current mobcount near the player and the total capacity.");
}
