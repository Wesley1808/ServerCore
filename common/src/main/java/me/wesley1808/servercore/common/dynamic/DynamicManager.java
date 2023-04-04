package me.wesley1808.servercore.common.dynamic;

import me.wesley1808.servercore.common.config.tables.CommandConfig;
import me.wesley1808.servercore.common.interfaces.IMinecraftServer;
import me.wesley1808.servercore.common.interfaces.IMobCategory;
import me.wesley1808.servercore.common.services.Platform;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.MobCategory;

import static me.wesley1808.servercore.common.config.tables.DynamicConfig.*;
import static me.wesley1808.servercore.common.dynamic.DynamicSetting.*;

public class DynamicManager {
    private final MinecraftServer server;
    private final boolean isClient;
    private double averageTickTime;
    private int count;

    public DynamicManager(MinecraftServer server) {
        this.server = server;
        this.isClient = server.isSingleplayer();

        if (ENABLED.get()) {
            final int maxViewDistance = MAX_VIEW_DISTANCE.get();
            final int maxSimDistance = MAX_SIMULATION_DISTANCE.get();
            if (server.getPlayerList().getViewDistance() > maxViewDistance) {
                this.modifyViewDistance(maxViewDistance);
            }

            if (server.getPlayerList().getSimulationDistance() > maxSimDistance) {
                this.modifySimulationDistance(maxSimDistance);
            }
        }
    }

    public static DynamicManager getInstance(MinecraftServer server) {
        return ((IMinecraftServer) server).getDynamicManager();
    }

    public static String getModifierAsPercentage() {
        return String.format("%.0f%%", MOBCAP_MULTIPLIER.get() * 100);
    }

    public static String createStatusReport(String title) {
        return title + "\n" + CommandConfig.STATUS_CONTENT.get()
                .replace("${version}", Platform.INSTANCE.getVersion())
                .replace("${mobcap_percentage}", getModifierAsPercentage())
                .replace("${chunk_tick_distance}", String.format("%.0f", CHUNK_TICK_DISTANCE.get()))
                .replace("${simulation_distance}", String.format("%.0f", SIMULATION_DISTANCE.get()))
                .replace("${view_distance}", String.format("%.0f", VIEW_DISTANCE.get()));
    }

    public static void update(MinecraftServer server) {
        if (server.getTickCount() % 20 == 0) {
            DynamicManager manager = getInstance(server);
            manager.updateValues();

            if (ENABLED.get()) {
                manager.runPerformanceChecks();
            }
        }
    }

    private void updateValues() {
        this.averageTickTime = this.calculateAverageTickTime();
        this.count++;
    }

    protected double calculateAverageTickTime() {
        return this.server.getAverageTickTime();
    }

    private void runPerformanceChecks() {
        final double targetMspt = TARGET_MSPT.get();
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

    public void modifyMobcaps(double modifier) {
        for (MobCategory category : MobCategory.values()) {
            if ((Object) category instanceof IMobCategory mobCategory) {
                mobCategory.modifyCapacity(modifier);
            }
        }
    }

    public double getAverageTickTime() {
        return this.averageTickTime;
    }
}