package me.wesley1808.servercore.common.config;

import me.wesley1808.servercore.common.dynamic.DynamicSetting;
import me.wesley1808.servercore.common.interfaces.IMobCategory;

public class Config {
    private static final ConfigManager<OptimizationConfig> OPTIMIZATION_MANAGER = ConfigManager.create("optimizations.yml", OptimizationConfig.class);
    private static ConfigManager<MainConfig> mainConfigManager;
    private static boolean shouldValidate;

    public static OptimizationConfig optimizations() {
        return OPTIMIZATION_MANAGER.get();
    }

    public static MainConfig get() {
        return mainConfigManager.get();
    }

    public static void loadOptimizationConfig() {
        OPTIMIZATION_MANAGER.reload();
    }

    public static boolean reloadConfigs() {
        Config.loadOptimizationConfig();
        boolean success = Config.getOrCreateConfigManager().reload();
        Config.loadChanges();
        return success;
    }

    public static void loadChanges() {
        DynamicSetting.reload();
        IMobCategory.reload();
    }

    private static ConfigManager<MainConfig> getOrCreateConfigManager() {
        if (mainConfigManager == null) {
            mainConfigManager = ConfigManager.create("config.yml", MainConfig.class);
        }
        return mainConfigManager;
    }

    public static void enableValidation() {
        shouldValidate = true;
    }

    public static boolean shouldValidate() {
        return shouldValidate;
    }
}