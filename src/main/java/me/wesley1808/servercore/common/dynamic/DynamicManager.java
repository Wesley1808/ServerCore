package me.wesley1808.servercore.common.dynamic;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.lucko.spark.api.SparkProvider;
import me.lucko.spark.api.statistic.StatisticWindow;
import me.lucko.spark.api.statistic.misc.DoubleAverageInfo;
import me.lucko.spark.api.statistic.types.GenericStatistic;
import me.wesley1808.servercore.common.ServerCore;
import me.wesley1808.servercore.common.config.tables.CommandConfig;
import me.wesley1808.servercore.common.interfaces.IMobCategory;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.MobCategory;

import static me.wesley1808.servercore.common.config.tables.DynamicConfig.*;
import static me.wesley1808.servercore.common.dynamic.DynamicSetting.*;

public class DynamicManager {
    private static final boolean SPARK_LOADED = FabricLoader.getInstance().isModLoaded("spark");
    private static GenericStatistic<DoubleAverageInfo, StatisticWindow.MillisPerTick> tickStatistics;
    private static double averageTickTime = 0.0;
    private static int count = 0;

    public static void initialize(PlayerList playerList) {
        if (ENABLED.get()) {
            final int maxViewDistance = MAX_VIEW_DISTANCE.get();
            final int maxSimDistance = MAX_SIMULATION_DISTANCE.get();
            if (playerList.getViewDistance() > maxViewDistance) modifyViewDistance(maxViewDistance);
            if (playerList.getSimulationDistance() > maxSimDistance) modifySimulationDistance(maxSimDistance);
        }
    }

    public static void loadCustomSettingsOrder() {
        ObjectArrayList<DynamicSetting> settings = new ObjectArrayList<>();
        for (String key : SETTING_ORDER.get()) {
            settings.add(DynamicSetting.valueOf(key.toUpperCase()));
        }

        for (int i = 0; i < settings.size(); i++) {
            DynamicSetting setting = settings.get(i);
            DynamicSetting prev = i == 0 ? null : settings.get(i - 1);
            DynamicSetting next = i == settings.size() - 1 ? null : settings.get(i + 1);
            setting.initialize(prev, next);
        }
    }

    public static void update(MinecraftServer server) {
        if (server.getTickCount() % 20 == 0) {
            updateValues(server);

            if (ENABLED.get()) {
                runPerformanceChecks();
            }
        }
    }

    private static void updateValues(MinecraftServer server) {
        if (SPARK_LOADED && tickStatistics == null) {
            tickStatistics = SparkProvider.get().mspt();
        }

        if (tickStatistics != null) {
            averageTickTime = tickStatistics.poll(StatisticWindow.MillisPerTick.SECONDS_10).median();
        } else {
            averageTickTime = server.getAverageTickTime();
        }

        count++;
    }

    private static void runPerformanceChecks() {
        final double targetMspt = TARGET_MSPT.get();
        final boolean decrease = averageTickTime > targetMspt + 5;
        final boolean increase = averageTickTime < Math.max(targetMspt - 5, 2);

        if (decrease || increase) {
            for (DynamicSetting setting : DynamicSetting.values()) {
                if (setting.shouldRun(count) && setting.modify(increase)) {
                    break;
                }
            }
        }
    }

    public static void modifyViewDistance(int distance) {
        ServerCore.getServer().getPlayerList().setViewDistance(distance);
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            Minecraft.getInstance().options.renderDistance().set(distance);
        }
    }

    public static void modifySimulationDistance(int distance) {
        ServerCore.getServer().getPlayerList().setSimulationDistance(distance);
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            Minecraft.getInstance().options.simulationDistance().set(distance);
        }
    }

    public static void modifyMobcaps(double modifier) {
        for (MobCategory category : MobCategory.values()) {
            if ((Object) category instanceof IMobCategory mobCategory) {
                mobCategory.modifyMobcaps(modifier);
            }
        }
    }

    public static String createStatusReport(String title) {
        return title + "\n" + CommandConfig.STATUS_CONTENT.get()
                .replace("${version}", ServerCore.getVersion())
                .replace("${mobcap_percentage}", getModifierAsPercentage())
                .replace("${chunk_tick_distance}", String.format("%.0f", CHUNK_TICK_DISTANCE.get()))
                .replace("${simulation_distance}", String.format("%.0f", SIMULATION_DISTANCE.get()))
                .replace("${view_distance}", String.format("%.0f", VIEW_DISTANCE.get()));
    }

    public static double getAverageTickTime() {
        return averageTickTime;
    }

    public static String getModifierAsPercentage() {
        return String.format("%.0f%%", MOBCAP_MULTIPLIER.get() * 100);
    }
}