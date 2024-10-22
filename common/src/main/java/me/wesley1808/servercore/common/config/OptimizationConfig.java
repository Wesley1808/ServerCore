package me.wesley1808.servercore.common.config;

import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault.DefaultBoolean;
import space.arim.dazzleconf.annote.ConfHeader;
import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter.Order;

@ConfHeader({
        "Allows you to toggle specific optimizations that don't have full vanilla parity.",
        "These settings will only take effect after server restarts.\n",
})
public interface OptimizationConfig extends Copyable {
    @Order(1)
    @ConfKey("reduce-sync-loads")
    @DefaultBoolean(true)
    @ConfComments({
            "Prevents many different lagspikes caused by loading chunks synchronously.",
            "This for example causes maps to only update loaded chunks, which depending on the viewdistance can be a smaller radius than vanilla."
    })
    boolean reduceSyncLoads();

    @Order(2)
    @ConfKey("cache-ticking-chunks")
    @DefaultBoolean(true)
    @ConfComments({
            "Can significantly reduce the time spent on chunk iteration by only updating the ticking chunks list each second, rather than each tick.",
            "This is especially useful for servers with a high playercount and / or viewdistance."
    })
    boolean cacheTickingChunks();

    @Order(3)
    @ConfKey("optimize-command-blocks")
    @DefaultBoolean(false)
    @ConfComments({
            "Optimizes command block executions by caching parsed commands.",
            "Command parsing is a relatively expensive operation. By caching it we avoid parsing the same command every time it is executed.",
    })
    boolean optimizeCommandBlocks();

    @Order(4)
    @ConfKey("fast-biome-lookups")
    @DefaultBoolean(false)
    @ConfComments({
            "Can significantly reduce time spent on mobspawning, but isn't as accurate as vanilla on biome borders.",
            "This may cause mobs from another biome to spawn a few blocks across a biome border (this does not affect structure spawning!)."
    })
    boolean fastBiomeLookups();

    @Order(5)
    @ConfKey("cancel-duplicate-fluid-ticks")
    @DefaultBoolean(false)
    @ConfComments({
            "Fluid random ticks, like lava spreading fire, are run twice each game tick.",
            "Enabling this will cancel the 'duplicate' second fluid tick, but this may cause slight behavior changes."
    })
    boolean cancelDuplicateFluidTicks();

    @Override
    default OptimizationConfig optimizedCopy() {
        return this;
    }
}
