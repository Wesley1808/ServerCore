package me.wesley1808.servercore.mixin.optimizations.chunk_loading;

import me.wesley1808.servercore.utils.ChunkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * From: PaperMC (Do-not-allow-bees-to-load-chunks-for-beehives.patch)
 */

@Mixin(Bee.class)
public abstract class BeeMixin extends Animal {
    @Shadow
    @Nullable BlockPos hivePos;

    private BeeMixin(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "doesHiveHaveSpace", at = @At("HEAD"), cancellable = true)
    private void onlyCheckIfLoaded(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (!ChunkManager.isChunkLoaded(this.level, pos)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(
            method = "isHiveNearFire",
            cancellable = true,
            at = @At(
                    value = "INVOKE",
                    shift = At.Shift.BEFORE,
                    target = "Lnet/minecraft/world/level/Level;getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;"
            )
    )
    private void onlyCheckIfLoaded(CallbackInfoReturnable<Boolean> cir) {
        // noinspection ConstantConditions
        if (!ChunkManager.isChunkLoaded(this.level, this.hivePos)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(
            method = "isHiveValid",
            cancellable = true,
            at = @At(
                    value = "INVOKE",
                    shift = At.Shift.BEFORE,
                    target = "Lnet/minecraft/world/level/Level;getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;"
            )
    )
    private void onlyValidateIfLoaded(CallbackInfoReturnable<Boolean> cir) {
        // noinspection ConstantConditions
        if (!ChunkManager.isChunkLoaded(this.level, this.hivePos)) {
            cir.setReturnValue(true);
        }
    }
}