package me.wesley1808.servercore.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import me.wesley1808.servercore.config.tables.CommandConfig;
import me.wesley1808.servercore.utils.TickManager;
import me.wesley1808.servercore.utils.Util;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TextComponent;
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
        StringBuilder builder = new StringBuilder(Util.createHeader(String.format("§bMobcaps §3(§b%s§3)", TickManager.getModifierAsString()), 56, ChatFormatting.DARK_AQUA));

        NaturalSpawner.SpawnState state = player.getLevel().getChunkSource().getLastSpawnState();
        if (state != null) {
            LocalMobCapCalculator.MobCounts mobCounts = state.localMobCapCalculator.playerMobCounts.computeIfAbsent(player, p -> new LocalMobCapCalculator.MobCounts());
            for (MobCategory category : MobCategory.values()) {
                builder.append(String.format("\n§8» §3%s: §a%d §8/ §a%d", category.getName(), mobCounts.counts.getOrDefault(category, 0), category.getMaxInstancesPerChunk()));
            }
        }

        player.displayClientMessage(new TextComponent(builder.toString()), false);
        return Command.SINGLE_SUCCESS;
    }
}
