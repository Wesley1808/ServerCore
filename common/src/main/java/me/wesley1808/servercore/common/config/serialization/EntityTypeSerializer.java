package me.wesley1808.servercore.common.config.serialization;

import me.wesley1808.servercore.common.config.Config;
import me.wesley1808.servercore.common.services.platform.PlatformHelper;
import net.minecraft.world.entity.EntityType;
import space.arim.dazzleconf.error.BadValueException;
import space.arim.dazzleconf.serialiser.Decomposer;
import space.arim.dazzleconf.serialiser.FlexibleType;
import space.arim.dazzleconf.serialiser.ValueSerialiser;

@SuppressWarnings("rawtypes")
public class EntityTypeSerializer implements ValueSerialiser<EntityType> {

    @Override
    public Class<EntityType> getTargetClass() {
        return EntityType.class;
    }

    @Override
    public EntityType<?> deserialise(FlexibleType flexibleType) throws BadValueException {
        String key = flexibleType.getString();

        EntityType<?> type = PlatformHelper.getEntityType(key);
        if (type == null) {
            if (Config.shouldValidate()) {
                throw flexibleType.badValueExceptionBuilder().message("Unknown entity type: " + key).build();
            } else {
                // Fallback to the marker entity type if none are found since we cannot return null here.
                // This is to fix an issue when providing a modded entity type in the config that hasn't been registered yet.
                // The config is required to be loaded early on, so it can't easily be delayed.
                // It will automatically be reloaded later to replace any of these marker types.
                type = EntityType.MARKER;
            }
        }

        return type;
    }

    @Override
    public Object serialise(EntityType value, Decomposer decomposer) {
        return PlatformHelper.getEntityTypeKey(value).toString();
    }
}
