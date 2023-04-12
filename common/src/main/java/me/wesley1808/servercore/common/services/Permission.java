package me.wesley1808.servercore.common.services;

import me.wesley1808.servercore.common.services.platform.PlatformHelper;
import net.minecraft.commands.CommandSourceStack;

import java.util.function.Predicate;

public class Permission {

    public static boolean check(CommandSourceStack source, String node, int level) {
        return PlatformHelper.hasPermission(source, node, level);
    }

    public static Predicate<CommandSourceStack> require(String node, int level) {
        return source -> check(source, node, level);
    }
}