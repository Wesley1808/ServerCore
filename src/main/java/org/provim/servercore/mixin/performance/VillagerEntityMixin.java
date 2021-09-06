package org.provim.servercore.mixin.performance;

import net.minecraft.block.BedBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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
public abstract class VillagerEntityMixin extends MerchantEntity {
    private boolean lobotomized = false;

    protected VillagerEntityMixin(EntityType<? extends MerchantEntity> entityType, World world) {
        super(entityType, world);
    }

    @Redirect(method = "mobTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/brain/Brain;tick(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/LivingEntity;)V"))
    private void lobotomizeTrappedVillagers(Brain<VillagerEntity> brain, ServerWorld world, LivingEntity entity) {
        VillagerEntity villager = (VillagerEntity) (Object) this;
        if (Config.instance().lobotomizeTrappedVillagers && isLobotomized()) {
            if (this.age % Config.instance().lobotomizedVillagerTickInterval == 0) {
                brain.tick(world, villager);
            }
        } else {
            brain.tick(world, villager);
        }
    }

    private boolean isLobotomized() {
        if (this.age % 300 == 0) {
            this.lobotomized = !canTravel(this.getBlockPos());
        }

        return this.lobotomized;
    }

    private boolean canTravel(BlockPos pos) {
        return canTravelTo(pos.east()) || canTravelTo(pos.west()) || canTravelTo(pos.north()) || canTravelTo(pos.south());
    }

    private boolean canTravelTo(BlockPos pos) {
        // Returns true in case it's surrounded by any bed. This way we don't break iron farms.
        if (ChunkManager.getStateIfVisible(this.getEntityWorld(), pos).getBlock() instanceof BedBlock) {
            return true;
        }

        Path path = this.getNavigation().findPathTo(pos, 0);
        return path != null && path.reachesTarget();
    }
}
