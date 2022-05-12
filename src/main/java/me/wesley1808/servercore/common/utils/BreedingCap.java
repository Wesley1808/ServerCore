package me.wesley1808.servercore.common.utils;

import me.wesley1808.servercore.common.config.tables.EntityLimitConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

public final class BreedingCap {

    public static boolean exceedsLimit(EntityType<?> type, Level level, BlockPos pos, int limit, int range) {
        if (EntityLimitConfig.ENABLED.get()) {
            return limit <= level.getEntities(type, getBox(pos, range), EntitySelector.NO_SPECTATORS).size();
        }
        return false;
    }

    public static boolean exceedsLimit(List<EntityType<?>> types, Level level, BlockPos pos, int limit, int range) {
        if (EntityLimitConfig.ENABLED.get()) {
            return limit <= level.getEntities((Entity) null, getBox(pos, range), e -> types.contains(e.getType())).size();
        }
        return false;
    }

    public static boolean exceedsLimit(Entity entity, int limit, int range) {
        return exceedsLimit(entity.getType(), entity.getLevel(), entity.blockPosition(), limit, range);
    }

    private static AABB getBox(BlockPos pos, int range) {
        return new AABB(pos.mutable().offset(range, range, range), pos.mutable().offset(-range, -range, -range));
    }
}
