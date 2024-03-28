package me.wesley1808.servercore.common.config.files.data.dynamic;

import com.google.common.collect.Lists;
import me.wesley1808.servercore.common.config.serialization.Validators;
import me.wesley1808.servercore.common.dynamic.DynamicSetting;
import space.arim.dazzleconf.annote.*;
import space.arim.dazzleconf.annote.ConfDefault.DefaultBoolean;
import space.arim.dazzleconf.annote.ConfDefault.DefaultInteger;
import space.arim.dazzleconf.annote.ConfDefault.DefaultObject;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter.Order;

import java.util.List;

public interface DynamicConfig {
    @Order(1)
    @ConfKey("enabled")
    @DefaultBoolean(false)
    @ConfComments("(Default = false) Enables this feature.")
    boolean enabled();

    @Order(2)
    @ConfKey("target-mspt")
    @DefaultInteger(35)
    @IntegerRange(min = 2)
    @ConfComments("(Default = 35) The average MSPT to target.")
    int targetMspt();

    @Order(3)
    @ConfKey("update-rate")
    @DefaultInteger(15)
    @IntegerRange(min = 1)
    @ConfComments("(Default = 15) The amount of seconds between dynamic performance updates.")
    int updateRate();

    @Order(4)
    @ConfKey("view-distance-update-rate")
    @DefaultInteger(150)
    @IntegerRange(min = 1)
    @ConfComments({
            "(Default = 150) The amount of seconds between dynamic viewdistance updates.",
            "This value is separate from the other checks because it makes all clients reload their chunks."
    })
    int viewDistanceUpdateRate();

    @Order(5)
    @ConfKey("dynamic-settings")
    @DefaultObject("defaultSettings")
    @ConfValidator(Validators.UniqueSettings.class)
    @ConfComments({
            "(Default = [CHUNK_TICK_DISTANCE, MOBCAP, SIMULATION_DISTANCE, VIEW_DISTANCE])",
            "The settings that will be decreased when the server is overloaded, in the specified order.",
            "Removing a setting from the list will disable it."
    })
    List<@SubSection Setting> settings();

    static List<Setting> defaultSettings() {
        return Lists.newArrayList(
                Setting.of(DynamicSetting.CHUNK_TICK_DISTANCE, 10, 2, 1),
                Setting.of(DynamicSetting.MOBCAP, 100, 30, 10),
                Setting.of(DynamicSetting.SIMULATION_DISTANCE, 10, 2, 1),
                Setting.of(DynamicSetting.VIEW_DISTANCE, 10, 2, 1)
        );
    }
}
