package me.wesley1808.servercore.mixin.features.dynamic;

import me.wesley1808.servercore.common.dynamic.DynamicSetting;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkMap.class)
public class ChunkMapMixin {

    @Inject(method = "playerIsCloseEnoughForSpawning", at = @At(value = "RETURN"), cancellable = true)
    private void servercore$withinChunkTickDistance(ServerPlayer player, ChunkPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue() && player.chunkPosition().getChessboardDistance(pos) > DynamicSetting.CHUNK_TICK_DISTANCE.get()) {
            cir.setReturnValue(false);
        }
    }
}
