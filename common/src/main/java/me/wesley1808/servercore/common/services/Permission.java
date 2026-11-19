package me.wesley1808.servercore.common.services;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.wesley1808.servercore.common.services.platform.PlatformHelper;
import net.minecraft.commands.CommandPermissionLevels;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.permissions.PermissionCheck;
import net.minecraft.server.permissions.PermissionSet;
import net.minecraft.server.permissions.Permissions;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class Permission {
    private static final ObjectArrayList<PermNode> PERMISSIONS = new ObjectArrayList<>();
    public static final PermNode COMMAND_ROOT = register("command.root", user -> true);
    public static final PermNode COMMAND_STATUS = register("command.status", user -> true);
    public static final PermNode COMMAND_MOBCAPS = register("command.mobcaps", user -> true);
    public static final PermNode COMMAND_MOBCAPS_GLOBAL = register("command.mobcaps.global", user -> true);
    public static final PermNode COMMAND_CONFIG = register("command.config", user -> user.hasPermission(Permissions.COMMANDS_GAMEMASTER));
    public static final PermNode COMMAND_SETTINGS = register("command.settings", user -> user.hasPermission(Permissions.COMMANDS_GAMEMASTER));
    public static final PermNode COMMAND_STATISTICS = register("command.statistics", user -> user.hasPermission(Permissions.COMMANDS_GAMEMASTER));

    public static void visitPermissions(Consumer<PermNode> visitor) {
        for (PermNode permission : PERMISSIONS) {
            visitor.accept(permission);
        }
    }

    public static boolean check(CommandSourceStack source, PermNode node) {
        return PlatformHelper.hasPermission(source, node);
    }

    public static Predicate<CommandSourceStack> require(PermNode node) {
        // TODO: Fix command permission node checks.
        // Vanilla now requires predicates to be of type PermissionProviderCheck, using their permission system.
        // The vanilla system doesn't seem to properly support permission nodes with operator level fallbacks.
        // LuckPerms also doesn't currently seem to implement the vanilla system on non-fabric platforms.

        // Fabric API provides a solution for this issue. As neoforge's permission api is completely different
        // there is a chance this issue might end up needing to be solved using mixins (preferably not).

        // return source -> check(source, node);

        PermissionCheck check = CommandPermissionLevels.LEVEL_GAMEMASTERS;
        if (node.defaultResolver().test(PermissionSet.NO_PERMISSIONS)) {
            check = CommandPermissionLevels.LEVEL_ALL;
        }

        return Commands.hasPermission(check);
    }

    private static PermNode register(String node, Predicate<PermissionSet> defaultResolver) {
        PermNode perm = new PermNode(node, defaultResolver);
        PERMISSIONS.add(perm);
        return perm;
    }
}
