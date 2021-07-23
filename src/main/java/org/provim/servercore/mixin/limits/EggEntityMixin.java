package org.provim.servercore.mixin.limits;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.thrown.EggEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.world.World;
import org.provim.servercore.config.Config;
import org.provim.servercore.utils.TickUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Random;

@Mixin(EggEntity.class)
abstract class EggEntityMixin extends ThrownItemEntity {

    protected EggEntityMixin(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    /**
     * Cancels chicken spawning from eggs if there are too many chickens in the surrounding area.
     *
     * @return Integer: chance for chickens to spawn from an egg.
     */

    @Redirect(method = "onCollision", at = @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I", ordinal = 0))
    public int shouldSpawn(Random random, int bound) {
        if (TickUtils.checkForEntities(EntityType.CHICKEN, this.world, this.getBlockPos(), Config.instance().animalLimit, Config.instance().animalLimitRange)) {
            return 1;
        } else {
            return random.nextInt(8);
        }
    }
}