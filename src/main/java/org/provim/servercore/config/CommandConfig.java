package org.provim.servercore.config;

import com.electronwill.nightconfig.core.CommentedConfig;

public final class CommandConfig {
    private static final String COMMAND_MOBCAPS = "command_mobcaps";
    private static final String MOBCAP_TITLE = "mobcap_title";
    private static final String MOBCAP_SPAWNGROUP = "mobcap_spawngroup";

    public boolean commandMobcaps;
    public String mobcapTitle;
    public String mobcapSpawnGroup;

    public CommandConfig(CommentedConfig config) {
        config.setComment(COMMAND_MOBCAPS, " Enables / disables the /mobcaps command.");
        this.commandMobcaps = config.getOrElse(COMMAND_MOBCAPS, true);

        config.setComment(MOBCAP_TITLE, " The title for the /mobcaps command.\n Arguments: The current global mobcap modifier (%.1f).");
        this.mobcapTitle = config.getOrElse(MOBCAP_TITLE, "§3Per Player Mobcaps (§a%.1f§3)");

        config.setComment(MOBCAP_SPAWNGROUP, " The content for the /mobcaps command. This is displayed for every existing spawngroup.\n Arguments: The name of the spawngroup, the current mobcount near the player and the total capacity.");
        this.mobcapSpawnGroup = config.getOrElse(MOBCAP_SPAWNGROUP, "§8- §3%s: §a%d §8/ §a%d");
    }

    public void save(CommentedConfig config) {
        config.set(COMMAND_MOBCAPS, this.commandMobcaps);
        config.set(MOBCAP_TITLE, this.mobcapTitle);
        config.set(MOBCAP_SPAWNGROUP, this.mobcapSpawnGroup);
    }
}