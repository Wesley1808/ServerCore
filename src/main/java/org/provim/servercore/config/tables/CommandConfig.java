package org.provim.servercore.config.tables;

import org.provim.servercore.config.ConfigEntry;

public final class CommandConfig {
    public static final ConfigEntry<Boolean> COMMAND_MOBCAPS = new ConfigEntry<>(true, "Enables / disables the /mobcaps command.");
    public static final ConfigEntry<String> MOBCAP_TITLE = new ConfigEntry<>("§3Per Player Mobcaps (§a%MODIFIER%§3)", "The title for the /mobcaps command.");
    public static final ConfigEntry<String> MOBCAP_SPAWN_GROUP = new ConfigEntry<>("§8- §3%NAME%: §a%CURRENT% §8/ §a%CAPACITY%", "The content for the /mobcaps command. This is displayed for every existing spawngroup.");
}
