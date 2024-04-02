package me.wesley1808.servercore.common.config.data.breeding_cap;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import space.arim.dazzleconf.annote.ConfDefault.DefaultInteger;
import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.annote.IntegerRange;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter.Order;

import java.util.Map;
import java.util.Set;

public interface BreedingCap {
    Map<EntityType<?>, Set<EntityType<?>>> CUSTOM_TYPES = Map.of(
            EntityType.FROG, Set.of(EntityType.TADPOLE, EntityType.FROG)
    );

    @Order(1)
    @ConfKey("limit")
    @DefaultInteger(32)
    int limit();

    @Order(2)
    @ConfKey("range")
    @DefaultInteger(64)
    @IntegerRange(min = 1)
    int range();

    default boolean exceedsLimit(EntityType<?> type, Level level, BlockPos pos) {
        final int limit = this.limit();
        if (limit < 0) {
            return false;
        }

        AABB area = this.getAreaAt(pos);
        Set<EntityType<?>> set = CUSTOM_TYPES.get(type);

        int count;
        if (set != null && !set.isEmpty()) {
            count = level.getEntities((Entity) null, area, (entity) -> set.contains(entity.getType())).size();
        } else {
            count = level.getEntities(type, area, EntitySelector.NO_SPECTATORS).size();
        }

        return limit <= count;
    }

    default boolean exceedsLimit(Entity entity) {
        return this.exceedsLimit(entity.getType(), entity.level(), entity.blockPosition());
    }

    private AABB getAreaAt(BlockPos pos) {
        int range = this.range();
        return new AABB(pos.offset(range, range, range), pos.offset(-range, -range, -range));
    }

    static void resetLove(Animal owner, Animal mate) {
        resetAge(owner, mate);
        owner.resetLove();
        mate.resetLove();
    }

    static void resetAge(AgeableMob owner, AgeableMob mate) {
        owner.setAge(6000);
        mate.setAge(6000);
    }
}
