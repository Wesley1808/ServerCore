package me.wesley1808.servercore.common.services.platform;

import me.wesley1808.servercore.common.ServerCore;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Optional;
import java.util.ServiceLoader;

public final class PlatformHelper {
    private static final ModPlatform MOD_PLATFORM = PlatformHelper.load(ModPlatform.class);
    private static MinecraftPlatform minecraftPlatform;

    public static void initialize() {
        PlatformHelper.minecraftPlatform = PlatformHelper.load(MinecraftPlatform.class);
    }

    public static boolean isModLoaded(String modId) {
        return MOD_PLATFORM.isModLoaded(modId);
    }

    public static Path getConfigDir() {
        return MOD_PLATFORM.getConfigDir();
    }

    public static String getVersion() {
        return MOD_PLATFORM.getVersion();
    }

    public static Component parseText(String input) {
        return minecraftPlatform.parseText(input);
    }

    public static boolean hasPermission(CommandSourceStack source, String node, int level) {
        return minecraftPlatform.hasPermission(source, node, level);
    }

    public static boolean shouldTickChunk(ChunkMap chunkMap, ChunkPos pos) {
        return chunkMap.anyPlayerCloseEnoughForSpawning(pos) || minecraftPlatform.shouldForceChunkTicks(chunkMap, pos);
    }

    @Nullable
    public static EntityType<?> getEntityType(String key) {
        var optional = EntityType.byString(key);
        if (optional.isPresent()) {
            return optional.get();
        } else {
            return minecraftPlatform.getEntityType(key);
        }
    }

    @NotNull
    public static ResourceLocation getEntityTypeKey(EntityType<?> type) {
        return minecraftPlatform.getEntityTypeKey(type)
                .map(ResourceKey::location)
                .orElse(EntityType.getKey(type));
    }

    private static <T> T load(Class<T> clazz) {
        Optional<T> optional = ServiceLoader.load(clazz).findFirst();
        if (optional.isPresent()) {
            return optional.get();
        } else {
            // This should never happen.
            ServerCore.LOGGER.error("-----------------------------------------------------------------------------------------");
            ServerCore.LOGGER.error("");
            ServerCore.LOGGER.error("[ServerCore] Unable to find valid {}. This will cause the server to crash!", clazz.getSimpleName());
            ServerCore.LOGGER.error("");
            ServerCore.LOGGER.error("-----------------------------------------------------------------------------------------");
            throw new NullPointerException("Unable to find valid " + clazz.getSimpleName());
        }
    }
}
