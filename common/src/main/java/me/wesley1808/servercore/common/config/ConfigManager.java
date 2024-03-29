package me.wesley1808.servercore.common.config;

import me.wesley1808.servercore.common.ServerCore;
import me.wesley1808.servercore.common.services.platform.PlatformHelper;
import org.apache.commons.lang3.StringUtils;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import space.arim.dazzleconf.ConfigurationFactory;
import space.arim.dazzleconf.ConfigurationOptions;
import space.arim.dazzleconf.error.ConfigFormatSyntaxException;
import space.arim.dazzleconf.error.InvalidConfigException;
import space.arim.dazzleconf.ext.snakeyaml.CommentMode;
import space.arim.dazzleconf.ext.snakeyaml.SnakeYamlConfigurationFactory;
import space.arim.dazzleconf.ext.snakeyaml.SnakeYamlOptions;
import space.arim.dazzleconf.helper.ConfigurationHelper;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class ConfigManager<C extends Copyable> {
    private static final Path CONFIG_DIR = PlatformHelper.getConfigDir().resolve(ServerCore.MODID);
    private final ConfigurationHelper<C> helper;
    private final String fileName;
    private C data;

    private ConfigManager(ConfigurationHelper<C> helper, String fileName) {
        this.helper = helper;
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

        return new ConfigManager<>(new ConfigurationHelper<>(CONFIG_DIR, fileName, configFactory), fileName);
    }

    public boolean reload() {
        try {
            C loadedData = this.helper.reloadConfigData();
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
            C defaultData = this.helper.getFactory().loadDefaults();
            this.copyAndSetData(defaultData);
        }

        return false;
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

    private void copyAndSetData(C source) {
        // noinspection unchecked
        this.data = (C) source.optimizedCopy();
    }
}
