package me.wesley1808.servercore.common.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.wesley1808.servercore.common.config.Config;
import me.wesley1808.servercore.common.config.legacy.CommandConfig;
import me.wesley1808.servercore.common.dynamic.DynamicManager;
import me.wesley1808.servercore.common.dynamic.DynamicSetting;
import me.wesley1808.servercore.common.services.Formatter;
import me.wesley1808.servercore.common.services.Permission;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class ServerCoreCommand {
    private static final String VALUE = "value";

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        var node = literal("servercore");

        node.then(reloadConfig());
        node.then(settings());
        if (CommandConfig.COMMAND_STATUS.get()) {
            node.then(literal("status").executes(ctx -> getStatus(ctx.getSource())));
        }

        dispatcher.register(node);
        dispatcher.register(literal("sc").redirect(node.build()));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> reloadConfig() {
        return literal("reload")
                .requires(Permission.require("command.config", 2))
                .executes(ctx -> reload(ctx.getSource()));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> settings() {
        var settings = literal("settings").requires(Permission.require("command.settings", 2));
        settings.then(literal("chunk_tick_distance").then(argument(VALUE, integer(2, 128)).executes(ctx -> modifyDynamic(ctx.getSource(), getInteger(ctx, VALUE), 1, "Chunk-tick distance", false))));
        settings.then(literal("view_distance").then(argument(VALUE, integer(2, 128)).executes(ctx -> modifyDynamic(ctx.getSource(), getInteger(ctx, VALUE), 2, "View distance", false))));
        settings.then(literal("simulation_distance").then(argument(VALUE, integer(2, 128)).executes(ctx -> modifyDynamic(ctx.getSource(), getInteger(ctx, VALUE), 3, "Simulation distance", false))));
        settings.then(literal("mobcaps").then(argument(VALUE, integer(1, 1000)).executes(ctx -> modifyDynamic(ctx.getSource(), getInteger(ctx, VALUE), 4, "Mobcaps", true))));
        return settings;
    }

    private static int modifyDynamic(CommandSourceStack source, int value, int id, String setting, boolean percentage) {
        DynamicManager manager = DynamicManager.getInstance(source.getServer());
        switch (id) {
            case 1 -> DynamicSetting.CHUNK_TICK_DISTANCE.set(value, manager);
            case 2 -> DynamicSetting.VIEW_DISTANCE.set(value, manager);
            case 3 -> DynamicSetting.SIMULATION_DISTANCE.set(value, manager);
            case 4 -> DynamicSetting.MOBCAP.set(value / 100D, manager);
        }

        sendMessage(source, setting, value + (percentage ? "%" : ""), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int reload(CommandSourceStack source) {
        Config.reload(true);

        source.sendSuccess(() -> Component.literal("Config reloaded!").withStyle(ChatFormatting.GREEN), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int getStatus(CommandSourceStack source) {
        source.sendSuccess(() -> Formatter.parse(DynamicManager.createStatusReport(Formatter.line(CommandConfig.STATUS_TITLE.get(), 40, source.isPlayer()))), false);
        return Command.SINGLE_SUCCESS;
    }

    private static void sendMessage(CommandSourceStack source, String key, String value, boolean success) {
        if (success) {
            source.sendSuccess(() -> Formatter.parse(String.format("<green>%s <dark_aqua>has been set to <green>%s", key, value)), false);
        } else {
            source.sendFailure(Component.literal(String.format("%s cannot be set to %s!", key, value)));
        }
    }
}