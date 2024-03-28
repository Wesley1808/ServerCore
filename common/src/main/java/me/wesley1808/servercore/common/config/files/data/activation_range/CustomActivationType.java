package me.wesley1808.servercore.common.config.files.data.activation_range;

import me.wesley1808.servercore.common.config.serialization.EntityTypeSerializer;
import me.wesley1808.servercore.common.config.serialization.EntityTypeTestSerializer;
import net.minecraft.world.level.entity.EntityTypeTest;
import space.arim.dazzleconf.annote.CollectionSize;
import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.annote.ConfSerialisers;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter.Order;

import java.util.List;

@ConfSerialisers({
        EntityTypeSerializer.class,
        EntityTypeTestSerializer.class
})
public interface CustomActivationType extends ActivationType {
    @Order(0)
    @ConfKey("name")
    String name();

    @Order(6)
    @ConfKey("entity-matcher")
    @CollectionSize(min = 1)
    List<EntityTypeTest<?, ?>> matchers();

    static CustomActivationType of(String name, List<EntityTypeTest<?, ?>> matchers, int activationRange, int tickInterval, int wakeupInterval, boolean extraHeightUp, boolean extraHeightDown) {
        return new CustomActivationType() {
            @Override
            public String name() {
                return name;
            }

            @Override
            public List<EntityTypeTest<?, ?>> matchers() {
                return matchers;
            }

            @Override
            public int activationRange() {
                return activationRange;
            }

            @Override
            public int tickInterval() {
                return tickInterval;
            }

            @Override
            public int wakeupInterval() {
                return wakeupInterval;
            }

            @Override
            public boolean extraHeightUp() {
                return extraHeightUp;
            }

            @Override
            public boolean extraHeightDown() {
                return extraHeightDown;
            }
        };
    }
}
