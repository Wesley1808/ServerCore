package me.wesley1808.servercore.mixin.optimizations.sync_loads;

import me.wesley1808.servercore.common.utils.ChunkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow
    public abstract double getEyeY();

    @Shadow
    public Level level;

    @Shadow
    public abstract double getX();

    @Shadow
    public abstract double getZ();

    @Inject(
            method = {"updateFluidOnEyes"},
            at = {@At("HEAD")},
            cancellable = true
    )
    private void servercore$onlyUpdateFluidIfLoaded(CallbackInfo ci) {
        BlockPos blockPos = new BlockPos(this.getX(), this.getEyeY(), this.getZ());
        if (!ChunkManager.isChunkLoaded(this.level, blockPos)) {
            ci.cancel();
        }
    }
}
