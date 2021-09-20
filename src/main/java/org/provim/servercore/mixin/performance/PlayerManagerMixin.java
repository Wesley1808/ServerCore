package org.provim.servercore.mixin.performance;

import com.mojang.authlib.GameProfile;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.UserCache;
import net.minecraft.util.registry.RegistryKey;
import org.provim.servercore.mixin.accessor.ServerPlayerEntityAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {

    // Paper - only move to spawn on first login, otherwise, stay where you are...
    @Inject(method = "onPlayerConnect", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", shift = At.Shift.BEFORE, target = "Lnet/minecraft/server/network/ServerPlayerEntity;setWorld(Lnet/minecraft/server/world/ServerWorld;)V"))
    private void moveToSpawn(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci, GameProfile gameProfile, UserCache userCache, Optional optional, String string, NbtCompound nbtCompound, RegistryKey registryKey, ServerWorld serverWorld, ServerWorld serverWorld3) {
        if (nbtCompound == null) {
            ((ServerPlayerEntityAccessor) player).movePlayerToSpawn(serverWorld3);
        }
    }
}
