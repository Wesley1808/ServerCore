package me.wesley1808.servercore.common.config.serialization;

import com.mojang.serialization.DataResult;
import net.minecraft.network.chat.TextColor;
import space.arim.dazzleconf.error.BadValueException;
import space.arim.dazzleconf.serialiser.Decomposer;
import space.arim.dazzleconf.serialiser.FlexibleType;
import space.arim.dazzleconf.serialiser.ValueSerialiser;

public class TextColorSerializer implements ValueSerialiser<TextColor> {

    @Override
    public Class<TextColor> getTargetClass() {
        return TextColor.class;
    }

    @Override
    public TextColor deserialise(FlexibleType flexibleType) throws BadValueException {
        String color = flexibleType.getString();
        DataResult<TextColor> result = TextColor.parseColor(color);
        var error = result.error();
        if (error.isPresent()) {
            throw flexibleType.badValueExceptionBuilder()
                    .message(error.get().message())
                    .build();
        }

        var value = result.result();
        if (value.isEmpty()) {
            throw flexibleType.badValueExceptionBuilder()
                    .message("Invalid color: " + color)
                    .build();
        }

        return value.get();
    }

    @Override
    public Object serialise(TextColor value, Decomposer decomposer) {
        return value.serialize();
    }
}
