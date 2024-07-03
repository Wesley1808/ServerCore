package me.wesley1808.servercore.common.config.data.dynamic;

import com.google.common.collect.Lists;
import me.wesley1808.servercore.common.config.impl.dynamic.DynamicConfigImpl;
import me.wesley1808.servercore.common.config.impl.dynamic.SettingImpl;
import me.wesley1808.servercore.common.dynamic.DynamicSetting;
import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault.DefaultBoolean;
import space.arim.dazzleconf.annote.ConfDefault.DefaultInteger;
import space.arim.dazzleconf.annote.ConfDefault.DefaultObject;
import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.annote.IntegerRange;
import space.arim.dazzleconf.annote.SubSection;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter.Order;

import java.util.List;

public interface DynamicConfig {
    @Order(1)
    @ConfKey("enabled")
    @DefaultBoolean(false)
    @ConfComments("Enables dynamic performance checks.")
    boolean enabled();

    @Order(2)
    @ConfKey("target-mspt")
    @DefaultInteger(35)
    @IntegerRange(min = 2)
    @ConfComments("The average MSPT to target.")
    int targetMspt();

    @Order(3)
    @ConfKey("dynamic-settings")
    @DefaultObject("defaultSettings")
    @ConfComments({
            "The settings that will be decreased when the server is overloaded, in the specified order.",
            "You can remove settings from the list that you don't want to be dynamically adjusted.",
            "► max = The maximum value the server will increase the setting to.",
            "► min = The minimum value the server will decrease the setting to.",
            "► increment = The amount the setting will be increased or decreased by.",
            "► interval = The amount of seconds between each check to increase or decrease."
    })
    List<@SubSection Setting> settings();

    default DynamicConfig optimizedCopy() {
        return new DynamicConfigImpl(this);
    }

    static List<Setting> defaultSettings() {
        return Lists.newArrayList(
                new SettingImpl(DynamicSetting.CHUNK_TICK_DISTANCE, 10, 6, 1, 15),
                new SettingImpl(DynamicSetting.MOBCAP_PERCENTAGE, 100, 50, 10, 15),
                new SettingImpl(DynamicSetting.SIMULATION_DISTANCE, 10, 6, 1, 15),
                new SettingImpl(DynamicSetting.CHUNK_TICK_DISTANCE, 6, 2, 1, 15),
                new SettingImpl(DynamicSetting.MOBCAP_PERCENTAGE, 50, 30, 10, 15),
                new SettingImpl(DynamicSetting.SIMULATION_DISTANCE, 6, 2, 1, 15),
                new SettingImpl(DynamicSetting.VIEW_DISTANCE, 10, 5, 1, 150)
        );
    }
}
