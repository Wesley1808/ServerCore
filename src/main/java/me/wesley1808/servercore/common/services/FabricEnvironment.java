package me.wesley1808.servercore.common.services;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import java.nio.file.Path;
import java.util.Optional;

public class FabricEnvironment implements Environment {
    private final String version;
    private final Path configDir;

    public FabricEnvironment() {
        this.version = this.getVersionString();
        this.configDir = FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public Path getConfigDir() {
        return this.configDir;
    }

    @Override
    public String getVersion() {
        return this.version;
    }

    private String getVersionString() {
        Optional<ModContainer> optional = FabricLoader.getInstance().getModContainer("servercore");
        return optional.map(container -> container.getMetadata().getVersion().getFriendlyString()).orElse("Unknown");
    }
}
