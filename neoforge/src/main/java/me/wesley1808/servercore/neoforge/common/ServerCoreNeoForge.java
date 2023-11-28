package me.wesley1808.servercore.neoforge.common;

import me.wesley1808.servercore.common.ServerCore;
import me.wesley1808.servercore.common.services.Events;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.server.permission.events.PermissionGatherEvent;;

@Mod(ServerCore.MODID)
public class ServerCoreNeoForge extends ServerCore {
    public ServerCoreNeoForge() {
        this.initialize();
        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Events.onTick(event.getServer());
        }
    }

    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event) {
        Events.onServerStarted(event.getServer());
    }

    @SubscribeEvent
    public void onServerStopped(ServerStoppedEvent event) {
        Events.onShutdown(event.getServer());
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        Events.registerCommands(event.getDispatcher(), event.getBuildContext(), event.getCommandSelection());
    }

    @SubscribeEvent
    public void handlePermissionNodesGather(PermissionGatherEvent.Nodes event) {
        NeoForgePermissions.addNodes(event);
    }
}