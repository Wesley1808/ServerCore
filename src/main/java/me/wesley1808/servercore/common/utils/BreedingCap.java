package me.wesley1808.servercore.common.utils;

import me.wesley1808.servercore.common.config.tables.EntityLimitConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.Map;
import java.util.Set;
import java.util.function.IntSupplier;

public final class BreedingCap {
    private static final Map<EntityType<?>, Set<EntityType<?>>> CUSTOM_TYPES = Map.of(
            EntityType.FROG, Set.of(EntityType.TADPOLE, EntityType.FROG)
    );

    public static void resetLove(Animal owner, Animal mate) {
        resetAge(owner, mate);
        owner.resetLove();
        mate.resetLove();
    }

    public static void resetAge(AgeableMob owner, AgeableMob mate) {
        owner.setAge(6000);
        mate.setAge(6000);
    }

    public static boolean exceedsLimit(EntityType<?> type, Level level, BlockPos pos, Info info) {
        if (!EntityLimitConfig.ENABLED.get()) {
            return false;
        }

        AABB box = getBox(pos, info.range.getAsInt());
        Set<EntityType<?>> set = CUSTOM_TYPES.get(type);

        int count;
        if (set != null && !set.isEmpty()) {
            count = level.getEntities((Entity) null, box, (entity) -> set.contains(entity.getType())).size();
        } else {
            count = level.getEntities(type, box, EntitySelector.NO_SPECTATORS).size();
        }

        return info.limit.getAsInt() <= count;
    }

    public static boolean exceedsLimit(Entity entity, Info info) {
        return exceedsLimit(entity.getType(), entity.getLevel(), entity.blockPosition(), info);
    }

    private static AABB getBox(BlockPos pos, int range) {
        return new AABB(pos.offset(range, range, range), pos.offset(-range, -range, -range));
    }

    public enum Info {
        ANIMAL(EntityLimitConfig.ANIMAL_COUNT::get, EntityLimitConfig.ANIMAL_RANGE::get),
        VILLAGER(EntityLimitConfig.VILLAGER_COUNT::get, EntityLimitConfig.VILLAGER_RANGE::get);

        private final IntSupplier limit;
        private final IntSupplier range;

        Info(IntSupplier limit, IntSupplier range) {
            this.limit = limit;
            this.range = range;
        }
    }

}
