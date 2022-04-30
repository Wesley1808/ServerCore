package me.wesley1808.servercore.utils;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import eu.pb4.placeholders.PlaceholderAPI;
import eu.pb4.placeholders.PlaceholderResult;
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
        PlaceholderAPI.register(resource("view_distance"),
                (ctx) -> PlaceholderResult.value(String.valueOf(TickManager.getViewDistance()))
        );

        PlaceholderAPI.register(resource("simulation_distance"),
                (ctx) -> PlaceholderResult.value(String.valueOf(TickManager.getSimulationDistance()))
        );

        PlaceholderAPI.register(resource("chunk_tick_distance"),
                (ctx) -> PlaceholderResult.value(String.valueOf(TickManager.getChunkTickDistance()))
        );

        PlaceholderAPI.register(resource("mobcap_multiplier"),
                (ctx) -> PlaceholderResult.value(TickManager.getModifierAsString())
        );

        PlaceholderAPI.register(resource("chunk_count"), (ctx) -> {
            if (ctx.hasPlayer()) {
                return cachedValue("chunk_count", () -> String.valueOf(Statistics.getLoadedChunkCount()));
            }

            return PlaceholderResult.value(String.valueOf(Statistics.getLoadedChunkCount()));
        });

        PlaceholderAPI.register(resource("entity_count"), (ctx) -> {
            if (ctx.hasPlayer()) {
                if (ctx.getArgument().equals("player")) {
                    return PlaceholderResult.value(String.valueOf(Statistics.getEntitiesNear(ctx.getPlayer()).size()));
                }

                return cachedValue("entity_count", () -> String.valueOf(Statistics.getAllEntities().size()));
            }

            return PlaceholderResult.value(String.valueOf(Statistics.getAllEntities().size()));
        });

        PlaceholderAPI.register(resource("block_entity_count"), (ctx) -> {
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

    private static ResourceLocation resource(String name) {
        return new ResourceLocation("servercore", name);
    }
}
