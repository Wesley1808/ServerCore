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
    private boolean lobotomized = false;
    private int ticks = 0;

    @Redirect(method = "mobTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/brain/Brain;tick(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/LivingEntity;)V"))
    private void lobotomizeTrappedVillagers(Brain<VillagerEntity> brain, ServerWorld world, LivingEntity entity) {
        this.ticks++;
        VillagerEntity villager = (VillagerEntity) (Object) this;
        if (Config.instance().lobotomizeTrappedVillagers && isLobotomized(villager)) {
            if (this.ticks % Config.instance().lobotomizedVillagerTickInterval == 0) {
                brain.tick(world, villager);
            }
        } else {
            brain.tick(world, villager);
        }
    }

    private boolean isLobotomized(VillagerEntity villager) {
        if (this.ticks % 300 == 0) {
            this.lobotomized = !canTravel(villager, villager.getBlockPos());
        }

        return this.lobotomized;
    }

    private boolean canTravel(VillagerEntity villager, BlockPos pos) {
        return canTravelTo(villager, pos.east()) || canTravelTo(villager, pos.west()) || canTravelTo(villager, pos.north()) || canTravelTo(villager, pos.south());
    }

    private boolean canTravelTo(VillagerEntity villager, BlockPos pos) {
        // Returns true in case it's surrounded by any bed. This way we don't break iron farms.
        if (ChunkManager.getStateIfVisible(villager.getEntityWorld(), pos).getBlock() instanceof BedBlock) {
            return true;
        }

        Path path = villager.getNavigation().findPathTo(pos, 0);
        return path != null && path.reachesTarget();
    }
}
