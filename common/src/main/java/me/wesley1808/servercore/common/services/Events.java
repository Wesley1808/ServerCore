package me.wesley1808.servercore.common.services;

import com.mojang.brigadier.CommandDispatcher;
import me.wesley1808.servercore.common.commands.MobcapsCommand;
import me.wesley1808.servercore.common.commands.ServerCoreCommand;
import me.wesley1808.servercore.common.commands.StatisticsCommand;
import me.wesley1808.servercore.common.config.Configs;
import me.wesley1808.servercore.common.config.legacy.FeatureConfig;
import me.wesley1808.servercore.common.dynamic.DynamicManager;
import me.wesley1808.servercore.common.dynamic.DynamicSetting;
import me.wesley1808.servercore.common.interfaces.IMinecraftServer;
import me.wesley1808.servercore.common.utils.ChunkManager;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.MinecraftServer;

public class Events {

    public static void onTick(MinecraftServer server) {
        DynamicManager.update(server);
    }

    public static void onServerStarted(MinecraftServer server) {
        Configs.loadChanges();
        IMinecraftServer.onStarted(server);

        // Disable spawn chunks after the server starts up.
        // This is only used for dedicated servers.
        if (server.isPublished() && FeatureConfig.DISABLE_SPAWN_CHUNKS.get()) {
            ChunkManager.disableSpawnChunks(server);
        }
    }

    public static void onShutdown(MinecraftServer server) {
        DynamicSetting.resetAll();
    }

    public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        ServerCoreCommand.register(dispatcher);
        StatisticsCommand.register(dispatcher);
        MobcapsCommand.register(dispatcher);
    }
}
