package org.provim.servercore.mixin.features.misc;

import net.minecraft.server.level.ChunkMap;
import org.provim.servercore.config.tables.FeatureConfig;
import org.provim.servercore.utils.TickManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.BooleanSupplier;

@Mixin(ChunkMap.class)
public abstract class ChunkMapMixin {

    @Redirect(method = "processUnloads", at = @At(value = "INVOKE", target = "Ljava/util/function/BooleanSupplier;getAsBoolean()Z", ordinal = 2))
    private boolean limitChunkSaves(BooleanSupplier supplier) {
        final int threshold = FeatureConfig.CHUNK_SAVE_THRESHOLD.get();
        if (threshold < 0) {
            return supplier.getAsBoolean();
        }

        return TickManager.getAverageTickTime() < threshold && supplier.getAsBoolean();
    }
}