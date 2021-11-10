package org.provim.servercore.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.LocalMobCapCalculator;
import net.minecraft.world.level.NaturalSpawner;
import org.provim.servercore.config.tables.CommandConfig;
import org.provim.servercore.utils.PermissionUtils;
import org.provim.servercore.utils.TickManager;

import static net.minecraft.commands.Commands.literal;

public final class InfoCommand {

    private InfoCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        if (CommandConfig.COMMAND_MOBCAPS.get()) {
            dispatcher.register(literal("mobcaps").executes(InfoCommand::mobcaps));
        }

        dispatcher.register(literal("sc").requires(src -> PermissionUtils.perm(src, PermissionUtils.COMMAND_INFO, 2))
                .then(literal("status").executes(InfoCommand::status))
        );
    }

    private static int mobcaps(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        TextComponent text = new TextComponent(CommandConfig.MOBCAP_TITLE.get().replace("%MODIFIER%", TickManager.getModifierAsString()));

        NaturalSpawner.SpawnState state = player.getLevel().getChunkSource().getLastSpawnState();
        if (state != null) {
            LocalMobCapCalculator.MobCounts mobCounts = state.localMobCapCalculator.playerMobCounts.computeIfAbsent(player, p -> new LocalMobCapCalculator.MobCounts());
            for (MobCategory group : MobCategory.values()) {
                String message = CommandConfig.MOBCAP_SPAWN_GROUP.get()
                        .replace("%NAME%", group.getName())
                        .replace("%CURRENT%", String.valueOf(mobCounts.counts.getOrDefault(group, 0)))
                        .replace("%CAPACITY%", String.valueOf(TickManager.getMobcap(group)));

                text.append("\n").append(new TextComponent(message));
            }
        }

        player.displayClientMessage(text, false);
        return Command.SINGLE_SUCCESS;
    }

    private static int status(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(TickManager.createStatusReport(), false);
        return Command.SINGLE_SUCCESS;
    }
}
