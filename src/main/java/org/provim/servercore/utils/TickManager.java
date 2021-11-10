package org.provim.servercore.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.provim.servercore.ServerCore;
import org.provim.servercore.config.tables.DynamicConfig;
import org.provim.servercore.config.tables.EntityConfig;

import java.math.BigDecimal;

public final class TickManager {
    private static final BigDecimal VALUE = new BigDecimal("0.1");
    private static BigDecimal mobcapModifier = new BigDecimal(String.valueOf(DynamicConfig.MAX_MOBCAP.get()));
    private static int viewDistance;
    private static int simulationDistance;
    private static int chunkTickDistance;


    private TickManager() {
    }

    public static void initValues(MinecraftServer server) {
        viewDistance = server.getPlayerList().getViewDistance();
        simulationDistance = viewDistance;
        chunkTickDistance = viewDistance;
    }

    /**
     * Runs performance checks based on the current MSPT.
     *
     * @param server: The minecraft server
     */

    public static void runPerformanceChecks(MinecraftServer server) {
        if (DynamicConfig.ENABLED.get()) {
            final double mspt = Mth.average(server.tickTimes) * 1.0E-6D;
            final double targetMspt = DynamicConfig.TARGET_MSPT.get();
            final double upperBound = targetMspt + 5;
            final double lowerBound = Math.max(targetMspt - 5, 2);

            checkViewDistance(mspt, upperBound, lowerBound);
            checkSimulationDistance(mspt, upperBound, lowerBound);
            checkMobcaps(mspt, upperBound, lowerBound);
            checkChunkTickDistance(mspt, upperBound, lowerBound);
        }
    }

    // Modifies chunk tick distance
    private static void checkChunkTickDistance(double mspt, double upperBound, double lowerBound) {
        if (mspt > upperBound && chunkTickDistance > DynamicConfig.MIN_CHUNK_TICK_DISTANCE.get()) {
            chunkTickDistance--;
        } else if (mspt < lowerBound && chunkTickDistance < DynamicConfig.MAX_CHUNK_TICK_DISTANCE.get() && mobcapModifier.doubleValue() >= DynamicConfig.MAX_MOBCAP.get()) {
            chunkTickDistance++;
        }
    }

    // Modifies mobcaps
    private static void checkMobcaps(double mspt, double upperBound, double lowerBound) {
        if (mspt > upperBound && mobcapModifier.doubleValue() > DynamicConfig.MIN_MOBCAP.get() && chunkTickDistance <= DynamicConfig.MIN_CHUNK_TICK_DISTANCE.get()) {
            mobcapModifier = mobcapModifier.subtract(VALUE);
        } else if (mspt < lowerBound && mobcapModifier.doubleValue() < DynamicConfig.MAX_MOBCAP.get() && simulationDistance >= DynamicConfig.MAX_SIMULATION_DISTANCE.get()) {
            mobcapModifier = mobcapModifier.add(VALUE);
        }
    }

    // Modifies simulation distance
    private static void checkSimulationDistance(double mspt, double upperBound, double lowerBound) {
        if (mspt > upperBound && simulationDistance > DynamicConfig.MIN_SIMULATION_DISTANCE.get() && mobcapModifier.doubleValue() <= DynamicConfig.MIN_MOBCAP.get()) {
            setSimulationDistance(simulationDistance - 1);
        } else if (mspt < lowerBound && simulationDistance < DynamicConfig.MAX_SIMULATION_DISTANCE.get() && viewDistance >= DynamicConfig.MAX_VIEW_DISTANCE.get()) {
            setSimulationDistance(simulationDistance + 1);
        }
    }

    // Modifies view distance
    private static void checkViewDistance(double mspt, double upperBound, double lowerBound) {
        if (mspt > upperBound && viewDistance > DynamicConfig.MIN_VIEW_DISTANCE.get() && simulationDistance <= DynamicConfig.MIN_SIMULATION_DISTANCE.get()) {
            setViewDistance(viewDistance - 1);
        } else if (mspt < lowerBound && viewDistance < DynamicConfig.MAX_VIEW_DISTANCE.get()) {
            setViewDistance(viewDistance + 1);
        }
    }

    public static void setViewDistance(int distance) {
        ServerCore.getServer().getPlayerList().setViewDistance(distance);
        viewDistance = distance;
    }

    public static void setSimulationDistance(int distance) {
        ServerCore.getServer().getPlayerList().setSimulationDistance(distance);
        simulationDistance = distance;
    }

    public static void setModifier(BigDecimal modifier) {
        mobcapModifier = modifier;
    }

    public static void setChunkTickDistance(int distance) {
        chunkTickDistance = distance;
    }

    public static String getModifierAsString() {
        return String.format("%.1f", mobcapModifier.doubleValue());
    }

    public static int getMobcap(MobCategory group) {
        return (int) (group.getMaxInstancesPerChunk() * mobcapModifier.doubleValue());
    }

    /**
     * Decides whether a chunk should tick.
     *
     * @param pos:   The position of the chunk
     * @param level: The world that is ticking chunks
     * @return Boolean: whether the chunk should tick.
     */

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

    /**
     * Checks for entities of the same type in the surrounding area.
     * If there are more than the limit specifies within the range, it will return true.
     *
     * @param type:  The type of entity
     * @param level: The world to check in
     * @param pos:   The position of the entity
     * @param limit: The entity limit
     * @param range: The range of the limit
     * @return Boolean: if there are more entities of the same type as the limit, within the specified range.
     */

    public static boolean checkForEntities(EntityType<?> type, Level level, BlockPos pos, int limit, int range) {
        if (EntityConfig.ENABLED.get()) {
            return limit <= level.getEntities(type, new AABB(pos.mutable().offset(range, range, range), pos.mutable().offset(-range, -range, -range)), entity -> !entity.isSpectator()).size();
        } else {
            return false;
        }
    }

    public static boolean checkForEntities(Entity entity, int limit, int range) {
        return checkForEntities(entity.getType(), entity.getLevel(), entity.blockPosition(), limit, range);
    }

    public static MutableComponent createStatusReport() {
        return new TextComponent(replaceStatusVariables("§8> §3ServerCore Status §8<\n§8- §3TPS: §a%s\n§8- §3MSPT: §a%s\n§8- §3Online: §a%d\n§8- §3View distance: §a%d\n§8- §3Mobcap multiplier: §a%s\n§8- §3Simulation distance: §a%d\n§8- §3Chunk-tick distance: §a%d"));
    }

    private static String replaceStatusVariables(String message) {
        final MinecraftServer server = ServerCore.getServer();
        final double ms = Mth.average(server.tickTimes) * 1.0E-6D;
        final String mspt = String.format("%.1f", ms);
        final String tps = String.format("%.1f", ms != 0 ? Math.min((1000 / ms), 20) : 20);
        return String.format(message, tps, mspt, server.getPlayerCount(), viewDistance, getModifierAsString(), simulationDistance, chunkTickDistance);
    }
}
