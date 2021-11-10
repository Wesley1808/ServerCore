package org.provim.servercore.mixin.event;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.progress.ChunkProgressListener;
import org.objectweb.asm.Opcodes;
import org.provim.servercore.ServerCore;
import org.provim.servercore.config.Config;
import org.provim.servercore.config.tables.FeatureConfig;
import org.provim.servercore.utils.TickManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

    @Shadow
    private int tickCount;

    /**
     * [Server Tick Event]
     */

    @Inject(method = "tickServer", at = @At("TAIL"))
    private void onTick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        if (this.tickCount % 300 == 0) {
            TickManager.runPerformanceChecks((MinecraftServer) (Object) this);
        }
    }

    /**
     * [Server Startup Event]
     */

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;updateStatusIcon(Lnet/minecraft/network/protocol/status/ServerStatus;)V", ordinal = 0), method = "runServer")
    private void afterSetupServer(CallbackInfo info) {
        final MinecraftServer server = (MinecraftServer) (Object) this;
        ServerCore.setServer(server);
        TickManager.initValues(server);
    }

    /**
     * [Server Shutdown Event]
     */

    @Inject(method = "stopServer", at = @At("HEAD"))
    private void beforeShutdownServer(CallbackInfo info) {
        Config.save();
    }

    @Inject(method = "prepareLevels", at = @At("HEAD"), cancellable = true)
    private void disableSpawnChunks(ChunkProgressListener chunkProgressListener, CallbackInfo ci) {
        if (FeatureConfig.DISABLE_SPAWN_CHUNKS.get()) {
            ci.cancel();
        }
    }

    @Redirect(method = "tickServer", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/server/MinecraftServer;tickCount:I", ordinal = 1))
    public int modifyAutoSaveInterval(MinecraftServer minecraftServer) {
        return this.tickCount % (FeatureConfig.AUTO_SAVE_INTERVAL.get() * 1200) == 0 ? 6000 : -1;
    }
}
