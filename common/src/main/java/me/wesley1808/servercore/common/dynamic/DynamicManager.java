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
            DynamicSetting viewDistance = DynamicSetting.VIEW_DISTANCE;
            if (viewDistance.isEnabled()) {
                int max = viewDistance.getMax();
                if (server.getPlayerList().getViewDistance() > max) {
                    this.modifyViewDistance(max);
                }
            }

            DynamicSetting simulationDistance = DynamicSetting.SIMULATION_DISTANCE;
            if (simulationDistance.isEnabled()) {
                int max = simulationDistance.getMax();
                if (server.getPlayerList().getSimulationDistance() > max) {
                    this.modifySimulationDistance(max);
                }
            }

            DynamicSetting mobcaps = DynamicSetting.MOBCAP_PERCENTAGE;
            if (mobcaps.isEnabled()) {
                DynamicManager.modifyMobcaps(mobcaps.getMax());
            }
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