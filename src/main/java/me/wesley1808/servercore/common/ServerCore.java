package me.wesley1808.servercore.common;

import com.mojang.logging.LogUtils;
import me.wesley1808.servercore.common.config.Config;
import me.wesley1808.servercore.common.services.Environment;
import me.wesley1808.servercore.common.services.Events;
import me.wesley1808.servercore.common.services.PlaceHolders;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;

public class ServerCore implements ModInitializer {
    public static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onInitialize() {
        LOGGER.info("[ServerCore] Loaded V{}!", Environment.VERSION);
        PlaceHolders.register();
        Events.register();

        // Load the config.
        Config.load(true);
        Config.save();
    }
}