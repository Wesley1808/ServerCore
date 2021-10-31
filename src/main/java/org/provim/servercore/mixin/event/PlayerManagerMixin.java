package org.provim.servercore.mixin.event;

import net.minecraft.network.Packet;
import net.minecraft.server.PlayerManager;
import org.provim.servercore.utils.TickManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {

    // Don't force clients to reload chunks every time view distance updates.
    @Redirect(method = "setViewDistance", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;sendToAll(Lnet/minecraft/network/Packet;)V"))
    private void sendViewDistanceUpdate(PlayerManager instance, Packet<?> packet) {
        if (TickManager.shouldUpdateClients()) {
            instance.sendToAll(packet);
        }
    }
}
