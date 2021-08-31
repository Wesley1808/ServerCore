package org.provim.servercore.mixin.event;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.provim.servercore.ServerCore;
import org.provim.servercore.config.Config;
import org.provim.servercore.config.ConfigHandler;
import org.provim.servercore.interfaces.TACSInterface;
import org.provim.servercore.utils.TickUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

    @Shadow
    private int ticks;

    @Shadow
    @Final
    private Map<RegistryKey<World>, ServerWorld> worlds;

    /**
     * [Server Tick Event]
     */

    @Inject(at = @At("TAIL"), method = "tick")
    private void onTick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        if (this.ticks % 300 == 0) {
            TickUtils.runPerformanceChecks((MinecraftServer) (Object) this);
        }
    }

    /**
     * [Server Startup Event]
     */

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;setupServer()Z"), method = "runServer")
    private void onSetupServer(CallbackInfo info) {
        ServerCore.setServer((MinecraftServer) (Object) this);
        ConfigHandler.load();
    }

    /**
     * [Server Save Event]
     */

    @Inject(method = "save", at = @At(value = "HEAD"))
    private void onSave(boolean suppressLogs, boolean flush, boolean force, CallbackInfoReturnable<Boolean> cir) {
        for (ServerWorld world : this.worlds.values()) {
            ((TACSInterface) world.getChunkManager().threadedAnvilChunkStorage).invalidatePlayers();
        }
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

    @Redirect(method = "tick", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/server/MinecraftServer;ticks:I", ordinal = 1))
    public int modifyAutoSaveInterval(MinecraftServer minecraftServer) {
        return this.ticks % (Config.instance().autoSaveInterval * 1200) == 0 ? 6000 : -1;
    }
}
