package me.wesley1808.servercore.mixin.optimizations.players;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Based on: Paper (Don-t-move-existing-players-to-world-spawn.patch)
 * <p>
 * This can cause a nasty server lag the spawn chunks are not kept loaded,
 * or they aren't finished loading yet, or if the world spawn radius is
 * larger than the keep loaded range.
 * <p>
 * By skipping this, we avoid potential for a large spike on server start.
 * <p>
 * Patch Author: Aikar (aikar@aikar.co)
 * <br>
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
