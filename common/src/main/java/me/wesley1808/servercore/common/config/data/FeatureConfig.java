package me.wesley1808.servercore.common.config.data;

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
    @ConfKey("prevent-enderpearl-chunkloading")
    @DefaultBoolean(false)
    @ConfComments({
            "Reverts enderpearl behavior to pre-1.21.2. The following will change when this setting is enabled:",
            "► Enderpearls will no longer load chunks around them.",
            "► Enderpearls will load / unload with chunks again instead of with their owner joining / leaving the game.",
            "Note: Toggling this setting may cause some old enderpearls from before the toggle to be lost."
    })
    boolean preventEnderpearlChunkLoading();

    @Order(2)
    @ConfKey("chunk-tick-distance-affects-random-ticks")
    @DefaultBoolean(false)
    @ConfComments({
            "Whether the chunk-tick-distance dynamic setting should affect random ticks.",
            "Enabling this will stop chunks from performing random ticks when they are outside of this distance from any player.",
            "That includes chunks loaded by enderpearls or portals, breaking vanilla behavior."
    })
    boolean chunkTickDistanceAffectsRandomTicks();

    @Order(3)
    @ConfKey("prevent-moving-into-unloaded-chunks")
    @DefaultBoolean(false)
    @ConfComments("Prevents lagspikes caused by players moving into unloaded chunks.")
    boolean preventMovingIntoUnloadedChunks();

    @Order(4)
    @ConfKey("autosave-interval-seconds")
    @DefaultInteger(300)
    @IntegerRange(min = 5)
    @ConfComments("The amount of seconds between auto-saves when /save-on is active.")
    int autosaveIntervalSeconds();

    @Order(5)
    @ConfKey("xp-merge-fraction")
    @DefaultInteger(40)
    @IntegerRange(min = 1)
    @ConfComments({
            "The fraction that decides the chance of experience orbs being able to merge with each other. (1 = 100%, 40 = 2.5%)",
            "Note that just like in vanilla, experience orbs will still need to be of the same size to actually merge."
    })
    int xpMergeFraction();

    @Order(6)
    @ConfKey("xp-merge-radius")
    @DefaultDouble(0.5)
    @NumericRange(min = 0.5)
    @ConfComments("The radius in blocks that experience orbs will merge at.")
    double xpMergeRadius();

    @Order(7)
    @ConfKey("item-merge-radius")
    @DefaultDouble(0.5)
    @NumericRange(min = 0.5)
    @ConfComments("The radius in blocks that items will merge at.")
    double itemMergeRadius();

    @Order(8)
    @ConfKey("lobotomize-villagers.enabled")
    @DefaultBoolean(false)
    @ConfComments("Makes villagers tick less often if they are stuck in a 1x1 space.")
    boolean lobotomizeVillagers();

    @Order(9)
    @ConfKey("lobotomize-villagers.tick-interval")
    @DefaultInteger(20)
    @IntegerRange(min = 2)
    @ConfComments("Decides the interval in between villager ticks when lobotomized.")
    int lobotomizedTickInterval();
}
