package me.wesley1808.servercore.mixin.features.ender_pearls;

import me.wesley1808.servercore.common.config.Config;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {

    @Inject(method = "registerEnderPearl", at = @At("HEAD"), cancellable = true)
    private void servercore$preventEnderpearlRegistration(ThrownEnderpearl enderpearl, CallbackInfo ci) {
        // Prevents enderpearls from being registered to players.
        // This prevents the game from unloading and saving the pearls with the player.
        if (Config.get().features().preventEnderpearlChunkLoading()) {
            ci.cancel();
        }
    }

    @Inject(method = "loadAndSpawnEnderpearls", at = @At("HEAD"), cancellable = true)
    private void servercore$preventEnderpearlLoading(Optional<CompoundTag> optional, CallbackInfo ci) {
        // Prevents enderpearls from being loaded from playerdata.
        if (Config.get().features().preventEnderpearlChunkLoading()) {
            ci.cancel();
        }
    }
}
