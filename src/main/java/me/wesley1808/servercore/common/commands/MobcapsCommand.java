package me.wesley1808.servercore.common.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import me.wesley1808.servercore.common.config.tables.CommandConfig;
import me.wesley1808.servercore.common.services.Formatter;
import me.wesley1808.servercore.common.utils.DynamicManager;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.LocalMobCapCalculator;
import net.minecraft.world.level.NaturalSpawner;

import static net.minecraft.commands.Commands.literal;

public final class MobcapsCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        if (CommandConfig.COMMAND_MOBCAPS.get() && !FabricLoader.getInstance().isModLoaded("vmp")) {
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
            LocalMobCapCalculator.MobCounts mobCounts = state.localMobCapCalculator.playerMobCounts.computeIfAbsent(player, p -> new LocalMobCapCalculator.MobCounts());
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
