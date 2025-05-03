package me.wesley1808.servercore.mixin.features.ender_pearls;

import me.wesley1808.servercore.common.config.Config;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.level.storage.ValueInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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

    @Inject(method = "loadAndSpawnEnderPearls", at = @At("HEAD"), cancellable = true)
    private void servercore$preventEnderpearlLoading(ValueInput input, CallbackInfo ci) {
        // Prevents enderpearls from being loaded from playerdata.
        if (Config.get().features().preventEnderpearlChunkLoading()) {
            ci.cancel();
        }
    }
}
