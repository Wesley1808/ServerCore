package me.wesley1808.servercore.common.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.wesley1808.servercore.common.ServerCore;
import me.wesley1808.servercore.common.config.Config;
import me.wesley1808.servercore.common.config.ConfigEntry;
import me.wesley1808.servercore.common.config.tables.CommandConfig;
import me.wesley1808.servercore.common.dynamic.DynamicManager;
import me.wesley1808.servercore.common.dynamic.DynamicSetting;
import me.wesley1808.servercore.common.services.Formatter;
import me.wesley1808.servercore.common.services.Permission;
import me.wesley1808.servercore.common.utils.Util;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import java.util.function.Function;

import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static com.mojang.brigadier.arguments.BoolArgumentType.getBool;
import static com.mojang.brigadier.arguments.DoubleArgumentType.doubleArg;
import static com.mojang.brigadier.arguments.DoubleArgumentType.getDouble;
import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.LongArgumentType.getLong;
import static com.mojang.brigadier.arguments.LongArgumentType.longArg;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class ServerCoreCommand {
    private static final String VALUE = "value";

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        var node = literal("servercore");

        node.then(config());
        node.then(settings());
        if (CommandConfig.COMMAND_STATUS.get()) {
            node.then(literal("status").executes(ctx -> getStatus(ctx.getSource())));
        }

        dispatcher.register(node);
        dispatcher.register(literal("sc").redirect(node.build()));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> config() {
        var config = literal("config").requires(Permission.require("command.config", 2));

        for (Config.Table table : Config.Table.values()) {
            var child = literal(table.key);

            try {
                Config.forEachEntry(table.clazz, (field, entry) -> {
                    String key = field.getName().toLowerCase();
                    Type type = getTypeFor(key, entry);
                    if (type != null) {
                        child.then(literal(key)
                                .executes(ctx -> sendInfo(ctx.getSource(), String.format("%s.%s", table.key, key), entry))
                                .then(argument(VALUE, type.argumentType)
                                        .executes(type.function::apply)
                                        .suggests((ctx, suggestionsBuilder) ->
                                                Util.suggestAll(suggestionsBuilder, asString(entry.get()), asString(entry.getDefault()))
                                        )
                                )
                        );
                    }
                });
            } catch (Exception ex) {
                ServerCore.getLogger().error("Exception thrown whilst registering commands!", ex);
            }

            config.then(child);
        }

        if (Config.isConfigAvailable()) {
            config.then(literal("reload").executes(ctx -> saveOrReload(ctx.getSource(), false)));
            config.then(literal("save").executes(ctx -> saveOrReload(ctx.getSource(), true)));
        }

        return config;
    }

    private static LiteralArgumentBuilder<CommandSourceStack> settings() {
        var settings = literal("settings").requires(Permission.require("command.settings", 2));
        settings.then(literal("chunk_tick_distance").then(argument(VALUE, integer(2, 128)).executes(ctx -> modify(ctx.getSource(), getInteger(ctx, VALUE), 1, "Chunk-tick distance", false))));
        settings.then(literal("view_distance").then(argument(VALUE, integer(2, 128)).executes(ctx -> modify(ctx.getSource(), getInteger(ctx, VALUE), 2, "View distance", false))));
        settings.then(literal("simulation_distance").then(argument(VALUE, integer(2, 128)).executes(ctx -> modify(ctx.getSource(), getInteger(ctx, VALUE), 3, "Simulation distance", false))));
        settings.then(literal("mobcaps").then(argument(VALUE, integer(1, 1000)).executes(ctx -> modify(ctx.getSource(), getInteger(ctx, VALUE), 4, "Mobcaps", true))));
        return settings;
    }

    private static int sendInfo(CommandSourceStack source, String key, ConfigEntry<Object> entry) {
        String message = String.format("<dark_aqua>Key: <green>%s\n<dark_aqua>Current value: <green>%s\n<dark_aqua>Default value: <green>%s\n<dark_aqua>Type: <green>%s",
                key,
                asString(entry.get()),
                asString(entry.getDefault()),
                entry.getType().getSimpleName()
        );

        source.sendSuccess(Formatter.parse(message), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int modify(String key, ConfigEntry<Object> entry, Object value, CommandSourceStack source) {
        if (value instanceof String val) value = val.replace("#N", "\n");

        sendMessage(source, key, String.valueOf(value), entry.set(value));
        return Command.SINGLE_SUCCESS;
    }

    private static int modify(CommandSourceStack source, int value, int id, String setting, boolean percentage) {
        DynamicManager manager = DynamicManager.getInstance(source.getServer());
        switch (id) {
            case 1 -> DynamicSetting.CHUNK_TICK_DISTANCE.set(value, manager);
            case 2 -> DynamicSetting.VIEW_DISTANCE.set(value, manager);
            case 3 -> DynamicSetting.SIMULATION_DISTANCE.set(value, manager);
            case 4 -> DynamicSetting.MOBCAP_MULTIPLIER.set(value / 100D, manager);
        }

        sendMessage(source, setting, value + (percentage ? "%" : ""), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int saveOrReload(CommandSourceStack source, boolean saved) {
        if (saved) Config.save();
        else Config.load();

        source.sendSuccess(Component.literal(saved ? "Config saved!" : "Config reloaded!").withStyle(ChatFormatting.GREEN), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int getStatus(CommandSourceStack source) {
        Component component = Formatter.parse(DynamicManager.createStatusReport(Formatter.line(CommandConfig.STATUS_TITLE.get(), 40, source.isPlayer())));
        source.sendSuccess(component, false);
        return Command.SINGLE_SUCCESS;
    }

    private static void sendMessage(CommandSourceStack source, String key, String value, boolean success) {
        if (success) {
            source.sendSuccess(Formatter.parse(String.format("<green>%s <dark_aqua>has been set to <green>%s", key, value)), false);
        } else {
            source.sendFailure(Component.literal(String.format("%s cannot be set to %s!", key, value)));
        }
    }

    private static Type getTypeFor(String key, ConfigEntry<Object> entry) {
        return switch (entry.getType().getSimpleName()) {
            case "Boolean" -> new Type(bool(), ctx -> modify(key, entry, getBool(ctx, VALUE), ctx.getSource()));
            case "Integer" -> new Type(longArg(Integer.MIN_VALUE, Integer.MAX_VALUE), ctx -> modify(key, entry, (int) getLong(ctx, VALUE), ctx.getSource()));
            case "Double" -> new Type(doubleArg(), ctx -> modify(key, entry, getDouble(ctx, VALUE), ctx.getSource()));
            case "String" -> new Type(greedyString(), ctx -> modify(key, entry, getString(ctx, VALUE), ctx.getSource()));
            default -> null;
        };
    }

    private static String asString(Object obj) {
        return obj instanceof String string ? string.replace("\n", "#N") : String.valueOf(obj);
    }

    private record Type(
            ArgumentType<?> argumentType,
            Function<CommandContext<CommandSourceStack>, Integer> function
    ) {
    }
}