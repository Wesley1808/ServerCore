package me.wesley1808.servercore.common.config;

import me.wesley1808.servercore.common.config.data.CommandConfig;
import me.wesley1808.servercore.common.config.data.FeatureConfig;
import me.wesley1808.servercore.common.config.data.activation_range.ActivationRangeConfig;
import me.wesley1808.servercore.common.config.data.breeding_cap.BreedingCapConfig;
import me.wesley1808.servercore.common.config.data.dynamic.DynamicConfig;
import me.wesley1808.servercore.common.config.data.mob_spawning.MobSpawnConfig;
import me.wesley1808.servercore.common.config.impl.MainConfigImpl;
import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfHeader;
import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.annote.SubSection;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter.Order;

@ConfHeader({
        "The main configuration file for ServerCore.",
        "Most of these settings can be reloaded without restarting using /servercore reload.\n"
})
public interface MainConfig extends Copyable {
    @Order(1)
    @SubSection
    @ConfKey("features")
    @ConfComments("Most miscellaneous feature toggles.")
    FeatureConfig features();

    @Order(2)
    @SubSection
    @ConfKey("dynamic")
    @ConfComments("Automatically modifies dynamic settings based on the server performance.")
    DynamicConfig dynamic();

    @Order(3)
    @SubSection
    @ConfKey("breeding-cap")
    @ConfComments("A special mobcap that only affects the breeding of animals and villagers.")
    BreedingCapConfig breedingCap();

    @Order(4)
    @SubSection
    @ConfKey("mob-spawning")
    @ConfComments("Gives more control over mob spawning.")
    MobSpawnConfig mobSpawning();

    @Order(5)
    @SubSection
    @ConfKey("commands")
    @ConfComments("Settings for commands and their formatting.")
    CommandConfig commands();

    @Order(6)
    @SubSection
    @ConfKey("activation-range")
    @ConfComments({
            "Activation range can drastically reduce the amount of lag caused by ticking entities.",
            "It does this by cleverly skipping certain entity ticks based on the distance to players and other factors, like immunity checks.",
            "Immunity checks determine whether an entity should be ticked even when it's outside the activation range, like for example when it is falling or takes damage.",
            "Note: while this is a very powerful feature, it can still slow down mobfarms and break very specific technical contraptions.",
    })
    ActivationRangeConfig activationRange();

    @Override
    default MainConfig optimizedCopy() {
        return new MainConfigImpl(this);
    }
}
