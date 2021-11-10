package org.provim.servercore.mixin.performance.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayerGameMode;
import org.provim.servercore.utils.ChunkManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerGameMode.class)
public abstract class ServerPlayerGameModeMixin {
    @Shadow
    protected ServerLevel level;

    // Don't load chunks for block break packets.
    @Inject(method = "handleBlockBreakAction", at = @At("HEAD"), cancellable = true)
    private void onlyProcessIfLoaded(BlockPos pos, ServerboundPlayerActionPacket.Action action, Direction direction, int i, CallbackInfo ci) {
        if (!ChunkManager.isChunkLoaded(this.level, pos)) {
            ci.cancel();
        }
    }
}
