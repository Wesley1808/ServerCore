package me.wesley1808.servercore.config.tables;

import me.wesley1808.servercore.config.ConfigEntry;

public final class CommandConfig {
    public static final ConfigEntry<Boolean> COMMAND_STATUS = new ConfigEntry<>(true, "Enables / disables the /servercore status command.");
    public static final ConfigEntry<Boolean> COMMAND_MOBCAPS = new ConfigEntry<>(true, "Enables / disables the /mobcaps command.\nForcefully set to false by: VMP");
}