package org.provim.servercore;

import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.provim.servercore.config.Config;

public final class ServerCore {
    private static final Logger LOGGER = LogManager.getLogger();
    private static MinecraftServer server;

    private ServerCore() {
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
}
