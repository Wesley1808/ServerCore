package me.wesley1808.servercore.common.config.data.breeding_cap;

import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault.DefaultBoolean;
import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.annote.SubSection;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter.Order;

public interface BreedingCapConfig {
    @Order(1)
    @ConfKey("enabled")
    @DefaultBoolean(false)
    @ConfComments("(Default = false) Enables this feature.")
    boolean enabled();

    @Order(2)
    @SubSection
    @ConfKey("villagers")
    @ConfComments({
            "The breeding caps for mobs of each group.",
            "► limit = The limit of mobs of the same type within range. Setting this to negative will disable the breeding cap.",
            "► range = The range it will check for entities of the same type."
    })
    BreedingCap villagers();

    @Order(3)
    @SubSection
    @ConfKey("animals")
    BreedingCap animals();
}
