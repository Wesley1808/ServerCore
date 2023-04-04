package me.wesley1808.servercore.common.services;

import me.wesley1808.servercore.common.ServerCore;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

import java.nio.file.Path;
import java.util.Optional;
import java.util.ServiceLoader;

public interface Platform {
    Platform INSTANCE = Platform.load();

    boolean isModLoaded(String modId);

    Path getConfigDir();

    String getVersion();

    boolean hasPermission(SharedSuggestionProvider source, String permission, int level);

    Component parseText(String input);

    private static Platform load() {
        Optional<Platform> optional = ServiceLoader.load(Platform.class).findFirst();
        if (optional.isPresent()) {
            return optional.get();
        } else {
            // This should never happen.
            ServerCore.LOGGER.error("-----------------------------------------------------------------------------------");
            ServerCore.LOGGER.error("");
            ServerCore.LOGGER.error("[ServerCore] Unable to find valid platform. This will cause the server to crash!");
            ServerCore.LOGGER.error("");
            ServerCore.LOGGER.error("-----------------------------------------------------------------------------------");
            throw new NullPointerException("Unable to find valid platform!");
        }
    }
}
