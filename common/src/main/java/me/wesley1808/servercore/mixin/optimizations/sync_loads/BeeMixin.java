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

@Mixin(Bee.class)
public abstract class BeeMixin extends Animal {
    @Shadow
    @Nullable
    BlockPos hivePos;

    @Shadow
    abstract boolean isTooFarAway(BlockPos pos);

    private BeeMixin(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(
            method = "isHiveValid",
            cancellable = true,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;",
                    shift = At.Shift.BEFORE,
                    ordinal = 0
            )
    )
    private void servercore$onlyValidateIfLoaded(CallbackInfoReturnable<Boolean> cir) {
        // noinspection ConstantConditions
        if (this.isTooFarAway(this.hivePos) || !ChunkManager.hasChunk(this.level, this.hivePos)) {
            cir.setReturnValue(false);
        }
    }
}