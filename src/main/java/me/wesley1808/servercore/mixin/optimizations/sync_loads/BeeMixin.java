package me.wesley1808.servercore.mixin.optimizations.sync_loads;

import me.wesley1808.servercore.common.utils.ChunkManager;
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
 * Based on: Paper (Do-not-allow-bees-to-load-chunks-for-beehives.patch)
 * <p>
 * Patch Author: chickeneer (emcchickeneer@gmail.com)
 */
@Mixin(Bee.class)
public abstract class BeeMixin extends Animal {
    @Shadow
    @Nullable BlockPos hivePos;

    private BeeMixin(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "doesHiveHaveSpace", at = @At("HEAD"), cancellable = true)
    private void servercore$onlyCheckIfLoaded(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (!ChunkManager.hasChunk(this.level, pos)) {
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
    private void servercore$onlyCheckIfLoaded(CallbackInfoReturnable<Boolean> cir) {
        // noinspection ConstantConditions
        if (!ChunkManager.hasChunk(this.level, this.hivePos)) {
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
    private void servercore$onlyValidateIfLoaded(CallbackInfoReturnable<Boolean> cir) {
        // noinspection ConstantConditions
        if (!ChunkManager.hasChunk(this.level, this.hivePos)) {
            cir.setReturnValue(true);
        }
    }
}