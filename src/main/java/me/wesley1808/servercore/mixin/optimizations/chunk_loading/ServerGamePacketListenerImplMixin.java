package me.wesley1808.servercore.mixin.optimizations.chunk_loading;

import me.wesley1808.servercore.utils.ChunkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {

    @Inject(
            method = "handleUseItemOn",
            locals = LocalCapture.CAPTURE_FAILHARD,
            cancellable = true,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerPlayer;resetLastActionTime()V",
                    shift = At.Shift.AFTER
            )
    )
    private void onlyProcessIfLoaded(ServerboundUseItemOnPacket packet, CallbackInfo ci, ServerLevel level, InteractionHand interactionHand, ItemStack itemStack, BlockHitResult blockHitResult, BlockPos pos, Direction direction) {
        if (!ChunkManager.isChunkLoaded(level, pos)) {
            ci.cancel();
        }
    }
}
