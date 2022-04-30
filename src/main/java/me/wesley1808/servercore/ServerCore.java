package me.wesley1808.servercore;

import com.mojang.logging.LogUtils;
import me.wesley1808.servercore.utils.Events;
import me.wesley1808.servercore.utils.Placeholders;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;

import java.util.Optional;

public class ServerCore implements ModInitializer {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static MinecraftServer server;
    private static String version;

    public static String getVersion() {
        return version;
    }

    public static Logger getLogger() {
        return LOGGER;
    }

    public static MinecraftServer getServer() {
        return server;
    }

    public static void setServer(MinecraftServer server) {
        ServerCore.server = server;
    }

    @Override
    public void onInitialize() {
        LOGGER.info("[ServerCore] Initializing...");

        version = this.findVersion();
        Placeholders.register();
        Events.register();
    }

    private String findVersion() {
        Optional<ModContainer> optional = FabricLoader.getInstance().getModContainer("servercore");
        return optional.map(container -> container.getMetadata().getVersion().getFriendlyString()).orElse("Unknown");
    }
}
