package me.wesley1808.servercore.common.config.serialization;

import me.wesley1808.servercore.common.config.Config;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import space.arim.dazzleconf.error.BadValueException;
import space.arim.dazzleconf.serialiser.Decomposer;
import space.arim.dazzleconf.serialiser.FlexibleType;
import space.arim.dazzleconf.serialiser.ValueSerialiser;

import java.util.Optional;

@SuppressWarnings("rawtypes")
public class EntityTypeSerializer implements ValueSerialiser<EntityType> {

    @Override
    public Class<EntityType> getTargetClass() {
        return EntityType.class;
    }

    @Override
    public EntityType<?> deserialise(FlexibleType flexibleType) throws BadValueException {
        String key = flexibleType.getString();

        Optional<EntityType<?>> type = BuiltInRegistries.ENTITY_TYPE.getOptional(Identifier.tryParse(key));
        if (type.isEmpty()) {
            if (Config.shouldValidate()) {
                throw flexibleType.badValueExceptionBuilder().message("Unknown entity type: " + key).build();
            } else {
                // Fallback to the marker entity type if none are found since we cannot return null here.
                // This is to fix an issue when providing a modded entity type in the config that hasn't been registered yet.
                // The config is required to be loaded early on, so it can't easily be delayed.
                // It will automatically be reloaded later to replace any of these marker types.
                return EntityTypes.MARKER;
            }
        }

        return type.get();
    }

    @Override
    public Object serialise(EntityType value, Decomposer decomposer) {
        return EntityType.getKey(value).toString();
    }
}
