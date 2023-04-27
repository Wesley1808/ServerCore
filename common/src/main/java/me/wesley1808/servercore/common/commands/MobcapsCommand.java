package me.wesley1808.servercore.common.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import me.wesley1808.servercore.common.config.tables.CommandConfig;
import me.wesley1808.servercore.common.dynamic.DynamicManager;
import me.wesley1808.servercore.common.services.Formatter;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.LocalMobCapCalculator;
import net.minecraft.world.level.NaturalSpawner;

import static net.minecraft.commands.Commands.literal;

public class MobcapsCommand {
    private static final LocalMobCapCalculator.MobCounts EMPTY_MOBCOUNTS = new LocalMobCapCalculator.MobCounts();

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        if (CommandConfig.COMMAND_MOBCAPS.get()) {
            dispatcher.register(literal("mobcaps").executes(ctx -> mobcaps(ctx.getSource().getPlayerOrException())));
        }
    }

    private static int mobcaps(ServerPlayer player) {
        StringBuilder builder = new StringBuilder(Formatter.line(
                CommandConfig.MOBCAP_TITLE.get().replace("${mobcap_percentage}", DynamicManager.getModifierAsPercentage()),
                50, true
        ));

        NaturalSpawner.SpawnState state = player.serverLevel().getChunkSource().getLastSpawnState();
        if (state != null) {
            LocalMobCapCalculator.MobCounts mobCounts = state.localMobCapCalculator.playerMobCounts.getOrDefault(player, EMPTY_MOBCOUNTS);
            for (MobCategory category : MobCategory.values()) {
                builder.append("\n").append(CommandConfig.MOBCAP_CONTENT.get()
                        .replace("${name}", category.getName())
                        .replace("${current}", String.valueOf(mobCounts.counts.getOrDefault(category, 0)))
                        .replace("${capacity}", String.valueOf(category.getMaxInstancesPerChunk()))
                );
            }
        }

        player.sendSystemMessage(Formatter.parse(builder.toString()));
        return Command.SINGLE_SUCCESS;
    }
}
