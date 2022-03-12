package me.wesley1808.servercore.mixin.features.entity_limits;

import me.wesley1808.servercore.config.tables.EntityLimitConfig;
import me.wesley1808.servercore.utils.TickManager;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Random;

@Mixin(ThrownEgg.class)
public abstract class ThrownEggMixin extends ThrowableItemProjectile {
    private ThrownEggMixin(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
    }

    /**
     * Cancels chicken spawning from eggs if there are too many chickens in the surrounding area.
     *
     * @return Integer: chance for chickens to spawn from an egg.
     */

    @Redirect(
            method = "onHit",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Random;nextInt(I)I",
                    ordinal = 0
            )
    )
    public int shouldSpawn(Random random, int bound) {
        if (TickManager.checkForEntities(EntityType.CHICKEN, this.level, this.blockPosition(), EntityLimitConfig.ANIMAL_COUNT.get(), EntityLimitConfig.ANIMAL_RANGE.get())) {
            return 1;
        } else {
            return random.nextInt(8);
        }
    }
}