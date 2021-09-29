package org.provim.servercore.config;

import com.moandjiezana.toml.Toml;

public final class CommandConfig {
    public boolean commandMobcaps;
    public String mobcapTitle;
    public String mobcapSpawnGroup;

    public CommandConfig(Toml toml) {
        this.commandMobcaps = toml.getBoolean("command_mobcaps", true);
        this.mobcapTitle = toml.getString("mobcap_title", "§3Per Player Mobcaps (§a%.1f§3)");
        this.mobcapSpawnGroup = toml.getString("mobcap_spawngroup", "§8- §3%s: §a%d §8/ §a%d");
    }
}