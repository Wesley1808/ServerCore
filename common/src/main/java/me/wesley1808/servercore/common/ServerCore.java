package me.wesley1808.servercore.common;

import com.mojang.logging.LogUtils;
import me.wesley1808.servercore.common.config.Configs;
import me.wesley1808.servercore.common.services.platform.PlatformHelper;
import org.slf4j.Logger;

public abstract class ServerCore {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MODID = "servercore";

    public final void initialize() {
        PlatformHelper.initialize();
        Configs.reload(true);

        LOGGER.info("[ServerCore] Loaded V{}!", PlatformHelper.getVersion());
    }
}