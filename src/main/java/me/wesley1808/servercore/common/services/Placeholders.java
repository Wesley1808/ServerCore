package me.wesley1808.servercore.common.services;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import eu.pb4.placeholders.PlaceholderAPI;
import eu.pb4.placeholders.PlaceholderHandler;
import eu.pb4.placeholders.PlaceholderResult;
import me.wesley1808.servercore.common.utils.DynamicManager;
import me.wesley1808.servercore.common.utils.Statistics;
import net.minecraft.resources.ResourceLocation;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public final class Placeholders {
    // Caches expensive to calculate global values for 1 tick.
    // This way we won't have to re-calculate these values for every single player on the same tick.
    private static final Cache<String, String> CACHE = CacheBuilder.newBuilder()
            .expireAfterWrite(50, TimeUnit.MILLISECONDS)
            .build();

    public static void register() {
        register("view_distance",
                (ctx) -> PlaceholderResult.value(String.valueOf(DynamicManager.getViewDistance()))
        );

        register("simulation_distance",
                (ctx) -> PlaceholderResult.value(String.valueOf(DynamicManager.getSimulationDistance()))
        );

        register("chunk_tick_distance",
                (ctx) -> PlaceholderResult.value(String.valueOf(DynamicManager.getChunkTickDistance()))
        );

        register("mobcap_multiplier",
                (ctx) -> PlaceholderResult.value(DynamicManager.getModifierAsString())
        );

        register("chunk_count", (ctx) -> {
            if (ctx.hasPlayer()) {
                return cachedValue("chunk_count", () -> String.valueOf(Statistics.getLoadedChunkCount()));
            }

            return PlaceholderResult.value(String.valueOf(Statistics.getLoadedChunkCount()));
        });

        register("entity_count", (ctx) -> {
            if (ctx.hasPlayer()) {
                if (ctx.getArgument().equals("player")) {
                    return PlaceholderResult.value(String.valueOf(Statistics.getEntitiesNear(ctx.getPlayer()).size()));
                }

                return cachedValue("entity_count", () -> String.valueOf(Statistics.getAllEntities().size()));
            }

            return PlaceholderResult.value(String.valueOf(Statistics.getAllEntities().size()));
        });

        register("block_entity_count", (ctx) -> {
            if (ctx.hasPlayer()) {
                if (ctx.getArgument().equals("player")) {
                    return PlaceholderResult.value(String.valueOf(Statistics.getBlockEntitiesNear(ctx.getPlayer()).size()));
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
        PlaceholderAPI.register(new ResourceLocation("servercore", name), handler);
    }
}
