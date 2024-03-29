package me.wesley1808.servercore.common.config.data.activation_range;

import me.wesley1808.servercore.common.config.serialization.EntityTypeSerializer;
import me.wesley1808.servercore.common.config.serialization.EntityTypeTestSerializer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntityTypeTest;
import space.arim.dazzleconf.annote.CollectionSize;
import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.annote.ConfSerialisers;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter.Order;

import java.util.List;

@ConfSerialisers({EntityTypeSerializer.class, EntityTypeTestSerializer.class})
public interface CustomActivationType extends ActivationType {
    @Order(0)
    @ConfKey("name")
    String name();

    @Order(6)
    @ConfKey("entity-matcher")
    @CollectionSize(min = 1)
    List<EntityTypeTest<? super Entity, ?>> matchers();
}
