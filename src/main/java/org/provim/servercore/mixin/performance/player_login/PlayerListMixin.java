package org.provim.servercore.mixin.performance.player_login;

import com.mojang.authlib.GameProfile;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;

/**
 * From: PaperMC (Don-t-move-existing-players-to-world-spawn.patch)
 * License: GPL-3.0 (licenses/GPL.md)
 */

@Mixin(PlayerList.class)
public abstract class PlayerListMixin {

    // Only move to spawn on first login.
    @Inject(method = "placeNewPlayer", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", shift = At.Shift.BEFORE, target = "Lnet/minecraft/server/level/ServerPlayer;setLevel(Lnet/minecraft/server/level/ServerLevel;)V"))
    private void moveToSpawn(Connection connection, ServerPlayer serverPlayer, CallbackInfo ci, GameProfile gameProfile, GameProfileCache gameProfileCache, Optional optional, String string, CompoundTag compoundTag, ResourceKey resourceKey, ServerLevel serverLevel, ServerLevel serverLevel2) {
        if (compoundTag == null) {
            serverPlayer.fudgeSpawnLocation(serverLevel2);
        }
    }
}