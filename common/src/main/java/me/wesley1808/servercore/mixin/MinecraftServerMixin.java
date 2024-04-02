package me.wesley1808.servercore.mixin;

import me.wesley1808.servercore.common.dynamic.DynamicManager;
import me.wesley1808.servercore.common.dynamic.SparkDynamicManager;
import me.wesley1808.servercore.common.interfaces.IMinecraftServer;
import me.wesley1808.servercore.common.utils.ModCompat;
import me.wesley1808.servercore.common.utils.statistics.Statistics;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin implements IMinecraftServer {
    @Unique
    private DynamicManager servercore$dynamicManager;
    @Unique
    private Statistics servercore$statistics;

    @Override
    public void servercore$onStarted(MinecraftServer server) {
        this.servercore$dynamicManager = ModCompat.SPARK ? new SparkDynamicManager(server) : new DynamicManager(server);
        this.servercore$statistics = new Statistics(server);
    }

    @Unique
    @Override
    public Statistics servercore$getStatistics() {
        return this.servercore$statistics;
    }

    @Unique
    @Override
    public DynamicManager servercore$getDynamicManager() {
        return this.servercore$dynamicManager;
    }
}
