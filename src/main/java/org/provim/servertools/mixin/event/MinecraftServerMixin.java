package org.provim.servertools.mixin.event;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import org.provim.servertools.ServerTools;
import org.provim.servertools.config.Config;
import org.provim.servertools.config.ConfigHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

    /**
     * [Server Startup Event]
     */

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;setupServer()Z"), method = "runServer")
    private void onSetupServer(CallbackInfo info) {
        ServerTools.setServer((MinecraftServer) (Object) this);
        ConfigHandler.load();
    }

    /**
     * [Server Shutdown Event]
     */

    @Inject(at = @At("HEAD"), method = "shutdown")
    private void onShutdown(CallbackInfo info) {
        ConfigHandler.save();
    }

    @Inject(at = @At("HEAD"), method = "prepareStartRegion", cancellable = true)
    private void cancelSpawnChunks(WorldGenerationProgressListener worldGenerationProgressListener, CallbackInfo ci) {
        if (Config.instance().noSpawnChunks) {
            ci.cancel();
        }
    }
}
