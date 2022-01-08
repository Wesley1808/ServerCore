package me.wesley1808.servercore.mixin.features.misc;

import it.unimi.dsi.fastutil.objects.ObjectIterator;
import me.wesley1808.servercore.config.tables.FeatureConfig;
import me.wesley1808.servercore.utils.TickManager;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChunkMap.class)
public abstract class ChunkMapMixin {

    @Redirect(method = "processUnloads", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/ObjectIterator;hasNext()Z", ordinal = 0))
    private boolean limitChunkSaves(ObjectIterator<ChunkHolder> iterator) {
        final int threshold = FeatureConfig.CHUNK_SAVE_THRESHOLD.get();
        if (threshold < 0) return iterator.hasNext();

        return TickManager.getAverageTickTime() < threshold && iterator.hasNext();
    }
}