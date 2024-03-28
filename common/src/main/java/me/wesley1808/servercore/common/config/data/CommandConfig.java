package me.wesley1808.servercore.common.config.data;

import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault.DefaultBoolean;
import space.arim.dazzleconf.annote.ConfDefault.DefaultString;
import space.arim.dazzleconf.annote.ConfDefault.DefaultStrings;
import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter.Order;

import java.util.List;

public interface CommandConfig {
    @Order(1)
    @ConfKey("status.enabled")
    @DefaultBoolean(true)
    @ConfComments("Enables / disables the /servercore status command.")
    boolean statusCommandEnabled();

    @Order(2)
    @ConfKey("status.title")
    @DefaultString("<dark_aqua>${line} <aqua>ServerCore</aqua> ${line}</dark_aqua>")
    @ConfComments("The title for the /servercore status command.")
    String statusTitle();

    @Order(3)
    @ConfKey("status.content")
    @DefaultStrings({
            "<dark_gray>» <dark_aqua>Version: <green>${version}",
            "<dark_gray>» <dark_aqua>Mobcap Percentage: <green>${mobcap_percentage}",
            "<dark_gray>» <dark_aqua>Chunk-Tick Distance: <green>${chunk_tick_distance}",
            "<dark_gray>» <dark_aqua>Simulation Distance: <green>${simulation_distance}",
            "<dark_gray>» <dark_aqua>View Distance: <green>${view_distance}"
    })
    @ConfComments("The content for the /servercore status command.")
    List<String> statusContents();

    @Order(4)
    @ConfKey("mobcap.enabled")
    @DefaultBoolean(true)
    @ConfComments("Enables / disables the /mobcaps command.")
    boolean mobcapsCommandEnabled();

    @Order(5)
    @ConfKey("mobcap.title")
    @DefaultString("<dark_aqua>${line} <aqua>Mobcaps</aqua> (<aqua>${mobcap_percentage}</aqua>) ${line}</dark_aqua>")
    @ConfComments("The title for the /mobcaps command.")
    String mobcapTitle();

    @Order(6)
    @ConfKey("mobcap.content")
    @DefaultString("<dark_gray>» <dark_aqua>${name}:</dark_aqua> <green>${current}</green> / <green>${capacity}</green></dark_gray>")
    @ConfComments("The content for the /mobcaps command. This is displayed for every existing spawngroup.")
    String mobcapContent();

    @Order(7)
    @ConfKey("statistics.title")
    @DefaultString("<dark_aqua>${line} <aqua>Statistics</aqua> ${line}</dark_aqua>")
    @ConfComments("The title for the /statistics command.")
    String statisticsTitle();

    @Order(8)
    @ConfKey("statistics.content")
    @DefaultStrings({
            "<dark_gray>» <dark_aqua>TPS:</dark_aqua> <green>${tps}</green> - <dark_aqua>MSPT: <green>${mspt}",
            "<dark_gray>» <dark_aqua>Total chunk count: <green>${chunk_count}",
            "<dark_gray>» <dark_aqua>Total entity count: <green>${entity_count}",
            "<dark_gray>» <dark_aqua>Total block entity count: <green>${block_entity_count}"
    })
    @ConfComments("The content for the /statistics command.")
    List<String> statisticsContents();

    @Order(9)
    @ConfKey("statistics.page-title")
    @DefaultString("<dark_aqua>${line} <aqua>${title}</aqua> by <aqua>${type}</aqua> ${line}</dark_aqua>")
    @ConfComments("The title for the /statistics (block) entities command.")
    String statisticsPageTitle();

    @Order(10)
    @ConfKey("statistics.page-title-player")
    @DefaultString("<dark_aqua>${line} <aqua>${title}</aqua> for <aqua>${player}</aqua> ${line}</dark_aqua>")
    String statisticsPageTitlePlayer();

    @Order(11)
    @ConfKey("statistics.page-content")
    @DefaultString("<green>${index}. <dark_aqua>${name}</dark_aqua> ${count}</green>")
    @ConfComments("The content for the /statistics (block) entities command. This is displayed for every entry.")
    String statisticsPageContent();

    @Order(12)
    @ConfKey("statistics.page-footer")
    @DefaultString("<dark_aqua>${line} <green>${prev_page}</green> Page <aqua>${page}</aqua> of <aqua>${page_count}</aqua> <green>${next_page}</green> ${line}")
    @ConfComments("The footer for the /statistics (block) entities command.")
    String statisticsPageFooter();
}