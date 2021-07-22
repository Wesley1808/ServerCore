package org.provim.servertools;

import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ServerTools {
    private static final Logger LOGGER = LogManager.getLogger();
    private static MinecraftServer server;

    private ServerTools() {
    }

    public static MinecraftServer getServer() {
        return server;
    }

    public static void setServer(MinecraftServer server) {
        ServerTools.server = server;
    }

    public static Logger getLogger() {
        return LOGGER;
    }
}
