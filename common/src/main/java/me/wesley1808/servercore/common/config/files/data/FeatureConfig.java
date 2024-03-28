package me.wesley1808.servercore.common.config.files.data;

import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault.DefaultBoolean;
import space.arim.dazzleconf.annote.ConfDefault.DefaultDouble;
import space.arim.dazzleconf.annote.ConfDefault.DefaultInteger;
import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.annote.IntegerRange;
import space.arim.dazzleconf.annote.NumericRange;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter.Order;

public interface FeatureConfig {
    @Order(1)
    @ConfKey("disable-spawn-chunks")
    @DefaultBoolean(false)
    @ConfComments("(Default = false) Stops the server from loading spawn chunks.")
    boolean disableSpawnChunks();

    @Order(2)
    @ConfKey("prevent-moving-into-unloaded-chunks")
    @DefaultBoolean(false)
    @ConfComments("(Default = false) Prevents lagspikes caused by players moving into unloaded chunks.")
    boolean preventMovingIntoUnloadedChunks();

    @Order(3)
    @ConfKey("autosave-interval-seconds")
    @DefaultInteger(300)
    @IntegerRange(min = 5)
    @ConfComments("(Default = 300) The amount of seconds between auto-saves when /save-on is active.")
    int autosaveIntervalSeconds();

    @Order(4)
    @ConfKey("xp-merge-chance")
    @DefaultInteger(40)
    @IntegerRange(min = 1)
    @ConfComments("(Default = 40) Decides the chance of XP orbs being able to merge together (1 in X).")
    int xpMergeChance();

    @Order(5)
    @ConfKey("xp-merge-radius")
    @DefaultDouble(0.5)
    @NumericRange(min = 0.5)
    @ConfComments("(Default = 0.5) Decides the radius in blocks that items / XP orbs will merge at.")
    double xpMergeRadius();

    @Order(6)
    @ConfKey("item-merge-radius")
    @DefaultDouble(0.5)
    @NumericRange(min = 0.5)
    double itemMergeRadius();

    @Order(7)
    @ConfKey("lobotomize-villagers.enabled")
    @DefaultBoolean(false)
    @ConfComments("(Default = false) Makes villagers tick less often if they are stuck in a 1x1 space.")
    boolean lobotomizeVillagers();

    @Order(8)
    @ConfKey("lobotomize-villagers.tick-interval")
    @DefaultInteger(20)
    @IntegerRange(min = 2)
    @ConfComments("(Default = 20) Decides the interval in between villager ticks when lobotomized.")
    int lobotomizedTickInterval();
}
