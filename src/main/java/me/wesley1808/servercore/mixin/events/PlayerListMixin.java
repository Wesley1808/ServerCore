package me.wesley1808.servercore.mixin.events;

import me.wesley1808.servercore.utils.TickManager;
import net.minecraft.server.players.PlayerList;
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
