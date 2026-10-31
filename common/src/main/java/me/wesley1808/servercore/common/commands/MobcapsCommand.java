package me.wesley1808.servercore.common.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import me.wesley1808.servercore.common.config.Config;
import me.wesley1808.servercore.common.config.data.CommandConfig;
import me.wesley1808.servercore.common.dynamic.DynamicSetting;
import me.wesley1808.servercore.common.services.Formatter;
import me.wesley1808.servercore.common.services.Permission;
import me.wesley1808.servercore.common.utils.Mobcaps;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.LocalMobCapCalculator;
import net.minecraft.world.level.NaturalSpawner;

import java.util.function.ToIntFunction;

import static net.minecraft.commands.Commands.literal;

public class MobcapsCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        if (Config.get().commands().mobcapsCommandEnabled()) {
            dispatcher.register(literal("mobcaps")
                    .requires(Permission.require(Permission.COMMAND_MOBCAPS))
                    .executes(ctx -> mobcaps(ctx.getSource(), false))
                    .then(literal("global")
                            .executes(ctx -> mobcaps(ctx.getSource(), true))
                    )
            );
        }
    }

    private static int mobcaps(CommandSourceStack source, boolean global) {
        CommandConfig config = Config.get().commands();
        source.sendSuccess(() -> {
            MutableComponent component = Component.empty();

            Component title = Formatter.parse("<c:#tertiary>Mobcaps <c:#primary>(<c:#tertiary>%s</c>)".formatted(
                    DynamicSetting.MOBCAP_PERCENTAGE.getFormattedValue()
            ), source.getServer());

            Formatter.addLines(component, 16, config.primaryValue(), title);

            ServerPlayer player = source.getPlayer();
            if (global || player == null) {
                appendGlobalMobcaps(source, component);
            } else {
                appendPerPlayerMobcaps(player, component);
            }

            return component;
        }, false);

        return Command.SINGLE_SUCCESS;
    }

    private static void appendGlobalMobcaps(CommandSourceStack source, MutableComponent output) {
        NaturalSpawner.SpawnState state = source.getLevel().getChunkSource().getLastSpawnState();
        if (state != null) {
            appendMobcaps(output, source.getServer(),
                    category -> state.getMobCategoryCounts().getInt(category),
                    category -> Mobcaps.toGlobalCapacity(state, category.getMaxInstancesPerChunk())
            );
        }
    }

    private static void appendPerPlayerMobcaps(ServerPlayer player, MutableComponent output) {
        NaturalSpawner.SpawnState state = player.level().getChunkSource().getLastSpawnState();
        if (state != null) {
            LocalMobCapCalculator.MobCounts mobCounts = state.localMobCapCalculator.playerMobCounts.getOrDefault(player, Mobcaps.EMPTY_MOBCOUNTS);
            appendMobcaps(output, player.level().getServer(),
                    category -> mobCounts.counts.getOrDefault(category, 0),
                    MobCategory::getMaxInstancesPerChunk
            );
        }
    }

    private static void appendMobcaps(MutableComponent output, MinecraftServer server, ToIntFunction<MobCategory> count, ToIntFunction<MobCategory> capacity) {
        for (MobCategory category : MobCategory.values()) {
            if (category != MobCategory.MISC) {
                output.append(formatCategory(
                        category,
                        count.applyAsInt(category),
                        capacity.applyAsInt(category),
                        server
                ));
            }
        }
    }

    private static Component formatCategory(MobCategory category, int count, int capacity, MinecraftServer server) {
        return Formatter.parse("\n<dark_gray>» <c:#primary>%s:</c> <c:#secondary>%d</c> / <c:#secondary>%d".formatted(category.getName(), count, capacity), server);
    }
}
