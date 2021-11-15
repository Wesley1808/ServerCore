package org.provim.servercore.utils;

import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.player.Player;

public final class PermissionManager {
    public static final String COMMAND_INFO = "servercore.command.sc";
    public static final String COMMAND_SETTINGS = "servercore.command.servercore";
    private static final boolean LOADED = FabricLoader.getInstance().isModLoaded("fabric-permissions-api-v0");

    /**
     * Decides whether a player or command source can use specific commands.
     *
     * @param src:   The player / command source
     * @param perm:  The permission string
     * @param level: The minimum permission level the player should have
     * @return Boolean: whether the player can use certain commands.
     */

    public static boolean perm(CommandSourceStack src, String perm, int level) {
        return LOADED ? Permissions.check(src, perm, level) : src.hasPermission(level);
    }

    public static boolean perm(Player src, String perm) {
        return LOADED ? Permissions.check(src, perm, 2) : src.hasPermissions(2);
    }
}
