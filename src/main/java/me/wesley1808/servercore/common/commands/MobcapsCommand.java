package me.wesley1808.servercore.common.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import me.wesley1808.servercore.common.config.tables.CommandConfig;
import me.wesley1808.servercore.common.interfaces.compat.ILocalMobCapCalculator;
import me.wesley1808.servercore.common.interfaces.compat.IMobCounts;
import me.wesley1808.servercore.common.services.Formatter;
import me.wesley1808.servercore.common.utils.DynamicManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.LocalMobCapCalculator;
import net.minecraft.world.level.NaturalSpawner;

import static net.minecraft.commands.Commands.literal;

public final class MobcapsCommand {
    private static final LocalMobCapCalculator.MobCounts EMPTY_MOBCOUNTS = new LocalMobCapCalculator.MobCounts();

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        if (CommandConfig.COMMAND_MOBCAPS.get()) {
            dispatcher.register(literal("mobcaps").executes(ctx -> mobcaps(ctx.getSource().getPlayerOrException())));
        }
    }

    private static int mobcaps(ServerPlayer player) {
        StringBuilder builder = new StringBuilder(Formatter.line(
                CommandConfig.MOBCAP_TITLE.get().replace("${mobcap_modifier}", DynamicManager.getModifierAsString()),
                50, true
        ));

        NaturalSpawner.SpawnState state = player.getLevel().getChunkSource().getLastSpawnState();
        if (state != null) {
            for (MobCategory category : MobCategory.values()) {
                builder.append("\n").append(CommandConfig.MOBCAP_CONTENT.get()
                        .replace("${name}", category.getName())
                        .replace("${current}", String.valueOf(getMobcount(player, category, state.localMobCapCalculator)))
                        .replace("${capacity}", String.valueOf(category.getMaxInstancesPerChunk()))
                );
            }
        }

        player.sendSystemMessage(Formatter.parse(builder.toString()));
        return Command.SINGLE_SUCCESS;
    }

    private static int getMobcount(ServerPlayer player, MobCategory category, LocalMobCapCalculator mobcapCalculator) {
        if (mobcapCalculator instanceof ILocalMobCapCalculator calculator) {
            IMobCounts mobCounts = calculator.getMobCounts(player, EMPTY_MOBCOUNTS);
            return mobCounts.getMobcount(category);
        } else {
            LocalMobCapCalculator.MobCounts mobCounts = mobcapCalculator.playerMobCounts.getOrDefault(player, EMPTY_MOBCOUNTS);
            return mobCounts.counts.getOrDefault(category, 0);
        }
    }
}
