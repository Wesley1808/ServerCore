package org.provim.servercore.config;

import com.moandjiezana.toml.Toml;

public final class MessageConfig {
    public String mobcapTitle;
    public String mobcapSpawnGroup;

    public MessageConfig(Toml toml) {
        this.mobcapTitle = toml.getString("mobcap_title", "§3Per Player Mobcaps (§a%.1f§3)");
        this.mobcapSpawnGroup = toml.getString("mobcap_spawngroup", "§8- §3%s: §a%d §8/ §a%d");
    }
}