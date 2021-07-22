package org.provim.servertools.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import org.provim.servertools.config.Config;
import org.provim.servertools.config.ConfigHandler;

import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static com.mojang.brigadier.arguments.BoolArgumentType.getBool;
import static com.mojang.brigadier.arguments.DoubleArgumentType.doubleArg;
import static com.mojang.brigadier.arguments.DoubleArgumentType.getDouble;
import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public final class SettingCommand {
    private static final String VALUE = "value";

    private SettingCommand() {
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("setting")
                .requires(src -> src.hasPermissionLevel(2))
                .then(literal("useTickDistance").then(argument(VALUE, bool()).executes(SettingCommand::setUseTickDistance)))
                .then(literal("useEntityLimits").then(argument(VALUE, bool()).executes(SettingCommand::setUseEntityLimits)))
                .then(literal("tickDistance").then(argument(VALUE, integer(1, 32)).executes(SettingCommand::setTickDistance)))
                .then(literal("viewDistance").then(argument(VALUE, integer(2, 32)).executes(SettingCommand::setViewDistance)))
                .then(literal("mobcaps").then(argument(VALUE, doubleArg(0.1, 10.0)).executes(SettingCommand::setMobcapModifier)))
                .then(literal("reload").executes(SettingCommand::reload))
                .then(literal("save").executes(SettingCommand::save))
        );
    }

    private static int setTickDistance(CommandContext<ServerCommandSource> context) {
        var value = getInteger(context, VALUE);
        Config.instance().tickDistance = value;
        context.getSource().sendFeedback(new LiteralText("Tick distance has been set to " + value).formatted(Formatting.WHITE), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int setViewDistance(CommandContext<ServerCommandSource> context) {
        var value = getInteger(context, VALUE);
        context.getSource().getServer().getPlayerManager().setViewDistance(value);
        context.getSource().sendFeedback(new LiteralText("View distance has been set to " + value).formatted(Formatting.WHITE), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int setMobcapModifier(CommandContext<ServerCommandSource> context) {
        var value = getDouble(context, VALUE);
        Config.instance().mobcapModifier = value;
        context.getSource().sendFeedback(new LiteralText("Mobcaps have been set to " + value).formatted(Formatting.WHITE), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int setUseTickDistance(CommandContext<ServerCommandSource> context) {
        var value = getBool(context, VALUE);
        Config.instance().useTickDistance = value;
        context.getSource().sendFeedback(new LiteralText("No-tick has been set to " + value).formatted(Formatting.WHITE), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int setUseEntityLimits(CommandContext<ServerCommandSource> context) {
        var value = getBool(context, VALUE);
        Config.instance().useEntityLimits = value;
        context.getSource().sendFeedback(new LiteralText("Entity limits have been set to " + value).formatted(Formatting.WHITE), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int save(CommandContext<ServerCommandSource> context) {
        ConfigHandler.save();
        context.getSource().sendFeedback(new LiteralText("Config saved!").formatted(Formatting.GREEN), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int reload(CommandContext<ServerCommandSource> context) {
        ConfigHandler.load();
        context.getSource().sendFeedback(new LiteralText("Config reloaded!").formatted(Formatting.GREEN), false);
        return Command.SINGLE_SUCCESS;
    }
}