package me.wesley1808.servercore.common.config;

import me.wesley1808.servercore.common.dynamic.DynamicSetting;
import me.wesley1808.servercore.common.interfaces.IMobCategory;

public class Config {
    private static final ConfigManager<OptimizationConfig> OPTIMIZATION_MANAGER = ConfigManager.create("optimizations.yml", OptimizationConfig.class);
    private static ConfigManager<MainConfig> mainConfigManager;

    public static OptimizationConfig optimizations() {
        return OPTIMIZATION_MANAGER.get();
    }

    public static MainConfig main() {
        return mainConfigManager.get();
    }

    public static void reload(boolean afterMixinLoad) {
        if (afterMixinLoad) {
            Config.getOrCreateConfigManager().reload();
            Config.loadChanges();
        } else {
            OPTIMIZATION_MANAGER.reload();
        }
    }

    public static void loadChanges() {
        DynamicSetting.loadCustomOrder();
        IMobCategory.reload();
    }

    private static ConfigManager<MainConfig> getOrCreateConfigManager() {
        if (mainConfigManager == null) {
            mainConfigManager = ConfigManager.create("config.yml", MainConfig.class);
        }
        return mainConfigManager;
    }
}