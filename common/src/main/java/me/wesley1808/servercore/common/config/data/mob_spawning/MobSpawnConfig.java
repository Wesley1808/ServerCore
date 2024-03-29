package me.wesley1808.servercore.common.config.data.mob_spawning;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.wesley1808.servercore.common.config.impl.mob_spawning.MobSpawnEntryImpl;
import net.minecraft.world.entity.MobCategory;
import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault.DefaultObject;
import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.annote.SubSection;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter.Order;

import java.util.List;

public interface MobSpawnConfig {
    @Order(1)
    @ConfKey("categories")
    @DefaultObject("defaultCategories")
    @ConfComments({
            "A list of mob categories with their respective mobcap and spawn interval.",
            "► category = The vanilla spawn category.",
            "► mobcap = The maximum amount of entities in the same category that can spawn near a player.",
            "► spawn-interval = The interval between spawn attempts in ticks. Higher values mean less frequent spawn attempts.",
    })
    List<@SubSection MobSpawnEntry> categories();

    static List<MobSpawnEntry> defaultCategories() {
        ObjectArrayList<MobSpawnEntry> entries = new ObjectArrayList<>();
        for (MobCategory category : MobCategory.values()) {
            if (category != MobCategory.MISC) {
                entries.add(new MobSpawnEntryImpl(category));
            }
        }
        return entries;
    }
}
