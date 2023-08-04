package me.wesley1808.servercore.common.interfaces;

import me.wesley1808.servercore.common.dynamic.DynamicManager;
import me.wesley1808.servercore.common.utils.Statistics;
import net.minecraft.server.MinecraftServer;

public interface IMinecraftServer {
    void servercore$onStarted(MinecraftServer server);

    Statistics servercore$getStatistics();

    DynamicManager servercore$getDynamicManager();

    static void onStarted(MinecraftServer server) {
        ((IMinecraftServer) server).servercore$onStarted(server);
    }
}
