package org.provim.perplayerspawns.mixin;

import net.minecraft.server.MinecraftServer;
import org.provim.perplayerspawns.config.ConfigHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

    /**
     * [Server Startup Event]
     */

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;setupServer()Z"), method = "runServer")
    private void onSetupServer(CallbackInfo info) {
        ConfigHandler.load();
    }

    /**
     * [Server Shutdown Event]
     */

    @Inject(at = @At("HEAD"), method = "shutdown")
    private void onShutdown(CallbackInfo info) {
        ConfigHandler.save();
    }

    /**
     * [Server Reload Event]
     */

    @Inject(at = @At("HEAD"), method = "reloadResources")
    private void onReload(Collection<String> datapacks, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        ConfigHandler.load();
    }
}
