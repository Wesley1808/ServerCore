package me.wesley1808.servercore.neoforge.common;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.wesley1808.servercore.common.ServerCore;
import me.wesley1808.servercore.common.services.Permission;
import net.neoforged.neoforge.server.permission.events.PermissionGatherEvent;
import net.neoforged.neoforge.server.permission.nodes.PermissionNode;
import net.neoforged.neoforge.server.permission.nodes.PermissionTypes;

import java.util.Map;

public class NeoForgePermissions {
    private static final Map<String, PermissionNode<Boolean>> PERMISSIONS = NeoForgePermissions.build();

    public static PermissionNode<Boolean> getPermissionNode(String node) {
        return PERMISSIONS.get(node);
    }

    public static void addNodes(PermissionGatherEvent.Nodes event) {
        for (PermissionNode<Boolean> node : PERMISSIONS.values()) {
            event.addNodes(node);
        }
    }

    private static Map<String, PermissionNode<Boolean>> build() {
        Map<String, PermissionNode<Boolean>> permissions = new Object2ObjectOpenHashMap<>();
        Permission.visitPermissions(node -> {
            permissions.put(node.id(), new PermissionNode<>(ServerCore.MODID, node.id(), PermissionTypes.BOOLEAN, (player, uuid, context) -> {
                return player != null && node.defaultResolver().test(player.permissions());
            }));
        });
        return permissions;
    }
}
