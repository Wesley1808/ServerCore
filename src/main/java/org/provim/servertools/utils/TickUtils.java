package org.provim.servertools.utils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import org.provim.servertools.config.Config;

public final class TickUtils {

    private TickUtils() {
    }

    /**
     * Decides whether or not a chunk should tick.
     *
     * @param pos:   The position of the chunk
     * @param world: The world that is ticking chunks
     * @return Boolean: whether or not the chunk should tick.
     */

    public static boolean shouldTick(ChunkPos pos, World world) {
        if (!Config.instance().useTickDistance) {
            return true;
        }

        for (PlayerEntity player : world.getPlayers()) {
            if (player.getChunkPos().getChebyshevDistance(pos) <= Config.instance().tickDistance) {
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
