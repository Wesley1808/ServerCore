package org.provim.servercore.config;

import com.moandjiezana.toml.Toml;
import net.fabricmc.loader.api.FabricLoader;
import org.provim.servercore.ServerCore;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

public final class Config {
    private static final File CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve("servercore.toml").toFile();
    private static final String[] TABLES = {"commands", "features", "dynamic", "entity_limits"};
    private static CommandConfig commandConfig;
    private static FeatureConfig featureConfig;
    private static DynamicConfig dynamicConfig;
    private static EntityConfig entityConfig;
    private static Toml toml = new Toml();

    private Config() {
    }

    public static CommandConfig getCommandConfig() {
        return commandConfig;
    }

    public static FeatureConfig getFeatureConfig() {
        return featureConfig;
    }

    public static DynamicConfig getDynamicConfig() {
        return dynamicConfig;
    }

    public static EntityConfig getEntityConfig() {
        return entityConfig;
    }

    public static void load() {
        if (!CONFIG_FILE.exists()) {
            Config.createDefaultConfig();
        }

        toml = toml.read(CONFIG_FILE);
        Config.validateToml();

        commandConfig = new CommandConfig(toml.getTable(TABLES[0]));
        featureConfig = new FeatureConfig(toml.getTable(TABLES[1]));
        dynamicConfig = new DynamicConfig(toml.getTable(TABLES[2]));
        entityConfig = new EntityConfig(toml.getTable(TABLES[3]));
    }

    private static void validateToml() {
        for (String table : TABLES) {
            if (!toml.containsTable(table)) {
                Config.createDefaultConfig();
                toml = toml.read(CONFIG_FILE);
                return;
            }
        }
    }

    private static void createDefaultConfig() {
        try {
            if (CONFIG_FILE.delete()) {
                ServerCore.getLogger().warn("[ServerCore] Invalid config found! Creating new default config...");
            } else {
                ServerCore.getLogger().info("[ServerCore] Creating new default config...");
            }

            Files.copy(Objects.requireNonNull(Config.class.getResourceAsStream("/config/servercore.toml")), CONFIG_FILE.toPath());
        } catch (IOException e) {
            ServerCore.getLogger().error("[ServerCore] Failed to create default config.", e);
            throw new RuntimeException();
        }
    }
}