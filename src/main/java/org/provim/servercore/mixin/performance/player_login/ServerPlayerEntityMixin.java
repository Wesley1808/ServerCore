package org.provim.servercore.mixin.performance.player_login;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * From: PaperMC (Don-t-move-existing-players-to-world-spawn.patch)
 * License: GPL-3.0 (licenses/GPL.md)
 */

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;moveToSpawn(Lnet/minecraft/server/world/ServerWorld;)V"))
    private void cancelOp(ServerPlayerEntity serverPlayerEntity, ServerWorld world) {
        // Moves operation to PlayerManager.
    }
}
