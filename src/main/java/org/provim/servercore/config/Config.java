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
    private static FeatureConfig featureConfig;
    private static DynamicConfig dynamicConfig;
    private static EntityConfig entityConfig;
    private static Toml toml = new Toml();

    private Config() {
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
        validateFile();
        toml = toml.read(CONFIG_FILE);
        featureConfig = new FeatureConfig(toml);
        dynamicConfig = new DynamicConfig(toml);
        entityConfig = new EntityConfig(toml);
    }

    private static void validateFile() {
        if (!CONFIG_FILE.exists()) {
            try {
                Files.copy(Objects.requireNonNull(Config.class.getResourceAsStream("/config/servercore.toml")), CONFIG_FILE.toPath());
            } catch (IOException e) {
                ServerCore.getLogger().error("Failed to create config.", e);
                throw new RuntimeException();
            }
        }
    }
}