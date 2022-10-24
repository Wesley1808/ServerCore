package me.wesley1808.servercore.common.services;

import com.mojang.brigadier.CommandDispatcher;
import me.wesley1808.servercore.common.commands.MobcapsCommand;
import me.wesley1808.servercore.common.commands.ServerCoreCommand;
import me.wesley1808.servercore.common.commands.StatisticsCommand;
import me.wesley1808.servercore.common.config.Config;
import me.wesley1808.servercore.common.config.tables.FeatureConfig;
import me.wesley1808.servercore.common.dynamic.DynamicManager;
import me.wesley1808.servercore.common.dynamic.DynamicSetting;
import me.wesley1808.servercore.common.interfaces.IMinecraftServer;
import me.wesley1808.servercore.common.utils.ChunkManager;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.MinecraftServer;

public class Events {
    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(Events::onTick);
        ServerLifecycleEvents.SERVER_STARTED.register(Events::onServerStarted);
        ServerLifecycleEvents.SERVER_STOPPED.register(Events::onShutdown);
        CommandRegistrationCallback.EVENT.register(Events::registerCommands);
    }

    private static void onTick(MinecraftServer server) {
        DynamicManager.update(server);
    }

    private static void onServerStarted(MinecraftServer server) {
        ((IMinecraftServer) server).onStarted();

        // Disable spawn chunks after the server starts up.
        // This is only used for dedicated servers.
        if (server.isPublished() && FeatureConfig.DISABLE_SPAWN_CHUNKS.get()) {
            ChunkManager.disableSpawnChunks(server);
        }
    }

    private static void onShutdown(MinecraftServer server) {
        DynamicSetting.resetAll();
        Config.save();
    }

    private static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        ServerCoreCommand.register(dispatcher);
        StatisticsCommand.register(dispatcher);
        MobcapsCommand.register(dispatcher);
    }
}
