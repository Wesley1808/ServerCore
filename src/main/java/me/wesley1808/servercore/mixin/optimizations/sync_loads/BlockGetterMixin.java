package me.wesley1808.servercore.mixin.optimizations.sync_loads;

import me.wesley1808.servercore.common.utils.ChunkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockGetter.class)
public interface BlockGetterMixin {

    // Don't load chunks for raytracing.
    @Inject(method = "method_17743", at = @At("HEAD"), cancellable = true)
    private void servercore$onlyRaytraceIfLoaded(ClipContext traverseContext, BlockPos traversePos, CallbackInfoReturnable<BlockHitResult> cir) {
        if (this instanceof Level level && !ChunkManager.isChunkLoaded(level, traversePos)) {
            Vec3 vec3d = traverseContext.getFrom().subtract(traverseContext.getTo());
            cir.setReturnValue(BlockHitResult.miss(traverseContext.getTo(), Direction.getNearest(vec3d.x, vec3d.y, vec3d.z), new BlockPos(traverseContext.getTo())));
        }
    }
}
