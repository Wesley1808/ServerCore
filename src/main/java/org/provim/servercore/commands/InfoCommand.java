package org.provim.servercore.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import org.provim.servercore.config.tables.FeatureConfig;
import org.provim.servercore.interfaces.IServerPlayerEntity;
import org.provim.servercore.mixin.accessor.SpawnHelperAccessor;
import org.provim.servercore.utils.PermissionUtils;
import org.provim.servercore.utils.TickManager;

import static net.minecraft.server.command.CommandManager.literal;

public final class InfoCommand {

    private InfoCommand() {
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
        dispatcher.register(literal("mobcaps").executes(InfoCommand::mobcaps));

        if (dedicated) {
            dispatcher.register(literal("sc").requires(src -> PermissionUtils.perm(src, PermissionUtils.COMMAND_INFO, 2))
                    .then(literal("status").executes(InfoCommand::performanceReport))
            );
        }
    }

    private static int mobcaps(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();
        LiteralText text = new LiteralText("");
        if (FeatureConfig.PER_PLAYER_SPAWNS.get()) {
            text.append(String.format("§3Per Player Mobcaps (§a%.1f§3)", TickManager.getModifier()));
            IServerPlayerEntity playerInf = (IServerPlayerEntity) player;
            for (SpawnGroup group : SpawnGroup.values()) {
                text.append(new LiteralText(String.format("\n§3%s: §a%d §8/ §a%d", group.getName(), playerInf.getMobCounts()[group.ordinal()], group.getCapacity())));
            }
        } else {
            var info = player.getServerWorld().getChunkManager().getSpawnInfo();
            if (info != null) {
                text.append(String.format("§3Global Mobcaps (§a%.1f§3)", TickManager.getModifier()));
                for (SpawnGroup group : SpawnGroup.values()) {
                    text.append(new LiteralText(String.format("\n§3%s: §a%d §8/ §a%d", group.getName(), info.getGroupToCount().getOrDefault(group, -1), (group.getCapacity() * info.getSpawningChunkCount() / SpawnHelperAccessor.getChunkArea()))));
                }
            }
        }
        player.sendMessage(text, false);
        return Command.SINGLE_SUCCESS;
    }

    private static int performanceReport(CommandContext<ServerCommandSource> context) {
        context.getSource().sendFeedback(TickManager.createStatusReport(), false);
        return Command.SINGLE_SUCCESS;
    }
}
