package me.wesley1808.servercore.fabric.common;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import eu.pb4.placeholders.api.PlaceholderHandler;
import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import me.wesley1808.servercore.common.dynamic.DynamicSetting;
import me.wesley1808.servercore.common.utils.statistics.Statistics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class PlaceHolders {
    // Caches expensive to calculate global values for 1 tick.
    // This way we won't have to re-calculate these values for every single player on the same tick.
    private static final Cache<String, String> CACHE = CacheBuilder.newBuilder()
            .expireAfterWrite(50, TimeUnit.MILLISECONDS)
            .build();

    public static void register() {
        PlaceHolders.registerDynamic();
        PlaceHolders.registerStatistics();
    }

    private static void registerDynamic() {
        register("view_distance",
                (ctx, arg) -> PlaceholderResult.value(String.valueOf(DynamicSetting.VIEW_DISTANCE.get()))
        );

        register("simulation_distance",
                (ctx, arg) -> PlaceholderResult.value(String.valueOf(DynamicSetting.SIMULATION_DISTANCE.get()))
        );

        register("chunk_tick_distance",
                (ctx, arg) -> PlaceholderResult.value(String.valueOf(DynamicSetting.CHUNK_TICK_DISTANCE.get()))
        );

        register("mobcap_percentage",
                (ctx, arg) -> PlaceholderResult.value(String.format("%d%%", DynamicSetting.MOBCAP_PERCENTAGE.get()))
        );
    }

    private static void registerStatistics() {
        register("chunk_count", (ctx, arg) -> {
            Statistics statistics = Statistics.getInstance(ctx.server());
            boolean onlyLoaded = Objects.equals(arg, "loaded");
            return cachedValue(onlyLoaded ? "chunk_count_loaded" : "chunk_count", () -> String.valueOf(statistics.getChunkCount(onlyLoaded)));
        });

        register("entity_count", (ctx, arg) -> {
            Statistics statistics = Statistics.getInstance(ctx.server());
            ServerPlayer player = ctx.player();
            if (player != null && Objects.equals(arg, "nearby")) {
                return PlaceholderResult.value(String.valueOf(statistics.getEntitiesNear(player).size()));
            }

            return cachedValue("entity_count", () -> String.valueOf(statistics.getAllEntities().size()));
        });

        register("block_entity_count", (ctx, arg) -> {
            Statistics statistics = Statistics.getInstance(ctx.server());
            ServerPlayer player = ctx.player();
            if (player != null && Objects.equals(arg, "nearby")) {
                return PlaceholderResult.value(String.valueOf(statistics.getBlockEntitiesNear(player).size()));
            }

            return cachedValue("block_entity_count", () -> String.valueOf(statistics.getAllBlockEntities().size()));
        });
    }

    private static PlaceholderResult cachedValue(String key, Callable<String> valueLoader) {
        try {
            return PlaceholderResult.value(CACHE.get(key, valueLoader));
        } catch (ExecutionException ex) {
            return PlaceholderResult.invalid("Failed to get value for key: " + key);
        }
    }

    private static void register(String name, PlaceholderHandler handler) {
        Placeholders.register(new ResourceLocation("servercore", name), handler);
    }
}
