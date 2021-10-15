package org.provim.servercore.utils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.provim.servercore.ServerCore;
import org.provim.servercore.config.tables.DynamicConfig;
import org.provim.servercore.config.tables.EntityConfig;

import java.math.BigDecimal;

public final class TickManager {
    private static final BigDecimal VALUE = new BigDecimal("0.1");
    private static int viewDistance = ServerCore.getServer().getPlayerManager().getViewDistance();
    private static int simulationDistance = viewDistance;
    private static int chunkTickDistance = viewDistance;
    private static BigDecimal mobcapModifier = new BigDecimal(String.valueOf(DynamicConfig.MAX_MOBCAP.get()));

    private TickManager() {
    }

    /**
     * Runs performance checks based on the current MSPT.
     *
     * @param server: The minecraft server
     */

    public static void runPerformanceChecks(MinecraftServer server) {
        if (DynamicConfig.ENABLED.get()) {
            final double mspt = MathHelper.average(server.lastTickLengths) * 1.0E-6D;
            checkViewDistance(mspt);
            checkSimulationDistance(mspt);
            checkMobcaps(mspt);
            checkChunkTickDistance(mspt);
        }
    }

    /**
     * Modifies the tick distance based on the MSPT.
     */

    private static void checkChunkTickDistance(double mspt) {
        if (mspt > 40 && chunkTickDistance > DynamicConfig.MIN_CHUNK_TICK_DISTANCE.get()) {
            chunkTickDistance--;
        } else if (mspt < 30 && chunkTickDistance < DynamicConfig.MAX_CHUNK_TICK_DISTANCE.get() && mobcapModifier.doubleValue() == DynamicConfig.MAX_MOBCAP.get()) {
            chunkTickDistance++;
        }
    }

    /**
     * Modifies the mobcaps based on the MSPT.
     */

    private static void checkMobcaps(double mspt) {
        if (mspt > 45 && mobcapModifier.doubleValue() > DynamicConfig.MIN_MOBCAP.get() && chunkTickDistance == DynamicConfig.MIN_CHUNK_TICK_DISTANCE.get()) {
            mobcapModifier = mobcapModifier.subtract(VALUE);
        } else if (mspt < 35 && mobcapModifier.doubleValue() < DynamicConfig.MAX_MOBCAP.get() && simulationDistance == DynamicConfig.MAX_SIMULATION_DISTANCE.get()) {
            mobcapModifier = mobcapModifier.add(VALUE);
        }
    }

    /**
     * Modifies the simulation distance based on the MSPT.
     */

    private static void checkSimulationDistance(double mspt) {
        if (mspt > 45 && simulationDistance > DynamicConfig.MAX_SIMULATION_DISTANCE.get() && mobcapModifier.doubleValue() == DynamicConfig.MIN_MOBCAP.get()) {
            setSimulationDistance(simulationDistance - 1);
        } else if (mspt < 35 && simulationDistance < DynamicConfig.MAX_SIMULATION_DISTANCE.get() && viewDistance == DynamicConfig.MAX_VIEW_DISTANCE.get()) {
            setSimulationDistance(simulationDistance + 1);
        }
    }

    /**
     * Modifies the view distance based on the MSPT.
     */

    private static void checkViewDistance(double mspt) {
        if (mspt > 45 && viewDistance > DynamicConfig.MIN_VIEW_DISTANCE.get() && simulationDistance == DynamicConfig.MIN_SIMULATION_DISTANCE.get()) {
            setViewDistance(viewDistance - 1);
        } else if (mspt < 35 && viewDistance < DynamicConfig.MAX_VIEW_DISTANCE.get()) {
            setViewDistance(viewDistance + 1);
        }
    }

    public static int getViewDistance() {
        return viewDistance;
    }

    public static void setViewDistance(int distance) {
        ServerCore.getServer().getPlayerManager().setViewDistance(distance);
        viewDistance = distance;
    }

    public static int getSimulationDistance() {
        return simulationDistance;
    }

    public static void setSimulationDistance(int distance) {
        ServerCore.getServer().getPlayerManager().setSimulationDistance(distance);
        simulationDistance = distance;
    }

    public static double getModifier() {
        return mobcapModifier.doubleValue();
    }

    public static void setModifier(BigDecimal modifier) {
        mobcapModifier = modifier;
    }

    public static int getMobcap(SpawnGroup group) {
        return (int) (group.getCapacity() * mobcapModifier.doubleValue());
    }

    public static int getChunkTickDistance() {
        return chunkTickDistance;
    }

    public static void setChunkTickDistance(int distance) {
        chunkTickDistance = distance;
    }

    /**
     * Decides whether a chunk should tick.
     *
     * @param pos:   The position of the chunk
     * @param world: The world that is ticking chunks
     * @return Boolean: whether the chunk should tick.
     */

    public static boolean shouldTickChunk(ChunkPos pos, ServerWorld world) {
        if (chunkTickDistance >= viewDistance) {
            return true;
        }

        for (ServerPlayerEntity player : world.getPlayers()) {
            if (player.interactionManager.getGameMode() != GameMode.SPECTATOR && player.getChunkPos().getChebyshevDistance(pos) <= chunkTickDistance) {
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
     * @param world: The world to check in
     * @param pos:   The position of the entity
     * @param limit: The entity limit
     * @param range: The range of the limit
     * @return Boolean: if there are more entities of the same type as the limit, within the specified range.
     */

    public static boolean checkForEntities(EntityType<?> type, World world, BlockPos pos, int limit, int range) {
        if (EntityConfig.ENABLED.get()) {
            return limit <= world.getEntitiesByType(type, new Box(pos.mutableCopy().add(range, range, range), pos.mutableCopy().add(-range, -range, -range)), EntityPredicates.EXCEPT_SPECTATOR).size();
        } else {
            return false;
        }
    }

    public static boolean checkForEntities(Entity entity, int limit, int range) {
        return checkForEntities(entity.getType(), entity.getEntityWorld(), entity.getBlockPos(), limit, range);
    }

    public static MutableText createPerformanceReport() {
        final MinecraftServer server = ServerCore.getServer();
        final double ms = MathHelper.average(server.lastTickLengths) * 1.0E-6D;
        final String mspt = String.format("%.1f", ms);
        final String tps = String.format("%.1f", ms != 0 ? Math.min((1000 / ms), 20) : 20);
        return new LiteralText(String.format("§8- §3TPS: §a%s §3MSPT: §a%s\n§8- §3Online: §a%d\n§8- §3View distance: §a%d\n§8- §3Mobcap multiplier: §a%s\n§8- §3Simulation distance: §a%d\n§8- §3Chunk-tick distance: §a%d", tps, mspt, server.getCurrentPlayerCount(), viewDistance, String.format("%.1f", getModifier()), simulationDistance, chunkTickDistance));
    }
}
