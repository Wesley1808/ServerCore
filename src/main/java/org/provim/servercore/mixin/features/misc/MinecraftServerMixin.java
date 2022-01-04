package org.provim.servercore.mixin.features.misc;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.progress.ChunkProgressListener;
import org.provim.servercore.config.tables.FeatureConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MinecraftServer.class, priority = 900)
public abstract class MinecraftServerMixin {

    @Inject(method = "prepareLevels", at = @At("HEAD"), cancellable = true)
    private void disableSpawnChunks(ChunkProgressListener chunkProgressListener, CallbackInfo ci) {
        if (FeatureConfig.DISABLE_SPAWN_CHUNKS.get()) {
            ci.cancel();
        }
    }

    @ModifyConstant(method = "tickServer", constant = @Constant(intValue = 6000), require = 0)
    public int modifyAutoSaveInterval(int constant) {
        return FeatureConfig.AUTO_SAVE_INTERVAL.get() * 1200;
    }
}
