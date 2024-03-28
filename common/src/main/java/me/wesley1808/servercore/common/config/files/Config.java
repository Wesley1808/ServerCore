package me.wesley1808.servercore.common.config.files;

import me.wesley1808.servercore.common.config.files.data.CommandConfig;
import me.wesley1808.servercore.common.config.files.data.FeatureConfig;
import me.wesley1808.servercore.common.config.files.data.activation_range.ActivationRangeConfig;
import me.wesley1808.servercore.common.config.files.data.breeding_cap.BreedingCapConfig;
import me.wesley1808.servercore.common.config.files.data.dynamic.DynamicConfig;
import me.wesley1808.servercore.common.config.files.data.mob_spawning.MobSpawnConfig;
import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfHeader;
import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.annote.SubSection;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter.Order;

@ConfHeader({
        "The main configuration file for ServerCore.",
        "Most of these settings can be reloaded without restarting using /servercore reload.\n"
})
public interface Config {
    @Order(1)
    @SubSection
    @ConfKey("features")
    @ConfComments("Lets you enable / disable certain features and modify them.")
    FeatureConfig features();

    @Order(2)
    @SubSection
    @ConfKey("dynamic")
    @ConfComments("Modifies mobcaps, no-chunk-tick, simulation and view-distance depending on the MSPT.")
    DynamicConfig dynamic();

    @Order(3)
    @SubSection
    @ConfKey("breeding-cap")
    @ConfComments("Stops animals / villagers from breeding if there are too many of the same type nearby.")
    BreedingCapConfig breedingCap();

    @Order(4)
    @SubSection
    @ConfKey("mob-spawning")
    @ConfComments("Allows you to modify individual mobcaps and their frequency of spawn attempts.")
    MobSpawnConfig mobSpawning();

    @Order(5)
    @SubSection
    @ConfKey("commands")
    @ConfComments("Allows you to disable specific commands and modify the way some of them are formatted.")
    CommandConfig commands();

    @Order(6)
    @SubSection
    @ConfKey("activation-range")
    @ConfComments("Ticks entities less often when they are further away from players.")
    ActivationRangeConfig activationRange();


}
