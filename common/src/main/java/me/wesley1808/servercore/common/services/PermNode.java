package me.wesley1808.servercore.common.services;

import net.minecraft.server.permissions.PermissionSet;

import java.util.function.Predicate;

public record PermNode(String id, Predicate<PermissionSet> defaultResolver) {
}