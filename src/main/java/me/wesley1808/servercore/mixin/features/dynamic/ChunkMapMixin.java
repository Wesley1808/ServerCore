package me.wesley1808.servercore.mixin.features.dynamic;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.wesley1808.servercore.common.dynamic.DynamicSetting;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ChunkMap.class)
public class ChunkMapMixin {

    @ModifyReturnValue(method = "playerIsCloseEnoughForSpawning", at = @At(value = "RETURN"))
    private boolean servercore$withinChunkTickDistance(boolean original, ServerPlayer player, ChunkPos pos) {
        return original && player.chunkPosition().getChessboardDistance(pos) <= DynamicSetting.CHUNK_TICK_DISTANCE.get();
    }
}
