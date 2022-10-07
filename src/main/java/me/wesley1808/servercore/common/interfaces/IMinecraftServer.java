package me.wesley1808.servercore.common.interfaces;

import me.wesley1808.servercore.common.dynamic.DynamicManager;
import me.wesley1808.servercore.common.utils.Statistics;

public interface IMinecraftServer {
    void onStarted();

    Statistics getStatistics();

    DynamicManager getDynamicManager();
}
