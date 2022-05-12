package me.wesley1808.servercore.common.services;

import com.mojang.brigadier.CommandDispatcher;
import me.wesley1808.servercore.common.ServerCore;
import me.wesley1808.servercore.common.commands.MobcapsCommand;
import me.wesley1808.servercore.common.commands.ServerCoreCommand;
import me.wesley1808.servercore.common.commands.StatisticsCommand;
import me.wesley1808.servercore.common.config.Config;
import me.wesley1808.servercore.common.utils.DynamicManager;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;

public final class Events {
    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(Events::onTick);
        ServerLifecycleEvents.SERVER_STARTED.register(Events::onServerStarted);
        ServerLifecycleEvents.SERVER_STOPPING.register(Events::onShutdown);
        CommandRegistrationCallback.EVENT.register(Events::registerCommands);
    }

    private static void onTick(MinecraftServer server) {
        DynamicManager.update(server);
    }

    private static void onServerStarted(MinecraftServer server) {
        ServerCore.setServer(server);
        DynamicManager.initValues(server.getPlayerList());
    }

    private static void onShutdown(MinecraftServer server) {
        Config.save();
    }

    private static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, boolean dedicated) {
        ServerCoreCommand.register(dispatcher);
        StatisticsCommand.register(dispatcher);
        MobcapsCommand.register(dispatcher);
    }
}
