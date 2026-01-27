package me.wesley1808.servercore.common.services;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.wesley1808.servercore.common.services.platform.PlatformHelper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.permissions.PermissionSet;
import net.minecraft.server.permissions.Permissions;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class Permission {
    private static final ObjectArrayList<PermNode> PERMISSIONS = new ObjectArrayList<>();
    public static final PermNode COMMAND_ROOT = register("command.root", user -> true);
    public static final PermNode COMMAND_MOBCAPS = register("command.mobcaps", user -> true);
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
        return source -> check(source, node);
    }

    private static PermNode register(String node, Predicate<PermissionSet> defaultResolver) {
        PermNode perm = new PermNode(node, defaultResolver);
        PERMISSIONS.add(perm);
        return perm;
    }
}
