package me.wesley1808.servercore.common.services.platform;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface MinecraftPlatform {
    boolean hasPermission(CommandSourceStack source, String node, int level);

    Component parseText(MinecraftServer server, String input);

    @Nullable
    default EntityType<?> getEntityType(String key) {
        return null;
    }

    @NotNull
    default Optional<ResourceKey<EntityType<?>>> getEntityTypeKey(EntityType<?> type) {
        return Optional.empty();
    }
}
