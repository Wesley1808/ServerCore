package me.wesley1808.servercore.common.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.file.GenericBuilder;
import com.electronwill.nightconfig.toml.TomlFormat;
import me.wesley1808.servercore.common.ServerCore;
import me.wesley1808.servercore.common.config.tables.*;
import me.wesley1808.servercore.common.dynamic.DynamicSetting;
import me.wesley1808.servercore.common.services.platform.PlatformHelper;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.function.BiConsumer;

public class Config {
    @Nullable
    private static GenericBuilder<CommentedConfig, CommentedFileConfig> configBuilder;

    static {
        // Required to generate the config with the correct order.
        System.setProperty("nightconfig.preserveInsertionOrder", "true");

        // Initialize the config builder.
        Path path = PlatformHelper.getConfigDir().resolve("servercore.toml");
        try {
            configBuilder = CommentedFileConfig.builder(path, TomlFormat.instance()).preserveInsertionOrder().sync();
        } catch (Throwable throwable) {
            ServerCore.LOGGER.error("[ServerCore] Unable to initialize config builder: {}", throwable.getMessage());
            ServerCore.LOGGER.error("[ServerCore] Load and save operations on the config file will not be available.");
            configBuilder = null;
        }
    }

    public static void load(boolean afterMixinLoad) {
        if (configBuilder != null) {
            CommentedFileConfig config = configBuilder.build();
            config.load();
            config.close();

            for (Table table : Table.values()) {
                Config.validate(table, config);

                if (afterMixinLoad || table.loadBeforeMixins) {
                    Config.loadEntries(config.get(table.key), table.clazz);
                }
            }

            Config.loadChanges();
        }
    }

    public static void save() {
        if (configBuilder != null) {
            CommentedFileConfig config = configBuilder.build();
            for (Table table : Table.values()) {
                Config.validate(table, config);
                Config.saveEntries(config.get(table.key), table.clazz);
                config.setComment(table.key, table.comment);
            }

            config.save();
            config.close();
        }
    }

    public static boolean isConfigAvailable() {
        return configBuilder != null;
    }

    private static void loadChanges() {
        DynamicSetting.loadCustomOrder();
    }

    // Creates table when missing.
    private static void validate(Table table, CommentedFileConfig config) {
        if (!config.contains(table.key)) {
            config.add(table.key, CommentedConfig.inMemory());
        }
    }

    private static void loadEntries(CommentedConfig config, Class<?> clazz) {
        try {
            Config.forEachEntry(clazz, (field, entry) -> {
                final String key = field.getName().toLowerCase();
                final Object value = config.getOrElse(key, entry.getDefault());
                if (!entry.set(value)) {
                    ServerCore.LOGGER.error("[ServerCore] Invalid config entry found! {} = {} (Reverting back to default: {})", key, value, entry.getDefault());
                }
            });
        } catch (Throwable throwable) {
            ServerCore.LOGGER.error("[ServerCore] An error occurred whilst loading config entries!", throwable);
        }
    }

    private static void saveEntries(CommentedConfig config, Class<?> clazz) {
        try {
            config.clear();
            Config.forEachEntry(clazz, (field, entry) -> {
                final String key = field.getName().toLowerCase();
                final String comment = entry.getComment();
                config.set(key, entry.get());
                if (comment != null) {
                    config.setComment(key, " " + comment.replace("\n", "\n "));
                }
            });
        } catch (Throwable throwable) {
            ServerCore.LOGGER.error("[ServerCore] An error occurred whilst saving configs!", throwable);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> void forEachEntry(Class<?> clazz, BiConsumer<Field, ConfigEntry<T>> consumer) throws IllegalAccessException {
        for (Field field : clazz.getFields()) {
            if (field.get(clazz) instanceof ConfigEntry entry) {
                consumer.accept(field, entry);
            }
        }
    }

    public enum Table {
        FEATURES(FeatureConfig.class, false, "Lets you enable / disable certain features and modify them."),
        DYNAMIC(DynamicConfig.class, false, "Modifies mobcaps, no-chunk-tick, simulation and view-distance depending on the MSPT."),
        BREEDING_CAP(EntityLimitConfig.class, false, "Stops animals / villagers from breeding if there are too many of the same type nearby."),
        OPTIMIZATIONS(OptimizationConfig.class, true, "Allows you to toggle specific optimizations that don't have full vanilla parity.\nThese settings will only take effect after server restarts."),
        COMMANDS(CommandConfig.class, false, "Allows you to disable specific commands and modify the way some of them are formatted."),
        ACTIVATION_RANGE(ActivationRangeConfig.class, false, "Stops entities from ticking if they are too far away.");

        public final String key;
        public final String comment;
        public final Class<?> clazz;
        public final boolean loadBeforeMixins;

        Table(Class<?> clazz, boolean loadBeforeMixins, String comment) {
            this.key = this.name().toLowerCase();
            this.comment = " " + comment.replace("\n", "\n ");
            this.clazz = clazz;
            this.loadBeforeMixins = loadBeforeMixins;
        }
    }
}