package me.wesley1808.servercore.utils;

import com.mojang.brigadier.CommandDispatcher;
import me.wesley1808.servercore.ServerCore;
import me.wesley1808.servercore.commands.MobcapsCommand;
import me.wesley1808.servercore.commands.ServerCoreCommand;
import me.wesley1808.servercore.commands.StatisticsCommand;
import me.wesley1808.servercore.config.Config;
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
        TickManager.update(server);
    }

    private static void onServerStarted(MinecraftServer server) {
        ServerCore.setServer(server);
        TickManager.initValues(server.getPlayerList());
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
