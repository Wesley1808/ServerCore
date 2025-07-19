package me.wesley1808.servercore.fabric.mixin.optimizations.sync_loads;

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

    @Inject(method = "method_17743", at = @At("HEAD"), cancellable = true)
    private void servercore$onlyRaytraceIfLoaded(ClipContext context, BlockPos pos, CallbackInfoReturnable<BlockHitResult> cir) {
        if (this instanceof Level level && !ChunkManager.hasChunk(level, pos)) {
            Vec3 vec3 = context.getFrom().subtract(context.getTo());
            cir.setReturnValue(BlockHitResult.miss(context.getTo(), Direction.getApproximateNearest(vec3.x, vec3.y, vec3.z), BlockPos.containing(context.getTo())));
        }
    }
}
