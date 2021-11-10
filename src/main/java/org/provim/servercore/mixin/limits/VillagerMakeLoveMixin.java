package org.provim.servercore.mixin.limits;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.behavior.VillagerMakeLove;
import net.minecraft.world.entity.npc.Villager;
import org.provim.servercore.config.tables.EntityConfig;
import org.provim.servercore.utils.TickManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(VillagerMakeLove.class)
public abstract class VillagerMakeLoveMixin {

    /**
     * Cancels villager breeding if there are too many villagers in the surrounding area.
     *
     * @return Double: distance to other villager.
     */

    @Redirect(method = "tick(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/npc/Villager;J)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/npc/Villager;distanceToSqr(Lnet/minecraft/world/entity/Entity;)D"))
    public double cancelBreeding(Villager villager, Entity entity) {
        if (TickManager.checkForEntities(villager, EntityConfig.VILLAGER_COUNT.get(), EntityConfig.VILLAGER_RANGE.get())) {
            ((Villager) entity).setAge(6000);
            villager.setAge(6000);
            return 10;
        }

        return villager.distanceToSqr(entity);
    }
}