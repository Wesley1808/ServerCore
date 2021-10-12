package org.provim.servercore.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import net.fabricmc.loader.api.FabricLoader;
import org.provim.servercore.ServerCore;

import java.nio.file.Path;

public final class Config {
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("servercore.toml");
    private static final CommentedFileConfig CONFIG = CommentedFileConfig.builder(CONFIG_PATH).preserveInsertionOrder().sync().build();
    private static final String[] TABLES = {"features", "dynamic", "entity_limits", "commands"};

    private static CommandConfig commandConfig;
    private static FeatureConfig featureConfig;
    private static DynamicConfig dynamicConfig;
    private static EntityConfig entityConfig;

    static {
        // Required to generate the config with the correct order.
        System.setProperty("nightconfig.preserveInsertionOrder", "true");
    }

    public static void load() {
        CONFIG.load();
        boolean modified = Config.validate();

        try {
            featureConfig = new FeatureConfig(CONFIG.get(TABLES[0]));
            dynamicConfig = new DynamicConfig(CONFIG.get(TABLES[1]));
            entityConfig = new EntityConfig(CONFIG.get(TABLES[2]));
            commandConfig = new CommandConfig(CONFIG.get(TABLES[3]));
        } catch (Exception ex) {
            ServerCore.getLogger().fatal("[ServerCore] Found invalid config entries! Fix these or reset the config.");
            CONFIG.close();
            throw ex;
        }

        if (modified) Config.save(false);
    }

    public static void save(boolean validate) {
        if (validate) Config.validate();

        CONFIG.setComment(TABLES[0], " Lets you enable / disable certain features and modify them.");
        featureConfig.save(CONFIG.get(TABLES[0]));

        CONFIG.setComment(TABLES[1], " Modifies mobcaps, no-chunk-tick, simulation and view-distance depending on the MSPT.");
        dynamicConfig.save(CONFIG.get(TABLES[1]));

        CONFIG.setComment(TABLES[2], " Stops animals / villagers from breeding if there are too many of the same type nearby.");
        entityConfig.save(CONFIG.get(TABLES[2]));

        CONFIG.setComment(TABLES[3], " Allows you to disable specific commands and modify the way some of them are formatted.");
        commandConfig.save(CONFIG.get(TABLES[3]));

        CONFIG.save();
    }

    public static void close() {
        CONFIG.close();
    }

    private static boolean validate() {
        boolean modified = false;
        for (String table : TABLES) {
            if (!CONFIG.contains(table)) {
                CONFIG.set(table, CommentedConfig.inMemory());
                modified = true;
            }
        }

        return modified;
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
}