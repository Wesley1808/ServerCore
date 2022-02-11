package me.wesley1808.servercore.mixin.features.ticking;

import me.wesley1808.servercore.config.tables.FeatureConfig;
import me.wesley1808.servercore.utils.ChunkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * From: Purpur (Lobotomize-stuck-villagers.patch)
 */

@Mixin(Villager.class)
public abstract class VillagerMixin extends AbstractVillager {
    @Unique
    private boolean lobotomized = false;

    @Unique
    private int notLobotomizedCount = 0;

    private VillagerMixin(EntityType<? extends AbstractVillager> entityType, Level level) {
        super(entityType, level);
    }

    @Redirect(
            method = "customServerAiStep",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/ai/Brain;tick(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;)V"
            )
    )
    private void shouldTickBrain(Brain<Villager> brain, ServerLevel level, LivingEntity livingEntity) {
        if (!FeatureConfig.LOBOTOMIZE_VILLAGERS.get() || !isLobotomized() || this.tickCount % FeatureConfig.LOBOTOMIZED_TICK_INTERVAL.get() == 0) {
            brain.tick(level, (Villager) (Object) this);
        }
    }

    private boolean isLobotomized() {
        // Check half as often if not lobotomized for the last 3+ consecutive checks
        if (this.tickCount % (this.notLobotomizedCount > 3 ? 600 : 300) == 0) {
            // Offset Y for short blocks like dirt_path/farmland
            this.lobotomized = this.getNavigation().isStuck() || !this.canTravel(new BlockPos(this.getX(), this.getY() + 0.0625D, this.getZ()));

            if (this.lobotomized) {
                this.notLobotomizedCount = 0;
            } else {
                this.notLobotomizedCount++;
            }
        }

        return this.lobotomized;
    }

    private boolean canTravel(BlockPos pos) {
        return canTravelTo(pos.east()) || canTravelTo(pos.west()) || canTravelTo(pos.north()) || canTravelTo(pos.south());
    }

    private boolean canTravelTo(BlockPos pos) {
        LevelChunk chunk = ChunkManager.getChunkIfLoaded(this.level, pos);
        if (chunk == null) {
            return false;
        }

        Block bottom = chunk.getBlockState(pos).getBlock();
        if (bottom instanceof BedBlock) {
            // Allows iron farms to function normally
            return true;
        }

        if (bottom instanceof FenceBlock || bottom instanceof FenceGateBlock || bottom instanceof WallBlock) {
            // Bottom block is too tall to get over
            return false;
        }

        Block top = chunk.getBlockState(pos.above()).getBlock();
        // Only if both blocks have no collision
        return !bottom.hasCollision && !top.hasCollision;
    }
}
