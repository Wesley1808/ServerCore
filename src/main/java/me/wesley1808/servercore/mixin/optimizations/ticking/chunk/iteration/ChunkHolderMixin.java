package me.wesley1808.servercore.mixin.optimizations.ticking.chunk.iteration;

import me.wesley1808.servercore.common.interfaces.chunk.IServerChunkCache;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.LightLayer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkHolder.class)
public abstract class ChunkHolderMixin {
    @Shadow
    @Final
    private LevelHeightAccessor levelHeightAccessor;

    @Inject(
            method = "blockChanged",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/server/level/ChunkHolder;hasChangedSections:Z",
                    opcode = Opcodes.PUTFIELD
            )
    )
    private void servercore$onBlockChanged(BlockPos blockPos, CallbackInfo ci) {
        this.requiresBroadcast();
    }

    @Inject(
            method = "sectionLightChanged",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/BitSet;set(I)V"
            )
    )
    private void servercore$onLightChanged(LightLayer lightLayer, int i, CallbackInfo ci) {
        this.requiresBroadcast();
    }

    private void requiresBroadcast() {
        if (this.levelHeightAccessor instanceof ServerLevel serverLevel) {
            ((IServerChunkCache) serverLevel.getChunkSource()).requiresBroadcast((ChunkHolder) (Object) this);
        }
    }
}
