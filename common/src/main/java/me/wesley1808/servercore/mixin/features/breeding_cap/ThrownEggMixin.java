package me.wesley1808.servercore.mixin.features.breeding_cap;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.wesley1808.servercore.common.utils.BreedingCap;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ThrownEgg.class)
public abstract class ThrownEggMixin extends ThrowableItemProjectile {
    private ThrownEggMixin(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
    }

    @ModifyExpressionValue(
            method = "onHit",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/RandomSource;nextInt(I)I",
                    ordinal = 0
            )
    )
    public int servercore$enforceBreedCap(int value) {
        if (value == 0 && BreedingCap.ANIMAL.exceedsLimit(EntityType.CHICKEN, this.level, this.blockPosition())) {
            return 1;
        }

        return value;
    }
}