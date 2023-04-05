package me.wesley1808.servercore.forge.common;

import me.wesley1808.servercore.common.ServerCore;
import me.wesley1808.servercore.common.services.Platform;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.server.permission.PermissionAPI;
import net.minecraftforge.server.permission.nodes.PermissionNode;

import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

public class ForgePlatform implements Platform {
    private final Path configDir;
    private String version;

    public ForgePlatform() {
        this.configDir = FMLPaths.CONFIGDIR.get();
    }

    @Override
    public boolean isModLoaded(String modId) {
        ModList list = ModList.get();
        return list != null && list.isLoaded(modId);
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

    @Override
    public boolean hasPermission(CommandSourceStack source, String node, int level) {
        if (source.hasPermission(level)) {
            return true;
        }

        ServerPlayer player = source.getPlayer();
        PermissionNode<Boolean> permission = ForgePermissions.getPermissionNode(node);
        if (player == null || permission == null) {
            return false;
        }

        return PermissionAPI.getPermission(player, permission);
    }

    @Override
    public Component parseText(String input) {
        return Component.Serializer.fromJson(GsonComponentSerializer.gson().serialize(MiniMessage.miniMessage().deserialize(input)));
    }
}
