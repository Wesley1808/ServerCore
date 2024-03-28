package me.wesley1808.servercore.common.config;

import me.wesley1808.servercore.common.activation_range.ActivationRange;
import me.wesley1808.servercore.common.config.files.Config;
import me.wesley1808.servercore.common.config.files.OptimizationConfig;
import me.wesley1808.servercore.common.dynamic.DynamicSetting;
import me.wesley1808.servercore.common.interfaces.IMobCategory;

public class Configs {
    private static final ConfigManager<OptimizationConfig> OPTIMIZATION_MANAGER = ConfigManager.create("optimizations.yml", OptimizationConfig.class);
    private static ConfigManager<Config> configManager;

    public static OptimizationConfig optimizations() {
        return OPTIMIZATION_MANAGER.get();
    }

    public static Config config() {
        return configManager.get();
    }

    public static void reload(boolean afterMixinLoad) {
        if (afterMixinLoad) {
            Configs.getOrCreateConfigManager().reload();
            Configs.loadChanges();
        } else {
            OPTIMIZATION_MANAGER.reload();
        }
    }

    public static void loadChanges() {
        DynamicSetting.loadCustomOrder();
        ActivationRange.reload();
        IMobCategory.reload();
    }

    private static ConfigManager<Config> getOrCreateConfigManager() {
        if (configManager == null) {
            configManager = ConfigManager.create("config.yml", Config.class);
        }
        return configManager;
    }
}