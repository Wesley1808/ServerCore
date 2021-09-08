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
                .then(literal("dynamic_performance").then(argument(VALUE, bool()).executes(context -> setEnabled(context.getSource(), getBool(context, VALUE), 1, "Dynamic performance checks have"))))
                .then(literal("per_player_spawns").then(argument(VALUE, bool()).executes(context -> setEnabled(context.getSource(), getBool(context, VALUE), 2, "Per player spawns has"))))
                .then(literal("lobotomize_villagers").then(argument(VALUE, bool()).executes(context -> setEnabled(context.getSource(), getBool(context, VALUE), 3, "Villager lobotomizing has"))))
                .then(literal("no_chunk_tick").then(argument(VALUE, bool()).executes(context -> setEnabled(context.getSource(), getBool(context, VALUE), 4, "Chunk-ticking distance has"))))
                .then(literal("entity_limits").then(argument(VALUE, bool()).executes(context -> setEnabled(context.getSource(), getBool(context, VALUE), 5, "Entity limits have"))))
                .then(literal("fast_xp_merging").then(argument(VALUE, bool()).executes(context -> setEnabled(context.getSource(), getBool(context, VALUE), 6, "Fast XP merging has"))))
                .then(literal("chunk_tick_distance").then(argument(VALUE, integer(2, 32)).executes(context -> setChunkDistance(context.getSource(), getInteger(context, VALUE), 1, "Chunk-tick"))))
                .then(literal("view_distance").then(argument(VALUE, integer(2, 32)).executes(context -> setChunkDistance(context.getSource(), getInteger(context, VALUE), 2, "View"))))
                .then(literal("item_merge_radius").then(argument(VALUE, doubleArg(0.5, 10)).executes(context -> setMergeRadius(context.getSource(), getDouble(context, VALUE), 1, "Item"))))
                .then(literal("xp_merge_radius").then(argument(VALUE, doubleArg(0.5, 10)).executes(context -> setMergeRadius(context.getSource(), getDouble(context, VALUE), 2, "XP"))))
                .then(literal("mobcaps").then(argument(VALUE, doubleArg(0.1, 10.0)).executes(context -> setMobcapModifier(context.getSource(), getDouble(context, VALUE)))))
                .then(literal("reload").executes(SettingCommand::reload))
        );
    }

    private static int setEnabled(ServerCommandSource source, boolean value, int id, String setting) {
        switch (id) {
            case 1 -> Config.getDynamicConfig().enabled = value;
            case 2 -> Config.getFeatureConfig().perPlayerSpawns = value;
            case 3 -> Config.getFeatureConfig().lobotomizeVillagers = value;
            case 4 -> Config.getFeatureConfig().noChunkTick = value;
            case 5 -> Config.getEntityConfig().enabled = value;
            case 6 -> Config.getFeatureConfig().fastXpMerging = value;
        }

        source.sendFeedback(new LiteralText(String.format("%s been set to %b", setting, value)), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int setChunkDistance(ServerCommandSource source, int distance, int id, String type) {
        switch (id) {
            case 1 -> TickUtils.setChunkTickDistance(distance);
            case 2 -> TickUtils.setViewDistance(distance);
        }

        source.sendFeedback(new LiteralText(String.format("%s distance has been set to %d", type, distance)), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int setMergeRadius(ServerCommandSource source, double radius, int id, String type) {
        switch (id) {
            case 1 -> Config.getFeatureConfig().itemMergeRadius = radius;
            case 2 -> Config.getFeatureConfig().xpMergeRadius = radius;
        }

        source.sendFeedback(new LiteralText(String.format("%s merge radius has been set to %.1f", type, radius)), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int setMobcapModifier(ServerCommandSource source, double mobcap) {
        TickUtils.setModifier(BigDecimal.valueOf(mobcap));
        source.sendFeedback(new LiteralText("Mobcaps have been set to " + mobcap).formatted(Formatting.WHITE), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int reload(CommandContext<ServerCommandSource> context) {
        Config.load();
        context.getSource().sendFeedback(new LiteralText("Config reloaded!").formatted(Formatting.GREEN), false);
        return Command.SINGLE_SUCCESS;
    }
}