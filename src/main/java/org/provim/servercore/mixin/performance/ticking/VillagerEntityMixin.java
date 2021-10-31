package org.provim.servercore.mixin.performance.ticking;

import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.provim.servercore.config.tables.FeatureConfig;
import org.provim.servercore.utils.ChunkManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * From: Purpur (Lobotomize-stuck-villagers.patch)
 */

@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin extends MerchantEntity {
    @Unique
    private boolean lobotomized = false;

    private VillagerEntityMixin(EntityType<? extends MerchantEntity> entityType, World world) {
        super(entityType, world);
    }

    @Redirect(method = "mobTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/brain/Brain;tick(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/LivingEntity;)V"))
    private void shouldTickBrain(Brain<VillagerEntity> brain, ServerWorld world, LivingEntity entity) {
        VillagerEntity villager = (VillagerEntity) (Object) this;
        if (FeatureConfig.LOBOTOMIZE_VILLAGERS.get() && isLobotomized()) {
            if (this.age % FeatureConfig.LOBOTOMIZED_TICK_INTERVAL.get() == 0) {
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
        final BlockState state = ChunkManager.getStateIfLoaded(this.world, pos);
        if (state == null || state.getBlock() instanceof BedBlock) {
            return true;
        }

        Path path = this.getNavigation().findPathTo(pos, 0);
        return path != null && path.reachesTarget();
    }
}
