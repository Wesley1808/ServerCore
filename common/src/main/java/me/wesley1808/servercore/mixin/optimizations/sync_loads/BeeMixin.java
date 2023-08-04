package me.wesley1808.servercore.mixin.optimizations.sync_loads;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
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

@Mixin(Bee.class)
public abstract class BeeMixin extends Animal {
    @Shadow
    @Nullable
    BlockPos hivePos;

    private BeeMixin(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @ModifyExpressionValue(
            method = "isHiveValid",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/animal/Bee;isTooFarAway(Lnet/minecraft/core/BlockPos;)Z"
            )
    )
    private boolean servercore$onlyValidateIfLoaded(boolean isTooFarAway) {
        // noinspection ConstantConditions
        return isTooFarAway || !ChunkManager.hasChunk(this.level(), this.hivePos);
    }
}