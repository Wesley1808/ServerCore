package me.wesley1808.servercore.mixin.features.dynamic;

import me.wesley1808.servercore.common.dynamic.DynamicSetting;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public class PlayerListMixin {

    @Inject(method = "setViewDistance", at = @At("HEAD"))
    private void servercore$updateViewDistance(int viewDistance, CallbackInfo ci) {
        DynamicSetting.VIEW_DISTANCE.set(viewDistance, null);
    }

    @Inject(method = "setSimulationDistance", at = @At("HEAD"))
    private void servercore$updateSimulationDistance(int simulationDistance, CallbackInfo ci) {
        DynamicSetting.SIMULATION_DISTANCE.set(simulationDistance, null);
    }
}