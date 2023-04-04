package me.wesley1808.servercore.fabric;

import eu.pb4.placeholders.api.TextParserUtils;
import me.lucko.fabric.api.permissions.v0.Permissions;
import me.wesley1808.servercore.common.services.Platform;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

import java.nio.file.Path;
import java.util.Optional;

public class FabricPlatform implements Platform {
    private final String version;
    private final Path configDir;

    public FabricPlatform() {
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

    @Override
    public boolean hasPermission(SharedSuggestionProvider source, String permission, int level) {
        return Permissions.check(source, permission, level);
    }

    @Override
    public Component parseText(String input) {
        return TextParserUtils.formatText(input);
    }

    private String getVersionString() {
        Optional<ModContainer> optional = FabricLoader.getInstance().getModContainer("servercore");
        return optional.map(container -> container.getMetadata().getVersion().getFriendlyString()).orElse("Unknown");
    }
}
