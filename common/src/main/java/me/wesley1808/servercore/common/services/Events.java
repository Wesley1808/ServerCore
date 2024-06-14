package me.wesley1808.servercore.common.services;

import com.mojang.brigadier.CommandDispatcher;
import me.wesley1808.servercore.common.commands.MobcapsCommand;
import me.wesley1808.servercore.common.commands.ServerCoreCommand;
import me.wesley1808.servercore.common.commands.StatisticsCommand;
import me.wesley1808.servercore.common.config.Config;
import me.wesley1808.servercore.common.dynamic.DynamicManager;
import me.wesley1808.servercore.common.dynamic.DynamicSetting;
import me.wesley1808.servercore.common.interfaces.IMinecraftServer;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.MinecraftServer;

public class Events {

    public static void onTick(MinecraftServer server) {
        DynamicManager.update(server);
    }

    public static void onServerStarted(MinecraftServer server) {
        Config.enableValidation();
        Config.reloadConfigs();

        IMinecraftServer.onStarted(server);
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
