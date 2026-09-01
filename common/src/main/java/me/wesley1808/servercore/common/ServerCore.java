package me.wesley1808.servercore.common;

import com.mojang.logging.LogUtils;
import me.wesley1808.servercore.common.config.Config;
import me.wesley1808.servercore.common.config.OptimizationConfig;
import me.wesley1808.servercore.common.services.platform.PlatformHelper;
import net.minecraft.world.attribute.EnvironmentAttributes;
import org.slf4j.Logger;

public abstract class ServerCore {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MODID = "servercore";

    public final void initialize() {
        PlatformHelper.initialize();
        Config.reload();
        this.applyOptimizations();

        LOGGER.info("[ServerCore] Loaded V{}!", PlatformHelper.getVersion());
    }

    private void applyOptimizations() {
        OptimizationConfig config = Config.optimizations();

        if (config.fastBiomeLookups()) {
            EnvironmentAttributes.NATURAL_MOB_SPAWNS.fullResolutionBiomes = false;
        }
    }
}