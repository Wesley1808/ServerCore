package me.wesley1808.servercore.fabric.common;

import eu.pb4.placeholders.api.TextParserUtils;
import me.lucko.fabric.api.permissions.v0.Permissions;
import me.wesley1808.servercore.common.ServerCore;
import me.wesley1808.servercore.common.services.platform.MinecraftPlatform;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.Nullable;

public class FabricMinecraftPlatform implements MinecraftPlatform {
    @Override
    public boolean hasPermission(CommandSourceStack source, String node, int level) {
        String permission = String.format("%s.%s", ServerCore.MODID, node);
        return Permissions.check(source, permission, level);
    }

    @Override
    public boolean shouldForceChunkTicks(ChunkMap chunkMap, ChunkPos pos) {
        return false;
    }

    @Override
    public Component parseText(String input) {
        return TextParserUtils.formatText(input);
    }

    @Override
    @Nullable
    public EntityType<?> getEntityType(String key) {
        return null;
    }
}
