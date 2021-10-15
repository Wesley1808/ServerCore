package org.provim.servercore.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.world.SpawnDensityCapper;
import org.provim.servercore.config.tables.CommandConfig;
import org.provim.servercore.mixin.accessor.SpawnHelperInfoAccessor;
import org.provim.servercore.utils.PermissionUtils;
import org.provim.servercore.utils.TickManager;

import static net.minecraft.server.command.CommandManager.literal;

public final class InfoCommand {

    private InfoCommand() {
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        if (CommandConfig.COMMAND_MOBCAPS.get()) {
            dispatcher.register(literal("mobcaps").executes(InfoCommand::mobcaps));
        }

        dispatcher.register(literal("sc").requires(src -> PermissionUtils.perm(src, PermissionUtils.COMMAND_INFO, 2))
                .then(literal("tps").executes(InfoCommand::performanceReport))
        );
    }

    private static int mobcaps(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();
        LiteralText text = new LiteralText(String.format(CommandConfig.MOBCAP_TITLE.get(), TickManager.getModifier()));

        SpawnHelperInfoAccessor info = (SpawnHelperInfoAccessor) player.getWorld().getChunkManager().getSpawnInfo();
        if (info != null) {
            SpawnDensityCapper.DensityCap densityCap = info.getDensityCapper().playersToDensityCap.computeIfAbsent(player, p -> new SpawnDensityCapper.DensityCap());
            for (SpawnGroup group : SpawnGroup.values()) {
                text.append("\n").append(new LiteralText(String.format(CommandConfig.MOBCAP_SPAWN_GROUP.get(), group.getName(), densityCap.spawnGroupsToDensity.getOrDefault(group, 0), TickManager.getMobcap(group))));
            }
        }

        player.sendMessage(text, false);
        return Command.SINGLE_SUCCESS;
    }

    private static int performanceReport(CommandContext<ServerCommandSource> context) {
        context.getSource().sendFeedback(TickManager.createPerformanceReport(), false);
        return Command.SINGLE_SUCCESS;
    }
}
