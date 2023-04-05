package me.wesley1808.servercore.forge.common;

import me.wesley1808.servercore.common.ServerCore;
import net.minecraftforge.server.permission.events.PermissionGatherEvent;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import net.minecraftforge.server.permission.nodes.PermissionTypes;

import java.util.Map;

public class ForgePermissions {
    private static final Map<String, PermissionNode<Boolean>> PERMISSIONS = Map.of(
            "command.config", node("command.config"),
            "command.settings", node("command.settings"),
            "command.statistics", node("command.statistics")
    );

    public static PermissionNode<Boolean> getPermissionNode(String node) {
        return PERMISSIONS.get(node);
    }

    public static void addNodes(PermissionGatherEvent.Nodes event) {
        for (PermissionNode<Boolean> node : PERMISSIONS.values()) {
            event.addNodes(node);
        }
    }

    private static PermissionNode<Boolean> node(String node) {
        return new PermissionNode<>(ServerCore.MODID, node, PermissionTypes.BOOLEAN, (x, y, z) -> false);
    }
}
