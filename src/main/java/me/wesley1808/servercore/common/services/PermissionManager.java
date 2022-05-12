package me.wesley1808.servercore.common.services;

import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.world.entity.Entity;

public final class PermissionManager {
    public static final String MODIFY_SETTINGS = "servercore.command.settings";
    public static final String MODIFY_CONFIG = "servercore.command.config";
    public static final String VIEW_STATS = "servercore.command.statistics";


    public static boolean hasPermission(SharedSuggestionProvider source, String perm, int level) {
        return Permissions.check(source, perm, level);
    }

    public static boolean hasPermission(Entity source, String perm) {
        return hasPermission(source.createCommandSourceStack(), perm, 2);
    }
}
