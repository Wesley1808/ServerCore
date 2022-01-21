package me.wesley1808.servercore.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.wesley1808.servercore.config.tables.CommandConfig;
import me.wesley1808.servercore.utils.TickManager;
import net.fabricmc.loader.api.FabricLoader;
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
            dispatcher.register(literal("mobcaps").executes(MobcapsCommand::mobcaps));
        }
    }

    private static int mobcaps(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        TextComponent text = new TextComponent(CommandConfig.MOBCAP_TITLE.get().replace("%MODIFIER%", TickManager.getModifierAsString()));
        NaturalSpawner.SpawnState state = player.getLevel().getChunkSource().getLastSpawnState();

        if (state != null) {
            LocalMobCapCalculator.MobCounts mobCounts = state.localMobCapCalculator.playerMobCounts.computeIfAbsent(player, p -> new LocalMobCapCalculator.MobCounts());
            for (MobCategory category : MobCategory.values()) {
                text.append("\n").append(new TextComponent(CommandConfig.MOBCAP_CONTENT.get()
                        .replace("%NAME%", category.getName())
                        .replace("%CURRENT%", String.valueOf(mobCounts.counts.getOrDefault(category, 0)))
                        .replace("%CAPACITY%", String.valueOf(category.getMaxInstancesPerChunk()))
                ));
            }
        }

        player.displayClientMessage(text, false);
        return Command.SINGLE_SUCCESS;
    }
}
