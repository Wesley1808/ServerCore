package org.provim.servercore.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import org.provim.servercore.config.Config;
import org.provim.servercore.utils.PermissionUtils;
import org.provim.servercore.utils.TickUtils;

import java.math.BigDecimal;

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
        dispatcher.register(literal("servercore").requires(src -> PermissionUtils.perm(src, PermissionUtils.COMMAND_SETTINGS, 2))
                .then(literal("dynamic_performance").then(argument(VALUE, bool()).executes(context -> modifyBoolean(context.getSource(), getBool(context, VALUE), 1, "Dynamic performance checks have"))))
                .then(literal("lobotomize_villagers").then(argument(VALUE, bool()).executes(context -> modifyBoolean(context.getSource(), getBool(context, VALUE), 2, "Villager lobotomizing has"))))
                .then(literal("entity_limits").then(argument(VALUE, bool()).executes(context -> modifyBoolean(context.getSource(), getBool(context, VALUE), 3, "Entity limits have"))))
                .then(literal("fast_xp_merging").then(argument(VALUE, bool()).executes(context -> modifyBoolean(context.getSource(), getBool(context, VALUE), 4, "Fast XP merging has"))))
                .then(literal("chunk_tick_distance").then(argument(VALUE, integer(2, 32)).executes(context -> modifyInt(context.getSource(), getInteger(context, VALUE), 1, "Chunk-tick distance has"))))
                .then(literal("view_distance").then(argument(VALUE, integer(2, 32)).executes(context -> modifyInt(context.getSource(), getInteger(context, VALUE), 2, "View distance has"))))
                .then(literal("simulation_distance").then(argument(VALUE, integer(2, 32)).executes(context -> modifyInt(context.getSource(), getInteger(context, VALUE), 3, "Simulation distance has"))))
                .then(literal("item_merge_radius").then(argument(VALUE, doubleArg(0.5, 10)).executes(context -> modifyDouble(context.getSource(), getDouble(context, VALUE), 1, "Item merge radius has"))))
                .then(literal("xp_merge_radius").then(argument(VALUE, doubleArg(0.5, 10)).executes(context -> modifyDouble(context.getSource(), getDouble(context, VALUE), 2, "XP merge radius has"))))
                .then(literal("mobcaps").then(argument(VALUE, doubleArg(0.1, 10.0)).executes(context -> modifyDouble(context.getSource(), getDouble(context, VALUE), 3, "Mobcaps have"))))
                .then(literal("reload").executes(SettingCommand::reload))
                .then(literal("save").executes(SettingCommand::save))
        );
    }

    private static int modifyBoolean(ServerCommandSource source, boolean value, int id, String setting) {
        switch (id) {
            case 1 -> Config.DYNAMIC_CONFIG.enabled.set(value);
            case 2 -> Config.FEATURE_CONFIG.lobotomizeVillagers.set(value);
            case 3 -> Config.ENTITY_CONFIG.enabled.set(value);
            case 4 -> Config.FEATURE_CONFIG.fastXpMerging.set(value);
        }

        source.sendFeedback(new LiteralText(String.format("%s been set to %b", setting, value)), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int modifyInt(ServerCommandSource source, int value, int id, String type) {
        switch (id) {
            case 1 -> TickUtils.setChunkTickDistance(value);
            case 2 -> TickUtils.setViewDistance(value);
            case 3 -> TickUtils.setSimulationDistance(value);
        }

        source.sendFeedback(new LiteralText(String.format("%s been set to %d", type, value)), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int modifyDouble(ServerCommandSource source, double value, int id, String type) {
        switch (id) {
            case 1 -> Config.FEATURE_CONFIG.itemMergeRadius.set(value);
            case 2 -> Config.FEATURE_CONFIG.xpMergeRadius.set(value);
            case 3 -> TickUtils.setModifier(BigDecimal.valueOf(value));
        }

        source.sendFeedback(new LiteralText(String.format("%s been set to %.1f", type, value)), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int reload(CommandContext<ServerCommandSource> context) {
        Config.load();
        context.getSource().sendFeedback(new LiteralText("Config reloaded!").formatted(Formatting.GREEN), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int save(CommandContext<ServerCommandSource> context) {
        Config.save(true);
        context.getSource().sendFeedback(new LiteralText("Config saved!").formatted(Formatting.GREEN), false);
        return Command.SINGLE_SUCCESS;
    }
}