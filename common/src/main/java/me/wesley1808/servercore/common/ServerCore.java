package me.wesley1808.servercore.common;

import com.mojang.logging.LogUtils;
import me.wesley1808.servercore.common.config.Config;
import me.wesley1808.servercore.common.services.Platform;
import org.slf4j.Logger;

public abstract class ServerCore {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MODID = "servercore";

    public final void initialize() {
        Config.load(true);
        Config.save();

        LOGGER.info("[ServerCore] Loaded V{}!", Platform.INSTANCE.getVersion());
    }
}