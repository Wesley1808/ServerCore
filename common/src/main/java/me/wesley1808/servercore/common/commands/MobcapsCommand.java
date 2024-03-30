package me.wesley1808.servercore.common.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import me.wesley1808.servercore.common.config.Config;
import me.wesley1808.servercore.common.config.data.CommandConfig;
import me.wesley1808.servercore.common.dynamic.DynamicSetting;
import me.wesley1808.servercore.common.services.Formatter;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.LocalMobCapCalculator;
import net.minecraft.world.level.NaturalSpawner;

import static net.minecraft.commands.Commands.literal;

public class MobcapsCommand {
    private static final LocalMobCapCalculator.MobCounts EMPTY_MOBCOUNTS = new LocalMobCapCalculator.MobCounts();

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        if (Config.get().commands().mobcapsCommandEnabled()) {
            dispatcher.register(literal("mobcaps").executes(ctx -> mobcaps(ctx.getSource(), ctx.getSource().getPlayerOrException())));
        }
    }

    private static int mobcaps(CommandSourceStack source, ServerPlayer player) {
        CommandConfig config = Config.get().commands();
        source.sendSuccess(() -> {
            MutableComponent component = Component.empty();

            Component title = Formatter.parse("<c:%s>Mobcaps <c:%s>(<c:%s>%s</c>)".formatted(
                    config.tertiaryHex(), config.primaryHex(), config.tertiaryHex(),
                    DynamicSetting.MOBCAP_PERCENTAGE.getFormattedValue()
            ));

            Formatter.addLines(component, 16, config.primaryValue(), title);

            NaturalSpawner.SpawnState state = player.serverLevel().getChunkSource().getLastSpawnState();
            if (state != null) {
                LocalMobCapCalculator.MobCounts mobCounts = state.localMobCapCalculator.playerMobCounts.getOrDefault(player, EMPTY_MOBCOUNTS);
                for (MobCategory category : MobCategory.values()) {
                    if (category != MobCategory.MISC) {
                        component.append(Formatter.parse("\n<dark_gray>» <c:%s>%s:</c> <c:%s>%d</c> / <c:%s>%d".formatted(
                                config.primaryHex(), category.getName(),
                                config.secondaryHex(), mobCounts.counts.getOrDefault(category, 0),
                                config.secondaryHex(), category.getMaxInstancesPerChunk()
                        )));
                    }
                }
            }
            return component;
        }, false);
        return Command.SINGLE_SUCCESS;
    }
}
