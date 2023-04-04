package me.wesley1808.servercore.common.services;

import me.wesley1808.servercore.common.ServerCore;

import java.nio.file.Path;
import java.util.Optional;
import java.util.ServiceLoader;

public interface Environment {
    Environment INSTANCE = Environment.load();

    boolean isModLoaded(String modId);

    Path getConfigDir();

    String getVersion();

    private static Environment load() {
        Optional<Environment> optional = ServiceLoader.load(Environment.class).findFirst();
        if (optional.isPresent()) {
            return optional.get();
        } else {
            // This should never happen.
            ServerCore.LOGGER.error("-----------------------------------------------------------------------------------");
            ServerCore.LOGGER.error("");
            ServerCore.LOGGER.error("[ServerCore] Unable to find valid environment. This will cause the server to crash!");
            ServerCore.LOGGER.error("");
            ServerCore.LOGGER.error("-----------------------------------------------------------------------------------");
            throw new NullPointerException("Unable to find valid environment!");
        }
    }
}
