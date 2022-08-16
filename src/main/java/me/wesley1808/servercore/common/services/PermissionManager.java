package me.wesley1808.servercore.common.services;

import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.world.entity.Entity;

public final class PermissionManager {
    public static boolean hasPermission(SharedSuggestionProvider source, String perm, int level) {
        return Permissions.check(source, String.format("servercore.%s", perm), level);
    }

    public static boolean hasPermission(Entity source, String perm) {
        return hasPermission(source.createCommandSourceStack(), perm, 2);
    }
}