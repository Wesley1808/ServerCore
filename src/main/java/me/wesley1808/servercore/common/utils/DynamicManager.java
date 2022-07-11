package me.wesley1808.servercore.common.utils;

import me.lucko.spark.api.SparkProvider;
import me.lucko.spark.api.statistic.StatisticWindow;
import me.lucko.spark.api.statistic.misc.DoubleAverageInfo;
import me.lucko.spark.api.statistic.types.GenericStatistic;
import me.wesley1808.servercore.common.ServerCore;
import me.wesley1808.servercore.common.config.tables.CommandConfig;
import me.wesley1808.servercore.common.config.tables.DynamicConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameType;

import java.math.BigDecimal;

public final class DynamicManager {
    private static final boolean SPARK_LOADED = FabricLoader.getInstance().isModLoaded("spark");
    private static final BigDecimal VALUE = new BigDecimal("0.1");
    private static BigDecimal mobcapModifier = new BigDecimal(String.valueOf(DynamicConfig.MAX_MOBCAP.get()));
    private static GenericStatistic<DoubleAverageInfo, StatisticWindow.MillisPerTick> tickStatistics;
    private static double averageTickTime = 0.0;
    private static int viewDistance;
    private static int simulationDistance;
    private static int chunkTickDistance;

    public static void initValues(PlayerList playerList) {
        viewDistance = playerList.getViewDistance();
        simulationDistance = playerList.getSimulationDistance();
        chunkTickDistance = DynamicConfig.MAX_CHUNK_TICK_DISTANCE.get();

        if (DynamicConfig.ENABLED.get()) {
            final int maxViewDistance = DynamicConfig.MAX_VIEW_DISTANCE.get();
            final int maxSimDistance = DynamicConfig.MAX_SIMULATION_DISTANCE.get();
            if (viewDistance > maxViewDistance) modifyViewDistance(maxViewDistance);
            if (simulationDistance > maxSimDistance) modifySimulationDistance(maxSimDistance);
        }
    }

    public static void update(MinecraftServer server) {
        updateValues(server);
        runPerformanceChecks(server);
    }

    private static void updateValues(MinecraftServer server) {
        if (server.getTickCount() % 20 == 0) {
            if (SPARK_LOADED && tickStatistics == null) {
                tickStatistics = SparkProvider.get().mspt();
            }

            if (tickStatistics != null) {
                averageTickTime = tickStatistics.poll(StatisticWindow.MillisPerTick.SECONDS_10).median();
            } else {
                averageTickTime = server.getAverageTickTime();
            }
        }
    }

    // Runs performance checks based on the current MSPT.
    private static void runPerformanceChecks(MinecraftServer server) {
        if (DynamicConfig.ENABLED.get()) {
            final double targetMspt = DynamicConfig.TARGET_MSPT.get();
            final double upperBound = targetMspt + 5;
            final double lowerBound = Math.max(targetMspt - 5, 2);

            if (server.getTickCount() % (DynamicConfig.VIEW_DISTANCE_UPDATE_RATE.get() * 20) == 0) {
                checkViewDistance(upperBound, lowerBound);
            }

            if (server.getTickCount() % (DynamicConfig.UPDATE_RATE.get() * 20) == 0) {
                checkSimulationDistance(upperBound, lowerBound);
                checkMobcaps(upperBound, lowerBound);
                checkChunkTickDistance(upperBound, lowerBound);
            }
        }
    }

    // Modifies chunk tick distance
    private static void checkChunkTickDistance(double upperBound, double lowerBound) {
        if (averageTickTime > upperBound && chunkTickDistance > DynamicConfig.MIN_CHUNK_TICK_DISTANCE.get()) {
            chunkTickDistance--;
        } else if (averageTickTime < lowerBound && chunkTickDistance < DynamicConfig.MAX_CHUNK_TICK_DISTANCE.get() && mobcapModifier.doubleValue() >= DynamicConfig.MAX_MOBCAP.get()) {
            chunkTickDistance++;
        }
    }

    // Modifies mobcaps
    private static void checkMobcaps(double upperBound, double lowerBound) {
        if (averageTickTime > upperBound && mobcapModifier.doubleValue() > DynamicConfig.MIN_MOBCAP.get() && chunkTickDistance <= DynamicConfig.MIN_CHUNK_TICK_DISTANCE.get()) {
            mobcapModifier = mobcapModifier.subtract(VALUE);
        } else if (averageTickTime < lowerBound && mobcapModifier.doubleValue() < DynamicConfig.MAX_MOBCAP.get() && simulationDistance >= DynamicConfig.MAX_SIMULATION_DISTANCE.get()) {
            mobcapModifier = mobcapModifier.add(VALUE);
        }
    }

    // Modifies simulation distance
    private static void checkSimulationDistance(double upperBound, double lowerBound) {
        if (averageTickTime > upperBound && simulationDistance > DynamicConfig.MIN_SIMULATION_DISTANCE.get() && mobcapModifier.doubleValue() <= DynamicConfig.MIN_MOBCAP.get()) {
            modifySimulationDistance(simulationDistance - 1);
        } else if (averageTickTime < lowerBound && simulationDistance < DynamicConfig.MAX_SIMULATION_DISTANCE.get() && viewDistance >= DynamicConfig.MAX_VIEW_DISTANCE.get()) {
            modifySimulationDistance(simulationDistance + 1);
        }
    }

    // Modifies view distance
    private static void checkViewDistance(double upperBound, double lowerBound) {
        if (averageTickTime > upperBound && viewDistance > DynamicConfig.MIN_VIEW_DISTANCE.get() && simulationDistance <= DynamicConfig.MIN_SIMULATION_DISTANCE.get()) {
            modifyViewDistance(viewDistance - 1);
        } else if (averageTickTime < lowerBound && viewDistance < DynamicConfig.MAX_VIEW_DISTANCE.get()) {
            modifyViewDistance(viewDistance + 1);
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
        if (chunkTickDistance >= viewDistance) {
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
                .replace("${view_distance}", String.valueOf(viewDistance))
                .replace("${simulation_distance}", String.valueOf(simulationDistance))
                .replace("${chunk_tick_distance}", String.valueOf(chunkTickDistance));
    }

    public static void setViewDistance(int distance) {
        viewDistance = distance;
    }

    public static void setSimulationDistance(int distance) {
        simulationDistance = distance;
    }

    public static void setChunkTickDistance(int distance) {
        chunkTickDistance = distance;
    }

    public static void setModifier(BigDecimal modifier) {
        mobcapModifier = modifier;
    }

    public static double getAverageTickTime() {
        return averageTickTime;
    }

    public static int getViewDistance() {
        return viewDistance;
    }

    public static int getSimulationDistance() {
        return simulationDistance;
    }

    public static int getChunkTickDistance() {
        return chunkTickDistance;
    }

    public static double getMobcapModifier() {
        return mobcapModifier.doubleValue();
    }

    public static String getModifierAsString() {
        return String.format("%.1f", mobcapModifier.doubleValue());
    }
}