package me.wesley1808.servercore.fabric.common;

import me.wesley1808.servercore.common.ServerCore;
import me.wesley1808.servercore.common.services.Events;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class ServerCoreFabric extends ServerCore implements ModInitializer {
    @Override
    public void onInitialize() {
        this.initialize();

        // Placeholders
        PlaceHolders.register();

        // Events
        ServerTickEvents.END_SERVER_TICK.register(Events::onTick);
        ServerLifecycleEvents.SERVER_STARTED.register(Events::onServerStarted);
        ServerLifecycleEvents.SERVER_STOPPED.register(Events::onShutdown);
        CommandRegistrationCallback.EVENT.register(Events::registerCommands);
    }
}
