package org.provim.servercore.mixin.performance.misc;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.progress.ChunkProgressListener;
import org.objectweb.asm.Opcodes;
import org.provim.servercore.config.tables.FeatureConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

    @Shadow
    private int tickCount;

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
