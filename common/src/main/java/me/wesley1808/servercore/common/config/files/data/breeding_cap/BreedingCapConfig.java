package me.wesley1808.servercore.common.config.files.data.breeding_cap;

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
    @ConfKey("villager")
    @ConfComments({
            "The breeding caps for mobs of each group.",
            "► count = Maximum count before stopping entities of the same type from breeding. Setting this to negative will disable the limit.",
            "► range = The range it will check for entities of the same type."
    })
    BreedingCap villager();

    @Order(3)
    @SubSection
    @ConfKey("animals")
    BreedingCap animals();
}
