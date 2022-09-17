package me.wesley1808.servercore.common.services;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import eu.pb4.placeholders.api.PlaceholderHandler;
import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import me.wesley1808.servercore.common.dynamic.DynamicManager;
import me.wesley1808.servercore.common.dynamic.DynamicSetting;
import me.wesley1808.servercore.common.utils.Statistics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public final class PlaceHolders {
    // Caches expensive to calculate global values for 1 tick.
    // This way we won't have to re-calculate these values for every single player on the same tick.
    private static final Cache<String, String> CACHE = CacheBuilder.newBuilder()
            .expireAfterWrite(50, TimeUnit.MILLISECONDS)
            .build();

    public static void register() {
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
                (ctx, arg) -> PlaceholderResult.value(DynamicManager.getModifierAsPercentage())
        );

        register("chunk_count", (ctx, arg) -> {
            boolean onlyLoaded = Objects.equals(arg, "loaded");
            if (ctx.hasPlayer()) {
                return cachedValue(onlyLoaded ? "chunk_count_loaded" : "chunk_count", () -> String.valueOf(Statistics.getChunkCount(onlyLoaded)));
            }

            return PlaceholderResult.value(String.valueOf(Statistics.getChunkCount(onlyLoaded)));
        });

        register("entity_count", (ctx, arg) -> {
            ServerPlayer player = ctx.player();
            if (player != null) {
                if (Objects.equals(arg, "nearby")) {
                    return PlaceholderResult.value(String.valueOf(Statistics.getEntitiesNear(player).size()));
                }

                return cachedValue("entity_count", () -> String.valueOf(Statistics.getAllEntities().size()));
            }

            return PlaceholderResult.value(String.valueOf(Statistics.getAllEntities().size()));
        });

        register("block_entity_count", (ctx, arg) -> {
            ServerPlayer player = ctx.player();
            if (player != null) {
                if (Objects.equals(arg, "nearby")) {
                    return PlaceholderResult.value(String.valueOf(Statistics.getBlockEntitiesNear(player).size()));
                }

                return cachedValue("block_entity_count", () -> String.valueOf(Statistics.getAllBlockEntities().size()));
            }

            return PlaceholderResult.value(String.valueOf(Statistics.getAllBlockEntities().size()));
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
