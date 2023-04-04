package me.wesley1808.servercore.common;

import me.wesley1808.servercore.common.services.Events;
import me.wesley1808.servercore.common.services.PlaceHolders;
import net.fabricmc.api.ModInitializer;

public class ServerCoreFabric extends ServerCore implements ModInitializer {
    @Override
    public void onInitialize() {
        this.initialize();
    }

    @Override
    public void register() {
        Events.register();
        PlaceHolders.register();
    }
}
