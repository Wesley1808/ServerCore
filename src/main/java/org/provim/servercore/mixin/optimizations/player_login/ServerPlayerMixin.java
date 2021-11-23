package org.provim.servercore.mixin.optimizations.player_login;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * From: PaperMC (Don-t-move-existing-players-to-world-spawn.patch)
 * License: GPL-3.0 (licenses/GPL.md)
 */

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;fudgeSpawnLocation(Lnet/minecraft/server/level/ServerLevel;)V"))
    private void cancelOp(ServerPlayer player, ServerLevel level) {
        // Moves operation to PlayerList.
    }
}
