package org.provim.servercore.mixin.fixes;

import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(PassiveEntity.class)
public abstract class PassiveEntityMixin extends MobEntity {
    protected PassiveEntityMixin(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
    }

    /**
     * Crash fix
     * Fixes a ClassCastException caused by passive entities casting ZombieEntity$ZombieData to PassiveEntity$PassiveData.
     */

    @ModifyVariable(method = "initialize", at = @At("HEAD"), index = 4)
    private EntityData init(EntityData entityData) {
        if (!(entityData instanceof PassiveEntity.PassiveData)) {
            return new PassiveEntity.PassiveData(true);
        }

        return entityData;
    }
}