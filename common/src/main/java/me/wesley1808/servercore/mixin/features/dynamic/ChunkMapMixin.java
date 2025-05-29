package me.wesley1808.servercore.mixin.features.dynamic;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteMaps;
import it.unimi.dsi.fastutil.longs.LongConsumer;
import me.wesley1808.servercore.common.config.Config;
import me.wesley1808.servercore.common.utils.ChunkTickManager;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.DistanceManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;

@Mixin(ChunkMap.class)
public class ChunkMapMixin {

    @ModifyReturnValue(method = "playerIsCloseEnoughForSpawning", at = @At(value = "RETURN"))
    private boolean servercore$withinChunkTickDistance(boolean original, ServerPlayer player, ChunkPos pos) {
        return original && ChunkTickManager.isChunkTickable(pos, player);
    }

    @WrapWithCondition(
            method = "forEachBlockTickingChunk",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ChunkMap$DistanceManager;forEachEntityTickingChunk(Lit/unimi/dsi/fastutil/longs/LongConsumer;)V"
            )
    )
    private boolean servercore$forEachBlockTickingChunk$notTheCauseOfTickLag(@Coerce DistanceManager distanceManager, LongConsumer consumer) {
        if (Config.get().features().chunkTickDistanceAffectsRandomTicks()) {
            for (Long2ByteMap.Entry entry : Long2ByteMaps.fastIterable(distanceManager.simulationChunkTracker.chunks)) {
                if (ChunkTickManager.isChunkTickable(entry.getByteValue(), distanceManager.simulationDistance)) {
                    consumer.accept(entry.getLongKey());
                }
            }
            return false;
        }
        return true;
    }
}
