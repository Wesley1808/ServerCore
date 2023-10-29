package me.wesley1808.servercore.common.services.platform;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;

public interface MinecraftPlatform {
    Component parseText(String input);

    boolean hasPermission(CommandSourceStack source, String node, int level);

    @Nullable
    EntityType<?> getEntityType(String key);
}
