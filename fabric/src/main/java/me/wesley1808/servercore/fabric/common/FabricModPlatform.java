package me.wesley1808.servercore.fabric.common;

import me.wesley1808.servercore.common.ServerCore;
import me.wesley1808.servercore.common.services.platform.ModPlatform;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import java.nio.file.Path;
import java.util.Optional;

public class FabricModPlatform implements ModPlatform {
    private final String version;
    private final Path configDir;

    public FabricModPlatform() {
        this.version = this.getVersionString();
        this.configDir = FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public boolean isClient() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
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
        Optional<ModContainer> optional = FabricLoader.getInstance().getModContainer(ServerCore.MODID);
        return optional.map(container -> container.getMetadata().getVersion().getFriendlyString()).orElse("Unknown");
    }
}
