package me.wesley1808.servercore.neoforge.common;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.wesley1808.servercore.common.ServerCore;
import net.neoforged.neoforge.server.permission.events.PermissionGatherEvent;
import net.neoforged.neoforge.server.permission.nodes.PermissionNode;
import net.neoforged.neoforge.server.permission.nodes.PermissionTypes;

import java.util.Map;

public class NeoForgePermissions {
    private static final Map<String, PermissionNode<Boolean>> PERMISSIONS = NeoForgePermissions.build(
            "command.config",
            "command.settings",
            "command.statistics"
    );

    public static PermissionNode<Boolean> getPermissionNode(String node) {
        return PERMISSIONS.get(node);
    }

    public static void addNodes(PermissionGatherEvent.Nodes event) {
        for (PermissionNode<Boolean> node : PERMISSIONS.values()) {
            event.addNodes(node);
        }
    }

    private static Map<String, PermissionNode<Boolean>> build(String... nodes) {
        Map<String, PermissionNode<Boolean>> permissions = new Object2ObjectOpenHashMap<>();
        for (String node : nodes) {
            permissions.put(node, new PermissionNode<>(ServerCore.MODID, node, PermissionTypes.BOOLEAN, (x, y, z) -> false));
        }
        return permissions;
    }
}
