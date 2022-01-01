package org.provim.servercore.mixin.events;

import net.minecraft.server.players.PlayerList;
import org.provim.servercore.utils.TickManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public abstract class PlayerListMixin {

    @Inject(method = "setViewDistance", at = @At("HEAD"))
    private void updateViewDistance(int i, CallbackInfo ci) {
        TickManager.setViewDistance(i);
    }

    @Inject(method = "setSimulationDistance", at = @At("HEAD"))
    private void updateSimulationDistance(int i, CallbackInfo ci) {
        TickManager.setSimulationDistance(i);
    }
}
