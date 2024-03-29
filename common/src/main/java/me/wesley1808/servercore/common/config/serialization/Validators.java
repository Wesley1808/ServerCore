package me.wesley1808.servercore.common.config.serialization;

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import me.wesley1808.servercore.common.config.data.dynamic.Setting;
import me.wesley1808.servercore.common.dynamic.DynamicSetting;
import space.arim.dazzleconf.error.BadValueException;
import space.arim.dazzleconf.validator.ValueValidator;

import java.util.List;

public class Validators {
    public static class UniqueSettings implements ValueValidator {
        @Override
        public void validate(String key, Object value) throws BadValueException {
            // noinspection unchecked
            List<Setting> settings = (List<Setting>) value;

            ReferenceOpenHashSet<DynamicSetting> dynamicSettings = new ReferenceOpenHashSet<>();
            for (Setting setting : settings) {
                if (!dynamicSettings.add(setting.dynamicSetting())) {
                    throw badValue(key, "Duplicate dynamic setting: " + setting.dynamicSetting());
                }
            }
        }
    }

    private static BadValueException badValue(String key, String message) {
        return new BadValueException.Builder()
                .key(key)
                .message(message)
                .build();
    }
}
