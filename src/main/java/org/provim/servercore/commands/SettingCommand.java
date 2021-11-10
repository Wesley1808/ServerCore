package org.provim.servercore.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TextComponent;
import org.provim.servercore.ServerCore;
import org.provim.servercore.config.Config;
import org.provim.servercore.config.ConfigEntry;
import org.provim.servercore.utils.PermissionUtils;
import org.provim.servercore.utils.TickManager;

import java.math.BigDecimal;
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

public final class SettingCommand {
    private static final String VALUE = "value";

    private SettingCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        final LiteralArgumentBuilder<CommandSourceStack> builder = literal("servercore").requires(src -> PermissionUtils.perm(src, PermissionUtils.COMMAND_SETTINGS, 2));
        registerConfigs(builder);
        registerSettings(builder);

        builder.then(literal("reload").executes(SettingCommand::reload));
        builder.then(literal("save").executes(SettingCommand::save));
        dispatcher.register(builder);
    }

    private static void registerConfigs(LiteralArgumentBuilder<CommandSourceStack> builder) {
        for (Config.Table table : Config.TABLES) {
            final LiteralArgumentBuilder<CommandSourceStack> child = literal(table.key());
            try {
                Config.forEachEntry(table.clazz(), (field, entry) -> {
                    final String key = field.getName().toLowerCase();
                    final Type type = getTypeFor(key, entry);
                    child.then(literal(key)
                            .executes(ctx -> sendInfo(ctx.getSource(), key, entry))
                            .then(argument(VALUE, type.argumentType)
                                    .executes(type.function::apply)
                                    .suggests((ctx, suggestionsBuilder) -> suggestionsBuilder.suggest(asString(entry.get())).suggest(asString(entry.getDefault())).buildFuture()))
                    );
                });
            } catch (Exception ex) {
                ServerCore.getLogger().error("Exception thrown whilst registering commands!", ex);
            }

            builder.then(child);
        }
    }

    private static void registerSettings(LiteralArgumentBuilder<CommandSourceStack> builder) {
        final LiteralArgumentBuilder<CommandSourceStack> settings = literal("settings");
        settings.then(literal("chunk_tick_distance").then(argument(VALUE, integer(2, 32)).executes(ctx -> modifyInt(ctx.getSource(), getInteger(ctx, VALUE), 1, "Chunk-tick distance"))));
        settings.then(literal("view_distance").then(argument(VALUE, integer(2, 32)).executes(ctx -> modifyInt(ctx.getSource(), getInteger(ctx, VALUE), 2, "View distance"))));
        settings.then(literal("simulation_distance").then(argument(VALUE, integer(2, 32)).executes(ctx -> modifyInt(ctx.getSource(), getInteger(ctx, VALUE), 3, "Simulation distance"))));
        settings.then(literal("mobcaps").then(argument(VALUE, doubleArg(0.1, 1.0)).executes(ctx -> modifyDouble(ctx.getSource(), getDouble(ctx, VALUE), 1, "Mobcap multiplier"))));
        builder.then(settings);
    }

    private static <T> int sendInfo(CommandSourceStack source, String key, ConfigEntry<T> entry) {
        final StringBuilder builder = new StringBuilder("§7");
        if (entry.getComment() != null) {
            builder.append(entry.getComment());
        } else {
            builder.append(key);
        }

        builder.append("\n§3Current value: §a").append(asString(entry.get()));
        builder.append("\n§3Default value: §a").append(asString(entry.getDefault()));
        builder.append("\n§3Type: §a").append(entry.getType().getSimpleName());
        source.sendSuccess(new TextComponent(builder.toString()), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int modifyBoolean(String key, ConfigEntry<Boolean> entry, boolean value, CommandSourceStack source) {
        if (entry.set(value)) {
            source.sendSuccess(new TextComponent(String.format("§a%s §3has been set to §a%b", key, value)), false);
        } else {
            source.sendFailure(new TextComponent(String.format("%s cannot be set to %b!", key, value)));
        }

        return Command.SINGLE_SUCCESS;
    }

    private static int modifyInt(String key, ConfigEntry<Integer> entry, int value, CommandSourceStack source) {
        if (entry.set(value)) {
            source.sendSuccess(new TextComponent(String.format("§a%s §3has been set to §a%d", key, value)), false);
        } else {
            source.sendFailure(new TextComponent(String.format("%s cannot be set to %d!", key, value)));
        }

        return Command.SINGLE_SUCCESS;
    }

    private static int modifyDouble(String key, ConfigEntry<Double> entry, double value, CommandSourceStack source) {
        if (entry.set(value)) {
            source.sendSuccess(new TextComponent(String.format("§a%s §3has been set to §a%.1f", key, value)), false);
        } else {
            source.sendFailure(new TextComponent(String.format("%s cannot be set to %.1f!", key, value)));
        }

        return Command.SINGLE_SUCCESS;
    }

    private static int modifyString(String key, ConfigEntry<String> entry, String value, CommandSourceStack source) {
        value = formatString(value);
        if (entry.set(value)) {
            source.sendSuccess(new TextComponent(String.format("§a%s §3has been set to §a%s", key, value)), false);
        } else {
            source.sendFailure(new TextComponent(String.format("%s cannot be set to %s!", key, value)));
        }

        return Command.SINGLE_SUCCESS;
    }

    private static int modifyInt(CommandSourceStack source, int value, int id, String setting) {
        switch (id) {
            case 1 -> TickManager.setChunkTickDistance(value);
            case 2 -> TickManager.setViewDistance(value);
            case 3 -> TickManager.setSimulationDistance(value);
        }

        source.sendSuccess(new TextComponent(String.format("§a%s §3has been set to §a%d", setting, value)), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int modifyDouble(CommandSourceStack source, double value, int id, String setting) {
        switch (id) {
            case 1 -> TickManager.setModifier(BigDecimal.valueOf(value));
        }

        source.sendSuccess(new TextComponent(String.format("§a%s §3has been set to §a%.1f", setting, value)), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int reload(CommandContext<CommandSourceStack> context) {
        Config.load();
        context.getSource().sendSuccess(new TextComponent("Config reloaded!").withStyle(ChatFormatting.GREEN), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int save(CommandContext<CommandSourceStack> context) {
        Config.save();
        context.getSource().sendSuccess(new TextComponent("Config saved!").withStyle(ChatFormatting.GREEN), false);
        return Command.SINGLE_SUCCESS;
    }

    private static Type getTypeFor(String key, ConfigEntry entry) {
        return switch (entry.getType().getSimpleName()) {
            case "Boolean" -> new Type(bool(), ctx -> modifyBoolean(key, entry, getBool(ctx, VALUE), ctx.getSource()));
            case "Integer" -> new Type(longArg(Integer.MIN_VALUE, Integer.MAX_VALUE), ctx -> modifyInt(key, entry, (int) getLong(ctx, VALUE), ctx.getSource()));
            case "Double" -> new Type(doubleArg(), ctx -> modifyDouble(key, entry, getDouble(ctx, VALUE), ctx.getSource()));
            case "String" -> new Type(greedyString(), ctx -> modifyString(key, entry, getString(ctx, VALUE), ctx.getSource()));
            default -> throw new IllegalStateException("Unsupported type: " + entry.getType().getSimpleName());
        };
    }

    private static String asString(Object obj) {
        if (obj instanceof String string) {
            return string.replace("§", "&");
        }

        return String.valueOf(obj);
    }

    private static String formatString(String string) {
        var builder = new StringBuilder(string);
        for (int index = builder.indexOf("&"); index >= 0; index = builder.indexOf("&", index + 1)) {
            if (matches(builder.charAt(index + 1))) {
                builder.setCharAt(index, '§');
            }
        }
        return builder.toString();
    }

    private static boolean matches(Character c) {
        return "b0931825467adcfelmnor".contains(c.toString());
    }

    private static record Type(
            ArgumentType<?> argumentType,
            Function<CommandContext<CommandSourceStack>, Integer> function
    ) {
    }
}