package me.wesley1808.servercore.common.config.serialization;

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
        TextColor textColor = TextColor.parseColor(color);
        if (textColor == null) {
            throw flexibleType.badValueExceptionBuilder()
                    .message("Invalid color: " + color)
                    .build();
        }

        return textColor;
    }

    @Override
    public Object serialise(TextColor value, Decomposer decomposer) {
        return value.serialize();
    }
}
