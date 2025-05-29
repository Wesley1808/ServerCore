package me.wesley1808.servercore.common.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import me.wesley1808.servercore.common.config.Config;
import me.wesley1808.servercore.common.config.data.CommandConfig;
import me.wesley1808.servercore.common.dynamic.DynamicSetting;
import me.wesley1808.servercore.common.services.Formatter;
import me.wesley1808.servercore.common.utils.Mobcaps;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.LocalMobCapCalculator;
import net.minecraft.world.level.NaturalSpawner;

import static net.minecraft.commands.Commands.literal;

public class MobcapsCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        if (Config.get().commands().mobcapsCommandEnabled()) {
            dispatcher.register(literal("mobcaps").executes(ctx -> mobcaps(ctx.getSource(), ctx.getSource().getPlayerOrException())));
        }
    }

    private static int mobcaps(CommandSourceStack source, ServerPlayer player) {
        CommandConfig config = Config.get().commands();
        source.sendSuccess(() -> {
            MutableComponent component = Component.empty();

            Component title = Formatter.parse("<c:#tertiary>Mobcaps <c:#primary>(<c:#tertiary>%s</c>)".formatted(
                    DynamicSetting.MOBCAP_PERCENTAGE.getFormattedValue()
            ), source.getServer());

            Formatter.addLines(component, 16, config.primaryValue(), title);

            NaturalSpawner.SpawnState state = player.level().getChunkSource().getLastSpawnState();
            if (state != null) {
                LocalMobCapCalculator.MobCounts mobCounts = state.localMobCapCalculator.playerMobCounts.getOrDefault(player, Mobcaps.EMPTY_MOBCOUNTS);
                for (MobCategory category : MobCategory.values()) {
                    if (category != MobCategory.MISC) {
                        component.append(Formatter.parse("\n<dark_gray>» <c:#primary>%s:</c> <c:#secondary>%d</c> / <c:#secondary>%d".formatted(
                                category.getName(),
                                mobCounts.counts.getOrDefault(category, 0),
                                category.getMaxInstancesPerChunk()
                        ), source.getServer()));
                    }
                }
            }
            return component;
        }, false);
        return Command.SINGLE_SUCCESS;
    }
}
