package org.provim.servercore.mixin.limits;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.brain.task.VillagerBreedTask;
import net.minecraft.entity.passive.VillagerEntity;
import org.provim.servercore.config.Config;
import org.provim.servercore.utils.TickUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(VillagerBreedTask.class)
public abstract class VillagerBreedTaskMixin {

    /**
     * Cancels villager breeding if there are too many villagers in the surrounding area.
     *
     * @return Double: distance to other villager.
     */

    @Redirect(method = "keepRunning*", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/VillagerEntity;squaredDistanceTo(Lnet/minecraft/entity/Entity;)D"))
    public double cancelBreeding(VillagerEntity villagerEntity, Entity entity) {
        if (TickUtils.checkForEntities(villagerEntity, Config.ENTITY_CONFIG.villagerCount.get(), Config.ENTITY_CONFIG.villagerRange.get())) {
            ((VillagerEntity) entity).setBreedingAge(6000);
            villagerEntity.setBreedingAge(6000);
            return 10;
        }

        return villagerEntity.squaredDistanceTo(entity);
    }
}