package me.wesley1808.servercore.common.config.data.mob_spawning;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.wesley1808.servercore.common.config.impl.mob_spawning.MobSpawnEntryImpl;
import me.wesley1808.servercore.common.interfaces.IMobCategory;
import net.minecraft.world.entity.MobCategory;
import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault.DefaultObject;
import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.annote.SubSection;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter.Order;

import java.util.List;

public interface MobSpawnConfig {
    @Order(1)
    @SubSection
    @ConfKey("zombie-reinforcement-spawning")
    @ConfComments({
            "Mobcap settings for zombie reinforcements.",
            "► enforce-mobcaps = Whether to enforce mobcaps for this type of mobspawning.",
            "► mobcap-modifier = The modifier to apply to this enforced mobcap. This modifier only affects this type of mobspawning.",
            "Since these mobspawns normally wouldn't be affected by the mobcap, you might want to allow them to spawn a bit over it."
    })
    EnforcedMobcap zombieReinforcements();

    @Order(2)
    @SubSection
    @ConfKey("portal-randomtick-spawning")
    @ConfComments("Mobcap settings for zombified piglin spawning from nether portal random ticks.")
    EnforcedMobcap portalRandomTicks();

    @Order(3)
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
                entries.add(new MobSpawnEntryImpl(
                        category,
                        IMobCategory.getOriginalCapacity(category),
                        category.isPersistent() ? 400 : 1
                ));
            }
        }
        return entries;
    }
}
