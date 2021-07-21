package org.provim.perplayerspawns.config;

public class Config {
    protected static Config instance = new Config();
    public boolean perPlayerSpawns = true;

    public static Config instance() {
        return instance;
    }
}
