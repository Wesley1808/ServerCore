package me.wesley1808.servercore.mixin.features.breeding_cap;

import me.wesley1808.servercore.common.utils.BreedingCap;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ThrownEgg.class)
public abstract class ThrownEggMixin extends ThrowableItemProjectile {
    private ThrownEggMixin(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
    }

    @Redirect(
            method = "onHit",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/RandomSource;nextInt(I)I",
                    ordinal = 0
            )
    )
    public int servercore$enforceBreedCap(RandomSource random, int i) {
        int value = random.nextInt(i);
        if (value == 0 && BreedingCap.ANIMAL.exceedsLimit(EntityType.CHICKEN, this.level, this.blockPosition())) {
            return 1;
        }

        return value;
    }
}