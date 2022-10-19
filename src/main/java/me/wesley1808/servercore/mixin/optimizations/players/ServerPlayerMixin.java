package me.wesley1808.servercore.mixin.optimizations.players;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Based on: Paper (Don-t-move-existing-players-to-world-spawn.patch)
 * Patch Author: Aikar (aikar@aikar.co)
 * License: GPL-3.0 (licenses/GPL.md)
 */

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {

    @WrapWithCondition(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerPlayer;fudgeSpawnLocation(Lnet/minecraft/server/level/ServerLevel;)V"
            )
    )
    private boolean servercore$noop(ServerPlayer player, ServerLevel level) {
        // Moves operation to PlayerList.
        return false;
    }
}
