package org.provim.servercore.config;

public class MessageConfig {
    protected static MessageConfig instance = new MessageConfig();
    public String performance = "§8- §3TPS: §a%s §3MSPT: §a%s\n§8- §3Online: §a%d\n§8- §3View distance: §a%d\n§8- §3Mobcap multiplier: §a%s\n§8- §3Chunk-Tick distance: §a%d";

    public static MessageConfig instance() {
        return instance;
    }
}
