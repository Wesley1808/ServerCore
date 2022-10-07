package me.wesley1808.servercore.mixin;

import me.wesley1808.servercore.common.dynamic.DynamicManager;
import me.wesley1808.servercore.common.interfaces.IMinecraftServer;
import me.wesley1808.servercore.common.utils.Statistics;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin implements IMinecraftServer {
    @Unique
    private DynamicManager dynamicManager;
    @Unique
    private Statistics statistics;

    @Unique
    @Override
    public void onStarted() {
        MinecraftServer server = (MinecraftServer) (Object) this;
        this.dynamicManager = new DynamicManager(server);
        this.statistics = new Statistics(server);
    }

    @Unique
    @Override
    public Statistics getStatistics() {
        return this.statistics;
    }

    @Unique
    @Override
    public DynamicManager getDynamicManager() {
        return this.dynamicManager;
    }
}
