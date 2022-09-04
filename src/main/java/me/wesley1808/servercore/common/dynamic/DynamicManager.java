package me.wesley1808.servercore.common.dynamic;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.lucko.spark.api.SparkProvider;
import me.lucko.spark.api.statistic.StatisticWindow;
import me.lucko.spark.api.statistic.misc.DoubleAverageInfo;
import me.lucko.spark.api.statistic.types.GenericStatistic;
import me.wesley1808.servercore.common.ServerCore;
import me.wesley1808.servercore.common.config.tables.CommandConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameType;

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
    }

    private static void runPerformanceChecks() {
        final double targetMspt = TARGET_MSPT.get();
        final double upperBound = targetMspt + 5;
        final double lowerBound = Math.max(targetMspt - 5, 2);

        count++;
        for (DynamicSetting setting : DynamicSetting.values()) {
            if (averageTickTime > upperBound) {
                if (setting.shouldRun(count) && setting.decrease()) {
                    return;
                }
            } else if (averageTickTime < lowerBound) {
                if (setting.shouldRun(count) && setting.increase()) {
                    return;
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

    public static boolean shouldTickChunk(ChunkPos pos, ServerLevel level) {
        double chunkTickDistance = CHUNK_TICK_DISTANCE.get();
        if (chunkTickDistance >= VIEW_DISTANCE.get()) {
            return true;
        }

        for (ServerPlayer player : level.players()) {
            if (player.gameMode.getGameModeForPlayer() != GameType.SPECTATOR && player.chunkPosition().getChessboardDistance(pos) <= chunkTickDistance) {
                return true;
            }
        }

        return false;
    }

    public static String createStatusReport(String title) {
        return title + "\n" + CommandConfig.STATUS_CONTENT.get()
                .replace("${version}", ServerCore.getVersion())
                .replace("${mobcap_modifier}", getModifierAsString())
                .replace("${view_distance}", String.format("%.0f", VIEW_DISTANCE.get()))
                .replace("${simulation_distance}", String.format("%.0f", SIMULATION_DISTANCE.get()))
                .replace("${chunk_tick_distance}", String.format("%.0f", CHUNK_TICK_DISTANCE.get()));
    }

    public static double getAverageTickTime() {
        return averageTickTime;
    }

    public static String getModifierAsString() {
        return String.format("%.1f", MOBCAP_MULTIPLIER.get());
    }
}