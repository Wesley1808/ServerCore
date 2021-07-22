package org.provim.servertools.utils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import org.provim.servertools.ServerTools;
import org.provim.servertools.config.Config;

import java.math.BigDecimal;

public final class TickUtils {
    private static final BigDecimal value = new BigDecimal("0.1");
    private static int viewDistance = Config.instance().defaultViewDistance;
    private static int tickDistance = Config.instance().defaultTickDistance;
    private static BigDecimal mobcapModifier = new BigDecimal(Config.instance().defaultMobcapModifier);

    private TickUtils() {
    }

    /**
     * Runs performance checks based on the current MSPT.
     *
     * @param server: The minecraft server
     */

    public static void runPerformanceChecks(MinecraftServer server) {
        if (Config.instance().useDynamicPerformance) {
            double mspt = server.getTickTime();
            checkViewDistance(mspt, server);
            checkMobcaps(mspt);
            checkTickDistance(mspt);
        }
    }

    /**
     * Modifies the tick distance based on the MSPT.
     *
     * @param mspt: The current MSPT
     */

    private static void checkTickDistance(double mspt) {
        if (mspt > 40 && tickDistance > Config.instance().minTickDistance) {
            tickDistance -= 1;
        } else if (mspt < 30 && tickDistance < Config.instance().maxTickDistance && mobcapModifier.doubleValue() == Config.instance().maxMobcap) {
            tickDistance += 1;
        }
    }

    public static int getTickDistance() {
        return tickDistance;
    }

    public static void setTickDistance(int distance) {
        tickDistance = distance;
    }

    /**
     * Modifies the mobcaps based on the MSPT.
     *
     * @param mspt: The current MSPT
     */

    private static void checkMobcaps(double mspt) {
        if (mspt > 45 && tickDistance == Config.instance().minTickDistance && mobcapModifier.doubleValue() > Config.instance().minMobcap) {
            mobcapModifier = mobcapModifier.subtract(value);
        } else if (mspt < 35 && mobcapModifier.doubleValue() < Config.instance().maxMobcap && viewDistance == Config.instance().maxViewDistance) {
            mobcapModifier = mobcapModifier.add(value);
        }
    }

    public static double getModifier() {
        return mobcapModifier.doubleValue();
    }

    public static void setModifier(BigDecimal modifier) {
        mobcapModifier = modifier;
    }

    /**
     * Modifies the view distance based on the MSPT.
     *
     * @param mspt:   The current MSPT
     * @param server: The minecraft server
     */

    private static void checkViewDistance(double mspt, MinecraftServer server) {
        if (mspt > 45 && mobcapModifier.doubleValue() == Config.instance().minMobcap && viewDistance > Config.instance().minViewDistance) {
            setViewDistance(viewDistance - 1);
        } else if (mspt < 35 && viewDistance < Config.instance().maxViewDistance) {
            setViewDistance(viewDistance + 1);
        }
    }

    public static int getViewDistance() {
        return viewDistance;
    }

    public static void setViewDistance(int distance) {
        ServerTools.getServer().getPlayerManager().setViewDistance(distance);
        viewDistance = distance;
    }

    /**
     * Decides whether or not a chunk should tick.
     *
     * @param pos:   The position of the chunk
     * @param world: The world that is ticking chunks
     * @return Boolean: whether or not the chunk should tick.
     */

    public static boolean shouldTick(ChunkPos pos, World world) {
        if (!Config.instance().useTickDistance || tickDistance == viewDistance) {
            return true;
        }

        for (PlayerEntity player : world.getPlayers()) {
            if (player.getChunkPos().getChebyshevDistance(pos) <= tickDistance) {
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
     * @return Boolean: if there are more entities of the same type than the limit, within the specified range.
     */

    public static boolean checkForEntities(EntityType<?> type, World world, BlockPos pos, int limit, int range) {
        if (Config.instance().useEntityLimits) {
            return limit <= world.getEntitiesByType(type, new Box(pos.mutableCopy().add(range, range, range), pos.mutableCopy().add(-range, -range, -range)), EntityPredicates.EXCEPT_SPECTATOR).size();
        } else {
            return false;
        }
    }

    public static boolean checkForEntities(Entity entity, int limit, int range) {
        return checkForEntities(entity.getType(), entity.getEntityWorld(), entity.getBlockPos(), limit, range);
    }
}
