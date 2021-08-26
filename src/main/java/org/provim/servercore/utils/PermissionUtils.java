package org.provim.servercore.utils;

import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public final class PermissionUtils {
    public static final String COMMAND_INFO = "servercore.command.sc";
    public static final String COMMAND_SETTINGS = "servercore.command.servercore";
    private static final boolean LOADED = FabricLoader.getInstance().isModLoaded("fabric-permissions-api-v0");

    private PermissionUtils() {
    }

    /**
     * Decides whether a player or command source can use specific commands.
     *
     * @param src:   The player / command source
     * @param perm:  The permission string
     * @param level: The minimum permission level the player should have
     * @return Boolean: whether the player can use certain commands.
     */

    public static boolean perm(ServerCommandSource src, String perm, int level) {
        return LOADED ? Permissions.check(src, perm, level) : src.hasPermissionLevel(level);
    }

    public static boolean perm(ServerPlayerEntity src, String perm) {
        return LOADED ? Permissions.check(src, perm, 2) : src.hasPermissionLevel(2);
    }

    public static boolean perm(PlayerEntity src, String perm) {
        return LOADED ? Permissions.check(src, perm, 2) : src.hasPermissionLevel(2);
    }
}
