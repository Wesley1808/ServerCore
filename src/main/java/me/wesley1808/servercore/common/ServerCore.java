package me.wesley1808.servercore.common;

import com.mojang.logging.LogUtils;
import me.wesley1808.servercore.common.config.Config;
import me.wesley1808.servercore.common.services.Events;
import me.wesley1808.servercore.common.services.PlaceHolders;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;

public class ServerCore implements ModInitializer {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static Logger getLogger() {
        return LOGGER;
    }

    public static void onLoadMixins() {
        LOGGER.info("[ServerCore] Loading...");
        Config.load();
    }

    @Override
    public void onInitialize() {
        PlaceHolders.register();
        Events.register();
    }
}