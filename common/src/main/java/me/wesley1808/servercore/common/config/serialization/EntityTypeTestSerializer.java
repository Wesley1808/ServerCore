package me.wesley1808.servercore.common.config.serialization;

import me.wesley1808.servercore.common.activation_range.EntityTypeTests;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.entity.EntityTypeTest;
import space.arim.dazzleconf.error.BadValueException;
import space.arim.dazzleconf.serialiser.Decomposer;
import space.arim.dazzleconf.serialiser.FlexibleType;
import space.arim.dazzleconf.serialiser.ValueSerialiser;

@SuppressWarnings("rawtypes")
public class EntityTypeTestSerializer implements ValueSerialiser<EntityTypeTest> {
    private static final String PREFIX = "typeof:";

    @Override
    public Class<EntityTypeTest> getTargetClass() {
        return EntityTypeTest.class;
    }

    @Override
    public EntityTypeTest deserialise(FlexibleType flexibleType) throws BadValueException {
        String key = flexibleType.getString();
        if (!key.startsWith(PREFIX)) {
            return flexibleType.getObject(EntityType.class);
        }

        EntityTypeTest<?, ?> test = EntityTypeTests.get(key.substring(PREFIX.length()));
        if (test == null) {
            throw flexibleType.badValueExceptionBuilder()
                    .message("Unknown typeof class matcher: " + key)
                    .build();
        }
        return test;
    }

    @Override
    public Object serialise(EntityTypeTest value, Decomposer decomposer) {
        if (value instanceof EntityType<?> type) {
            return decomposer.decompose(EntityType.class, type);
        }
        return PREFIX + EntityTypeTests.getKey(value);
    }
}
