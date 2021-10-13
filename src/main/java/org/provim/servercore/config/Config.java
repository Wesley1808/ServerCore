package org.provim.servercore.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import net.fabricmc.loader.api.FabricLoader;
import org.provim.servercore.ServerCore;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.function.BiConsumer;

public final class Config {
    public static final CommandConfig COMMAND_CONFIG = new CommandConfig();
    public static final FeatureConfig FEATURE_CONFIG = new FeatureConfig();
    public static final DynamicConfig DYNAMIC_CONFIG = new DynamicConfig();
    public static final EntityConfig ENTITY_CONFIG = new EntityConfig();

    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("servercore.toml");
    private static final CommentedFileConfig CONFIG = CommentedFileConfig.builder(CONFIG_PATH).preserveInsertionOrder().sync().build();
    private static final String[] TABLES = {"features", "dynamic", "entity_limits", "commands"};

    static {
        // Required to generate the config with the correct order.
        System.setProperty("nightconfig.preserveInsertionOrder", "true");
    }

    public static void load() {
        CONFIG.load();
        boolean modified = Config.validateTables();

        Config.load(CONFIG.get(TABLES[0]), FEATURE_CONFIG);
        Config.load(CONFIG.get(TABLES[1]), DYNAMIC_CONFIG);
        Config.load(CONFIG.get(TABLES[2]), ENTITY_CONFIG);
        Config.load(CONFIG.get(TABLES[3]), COMMAND_CONFIG);

        if (modified) Config.save(false);
    }

    public static void save(boolean validate) {
        if (validate) Config.validateTables();

        CONFIG.setComment(TABLES[0], " Lets you enable / disable certain features and modify them.");
        Config.save(CONFIG.get(TABLES[0]), FEATURE_CONFIG);

        CONFIG.setComment(TABLES[1], " Modifies mobcaps, no-chunk-tick, simulation and view-distance depending on the MSPT.");
        Config.save(CONFIG.get(TABLES[1]), DYNAMIC_CONFIG);

        CONFIG.setComment(TABLES[2], " Stops animals / villagers from breeding if there are too many of the same type nearby.");
        Config.save(CONFIG.get(TABLES[2]), ENTITY_CONFIG);

        CONFIG.setComment(TABLES[3], " Allows you to disable specific commands and modify the way some of them are formatted.");
        Config.save(CONFIG.get(TABLES[3]), COMMAND_CONFIG);

        CONFIG.save();
    }

    public static void close() {
        CONFIG.close();
    }

    private static boolean validateTables() {
        boolean modified = false;
        for (String table : TABLES) {
            if (!CONFIG.contains(table)) {
                CONFIG.set(table, CommentedConfig.inMemory());
                modified = true;
            }
        }

        return modified;
    }

    private static void load(CommentedConfig config, Object obj) {
        try {
            Config.forEachEntry(obj, (key, entry) -> {
                entry.set(config.getOrElse(key, entry.getDefault()));
                config.setComment(key, entry.getComment());
            });
        } catch (Exception ex) {
            ServerCore.getLogger().error("[ServerCore] Exception was thrown whilst loading configs!", ex);
        }
    }

    private static void save(CommentedConfig config, Object obj) {
        try {
            Config.forEachEntry(obj, (key, entry) -> config.set(key, entry.get()));
        } catch (Exception ex) {
            ServerCore.getLogger().error("[ServerCore] Exception was thrown whilst saving configs!", ex);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> void forEachEntry(Object obj, BiConsumer<String, ConfigEntry<T>> consumer) throws IllegalAccessException {
        for (Field field : obj.getClass().getFields()) {
            if (field.get(obj) instanceof ConfigEntry entry) {
                consumer.accept(Config.getName(field.getName()), entry);
            }
        }
    }

    // Converts field names into config names.
    // Example: maxViewDistance -> max_view_distance.
    private static String getName(String name) {
        for (char c : name.toCharArray()) {
            if (Character.isUpperCase(c)) {
                name = name.replace(String.valueOf(c), "_" + Character.toLowerCase(c));
            }
        }
        return name;
    }
}