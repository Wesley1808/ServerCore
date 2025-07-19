package me.wesley1808.servercore.common.config;

import me.wesley1808.servercore.common.ServerCore;
import me.wesley1808.servercore.common.services.platform.PlatformHelper;
import org.apache.commons.lang3.StringUtils;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import space.arim.dazzleconf.AuxiliaryKeys;
import space.arim.dazzleconf.ConfigurationFactory;
import space.arim.dazzleconf.ConfigurationOptions;
import space.arim.dazzleconf.error.ConfigFormatSyntaxException;
import space.arim.dazzleconf.error.InvalidConfigException;
import space.arim.dazzleconf.ext.snakeyaml.CommentMode;
import space.arim.dazzleconf.ext.snakeyaml.SnakeYamlConfigurationFactory;
import space.arim.dazzleconf.ext.snakeyaml.SnakeYamlOptions;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class ConfigManager<C extends Copyable> {
    private static final Path CONFIG_DIR = PlatformHelper.getConfigDir().resolve(ServerCore.MODID);
    private final ConfigurationFactory<C> factory;
    private final String fileName;
    private C data;

    private ConfigManager(ConfigurationFactory<C> factory, String fileName) {
        this.factory = factory;
        this.fileName = fileName;
    }

    public static <C extends Copyable> ConfigManager<C> create(String fileName, Class<C> configClass) {
        SnakeYamlOptions yamlOptions = new SnakeYamlOptions.Builder()
                .commentMode(CommentMode.alternativeWriter("# %s"))
                .charset(StandardCharsets.UTF_8)
                .yamlSupplier(() -> {
                    DumperOptions opts = new DumperOptions();
                    opts.setProcessComments(true);
                    opts.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
                    opts.setDefaultScalarStyle(DumperOptions.ScalarStyle.PLAIN);
                    opts.setPrettyFlow(true);
                    opts.setSplitLines(false);
                    return new Yaml(opts);
                })
                .build();

        ConfigurationOptions options = new ConfigurationOptions.Builder()
                .sorter(new AnnotationBasedSorter())
                .build();

        ConfigurationFactory<C> configFactory = SnakeYamlConfigurationFactory.create(
                configClass,
                options,
                yamlOptions
        );

        return new ConfigManager<>(configFactory, fileName);
    }

    public boolean reload() {
        try {
            C loadedData = this.reloadConfigData();
            this.copyAndSetData(loadedData);
            return true;
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        } catch (ConfigFormatSyntaxException ex) {
            ServerCore.LOGGER.error("[ServerCore] The yaml syntax in {} is invalid. Check your YAML syntax with a tool such as https://yaml-online-parser.appspot.com/", this.fileName);
            this.printError(ex.getMessage());
        } catch (InvalidConfigException ex) {
            ServerCore.LOGGER.error("[ServerCore] One of the values in {} is not valid.", this.fileName);
            this.printError(ex.getMessage());
        }

        // If the config is not loaded yet, load the default values.
        if (this.data == null) {
            C defaultData = this.factory.loadDefaults();
            this.copyAndSetData(defaultData);
        }

        return false;
    }

    private C reloadConfigData() throws IOException, InvalidConfigException {
        // Create parent directory if it does not exist.
        Files.createDirectories(CONFIG_DIR);

        C defaults = this.factory.loadDefaults();

        Path configPath = CONFIG_DIR.resolve(this.fileName);
        if (!Files.exists(configPath)) {
            // Copy default config data.
            try (FileChannel fileChannel = FileChannel.open(configPath, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE)) {
                this.factory.write(defaults, fileChannel);
            }
            return defaults;
        }

        C loadedData;
        try (FileChannel fileChannel = FileChannel.open(configPath, StandardOpenOption.READ)) {
            loadedData = this.factory.load(fileChannel, defaults);
        }

        // Makes sure not to write temporarily invalid data to the config file.
        if (Config.shouldValidate() && loadedData instanceof AuxiliaryKeys) {
            // Update config with latest keys.
            try (FileChannel fileChannel = FileChannel.open(configPath, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
                this.factory.write(loadedData, fileChannel);
            }
        }
        return loadedData;
    }

    private void printError(String message) {
        String[] errors = StringUtils.split(message, System.lineSeparator());
        String separator = "-".repeat(100);

        ServerCore.LOGGER.error(separator);
        for (String error : errors) {
            ServerCore.LOGGER.error(error);
        }
        ServerCore.LOGGER.error(separator);
    }

    public C get() {
        return this.data;
    }

    public boolean isLoaded() {
        return this.data != null;
    }

    private void copyAndSetData(C source) {
        // noinspection unchecked
        this.data = (C) source.optimizedCopy();
    }
}
