package me.wesley1808.servercore.forge.common;

import me.wesley1808.servercore.common.ServerCore;
import me.wesley1808.servercore.common.services.platform.ModPlatform;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.LoadingModList;

import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

public class ForgeModPlatform implements ModPlatform {
    private final Path configDir;
    private String version;

    public ForgeModPlatform() {
        this.configDir = FMLPaths.CONFIGDIR.get();
    }

    @Override
    public boolean isClient() {
        return FMLEnvironment.dist.isClient();
    }

    @Override
    public boolean isModLoaded(String modId) {
        ModList modList = ModList.get();
        if (modList != null) {
            return modList.isLoaded(modId);
        }

        LoadingModList loadingModList = LoadingModList.get();
        return loadingModList != null && loadingModList.getModFileById(modId) != null;
    }

    @Override
    public Path getConfigDir() {
        return this.configDir;
    }

    @Override
    public String getVersion() {
        if (this.version == null) {
            ModList modList = ModList.get();
            Optional<? extends ModContainer> optional = modList != null ? modList.getModContainerById(ServerCore.MODID) : Optional.empty();
            this.version = optional.map(container -> container.getModInfo().getVersion().getQualifier()).orElse(null);
        }
        return Objects.requireNonNullElse(this.version, "Unknown");
    }
}
