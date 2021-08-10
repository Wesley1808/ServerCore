package org.provim.servercore.mixin.performance;

import net.minecraft.block.BedBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.provim.servercore.config.Config;
import org.provim.servercore.utils.ChunkManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Inspired by: Purpur
 * https://github.com/pl3xgaming/Purpur/blob/ver/1.17.1/patches/server/0133-Lobotomize-stuck-villagers.patch
 */

@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin {
    private boolean slowed = false;
    private int ticks = 0;

    @Redirect(method = "mobTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/brain/Brain;tick(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/LivingEntity;)V"))
    private void slowTrappedVillagers(Brain<VillagerEntity> brain, ServerWorld world, LivingEntity entity) {
        this.ticks++;
        VillagerEntity villager = (VillagerEntity) (Object) this;
        if (Config.instance().slowTrappedVillagers && isSlowed(villager)) {
            if (this.ticks % Config.instance().slowedVillagerTickInterval == 0) {
                brain.tick(world, villager);
            }
        } else {
            brain.tick(world, villager);
        }
    }

    private boolean isSlowed(VillagerEntity villager) {
        if (this.ticks % 300 == 0) {
            this.slowed = !canTravel(villager, villager.getBlockPos());
        }

        return this.slowed;
    }

    private boolean canTravel(VillagerEntity villager, BlockPos pos) {
        return canTravelTo(villager, pos.east()) || canTravelTo(villager, pos.west()) || canTravelTo(villager, pos.north()) || canTravelTo(villager, pos.south());
    }

    private boolean canTravelTo(VillagerEntity villager, BlockPos pos) {
        // Returns true in case its surrounded by any bed. This way we don't break iron farms.
        if (ChunkManager.getStateIfVisible(villager.getEntityWorld(), pos).getBlock() instanceof BedBlock) {
            return true;
        }

        Path path = villager.getNavigation().findPathTo(pos, 0);
        return path != null && path.reachesTarget();
    }
}
