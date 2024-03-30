package me.wesley1808.servercore.common.dynamic;

import me.wesley1808.servercore.common.config.Config;
import me.wesley1808.servercore.common.config.data.dynamic.DynamicConfig;
import me.wesley1808.servercore.common.interfaces.IMinecraftServer;
import me.wesley1808.servercore.common.interfaces.IMobCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.MobCategory;

public class DynamicManager {
    private final MinecraftServer server;
    private final boolean isClient;
    private double averageTickTime;
    private int count;

    public DynamicManager(MinecraftServer server) {
        this.server = server;
        this.isClient = server.isSingleplayer();

        DynamicConfig config = Config.get().dynamic();
        if (config.enabled()) {
            final int maxViewDistance = DynamicSetting.VIEW_DISTANCE.getMax();
            if (server.getPlayerList().getViewDistance() > maxViewDistance) {
                this.modifyViewDistance(maxViewDistance);
            }

            final int maxSimDistance = DynamicSetting.SIMULATION_DISTANCE.getMax();
            if (server.getPlayerList().getSimulationDistance() > maxSimDistance) {
                this.modifySimulationDistance(maxSimDistance);
            }

            DynamicManager.modifyMobcaps(DynamicSetting.MOBCAP_PERCENTAGE.getMax());
        }
    }

    public static DynamicManager getInstance(MinecraftServer server) {
        return ((IMinecraftServer) server).servercore$getDynamicManager();
    }

    public static void update(MinecraftServer server) {
        if (server.getTickCount() % 20 == 0) {
            DynamicManager manager = getInstance(server);
            manager.updateValues();

            DynamicConfig config = Config.get().dynamic();
            if (config.enabled()) {
                manager.runPerformanceChecks(config);
            }
        }
    }

    private void updateValues() {
        this.averageTickTime = this.calculateAverageTickTime();
        this.count++;
    }

    protected double calculateAverageTickTime() {
        return this.server.getCurrentSmoothedTickTime();
    }

    private void runPerformanceChecks(DynamicConfig config) {
        final double targetMspt = config.targetMspt();
        final boolean decrease = this.averageTickTime > targetMspt + 5;
        final boolean increase = this.averageTickTime < Math.max(targetMspt - 5, 2);

        if (decrease || increase) {
            for (DynamicSetting setting : DynamicSetting.values()) {
                if (setting.shouldRun(this.count) && setting.modify(increase, this)) {
                    break;
                }
            }
        }
    }

    public void modifyViewDistance(int distance) {
        this.server.getPlayerList().setViewDistance(distance);
        if (this.isClient) {
            Minecraft.getInstance().options.renderDistance().set(distance);
        }
    }

    public void modifySimulationDistance(int distance) {
        this.server.getPlayerList().setSimulationDistance(distance);
        if (this.isClient) {
            Minecraft.getInstance().options.simulationDistance().set(distance);
        }
    }

    public static void modifyMobcaps(int percentage) {
        final double modifier = percentage / 100F;
        for (MobCategory category : MobCategory.values()) {
            IMobCategory.modifyCapacity(category, modifier);
        }
    }

    public double getAverageTickTime() {
        return this.averageTickTime;
    }
}