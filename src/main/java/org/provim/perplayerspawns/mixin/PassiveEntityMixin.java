package org.provim.perplayerspawns.mixin;

import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PassiveEntity.class)
public abstract class PassiveEntityMixin extends MobEntity {

    protected PassiveEntityMixin(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow
    public abstract void setBreedingAge(int age);

    /**
     * @author Wesley1808
     * @reason Fixing a ClassCastException caused by passive entities casting ZombieEntity$ZombieData to PassiveEntity$PassiveData.
     */

    @Override
    @Overwrite
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        if (!(entityData instanceof PassiveEntity.PassiveData)) {
            entityData = new PassiveEntity.PassiveData(true);
        }

        var passiveData = (PassiveEntity.PassiveData) entityData;
        if (passiveData.canSpawnBaby() && passiveData.getSpawnedCount() > 0 && this.random.nextFloat() <= passiveData.getBabyChance()) {
            this.setBreedingAge(-24000);
        }

        passiveData.countSpawned();
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }
}