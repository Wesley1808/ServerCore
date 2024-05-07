package me.wesley1808.servercore.mixin.optimizations.players;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

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
@Mixin(PlayerList.class)
public class PlayerListMixin {

    // Paper - Finds random spawn location for new players.
    @Inject(
            method = "placeNewPlayer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerPlayer;setServerLevel(Lnet/minecraft/server/level/ServerLevel;)V",
                    shift = At.Shift.BEFORE
            )
    )
    private void servercore$moveToSpawn(
            Connection connection, ServerPlayer player, CommonListenerCookie cookie, CallbackInfo ci,
            @Local(ordinal = 0) Optional<CompoundTag> playerData,
            @Local(ordinal = 1) ServerLevel level
    ) {
        if (playerData.isEmpty()) {
            player.fudgeSpawnLocation(level);
        }
    }

    // ServerCore - Finds random spawn location for respawning players without spawnpoint.
    @Inject(
            method = "respawn",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerPlayer;restoreFrom(Lnet/minecraft/server/level/ServerPlayer;Z)V",
                    shift = At.Shift.BEFORE,
                    ordinal = 0
            )
    )
    private void servercore$moveToSpawn(
            ServerPlayer oldPlayer, boolean bl, CallbackInfoReturnable<ServerPlayer> cir,
            @Local(ordinal = 0) Optional<Vec3> spawnPoint,
            @Local(ordinal = 1) ServerPlayer player
    ) {
        if (spawnPoint.isEmpty()) {
            player.fudgeSpawnLocation(player.serverLevel());
        }
    }
}
