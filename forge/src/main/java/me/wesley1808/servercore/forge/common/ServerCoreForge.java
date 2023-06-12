package me.wesley1808.servercore.forge.common;

import me.wesley1808.servercore.common.ServerCore;
import me.wesley1808.servercore.common.services.Events;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.permission.events.PermissionGatherEvent;

@Mod(ServerCore.MODID)
public class ServerCoreForge extends ServerCore {
    public ServerCoreForge() {
        this.initialize();
        MinecraftForge.EVENT_BUS.register(this);
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
        ForgePermissions.addNodes(event);
    }
}