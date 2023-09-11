package me.wesley1808.servercore.mixin.features.ticking;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import me.wesley1808.servercore.common.config.tables.FeatureConfig;
import me.wesley1808.servercore.common.utils.ChunkManager;
import me.wesley1808.servercore.common.utils.EntityTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Based on: Purpur (Lobotomize-stuck-villagers.patch)
 * <p>
 * Patch Author: William Blake Galbreath (Blake.Galbreath@GMail.com)
 */
@Mixin(Villager.class)
public abstract class VillagerMixin extends AbstractVillager {
    @Unique
    private boolean servercore$lobotomized = false;

    @Unique
    private int servercore$notLobotomizedCount = 0;

    private VillagerMixin(EntityType<? extends AbstractVillager> entityType, Level level) {
        super(entityType, level);
    }

    @WrapWithCondition(
            method = "customServerAiStep",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/ai/Brain;tick(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;)V"
            )
    )
    private boolean servercore$shouldTickBrain(Brain<Villager> brain, ServerLevel level, LivingEntity livingEntity) {
        return !FeatureConfig.LOBOTOMIZE_VILLAGERS.get() || !this.servercore$isLobotomized() || this.tickCount % FeatureConfig.LOBOTOMIZED_TICK_INTERVAL.get() == 0;
    }

    @Unique
    private boolean servercore$isLobotomized() {
        // Check half as often if not lobotomized for the last 3+ consecutive checks
        if (this.tickCount % (this.servercore$notLobotomizedCount > 3 ? 600 : 300) == 0) {

            this.servercore$lobotomized = !this.getTags().contains(EntityTags.EXCLUDE_FROM_LOBOTOMIZATION) && (
                    this.isPassenger() || !this.servercore$canTravel()
            );

            if (this.servercore$lobotomized) {
                this.servercore$notLobotomizedCount = 0;
            } else {
                this.servercore$notLobotomizedCount++;
            }
        }

        return this.servercore$lobotomized;
    }

    @Unique
    private boolean servercore$canTravel() {
        // Offset Y for short blocks like dirt_path/farmland
        BlockPos center = BlockPos.containing(this.getX(), this.getY() + 0.0625D, this.getZ());
        ChunkAccess chunk = ChunkManager.getChunkNow(this.level(), center);
        if (chunk == null) {
            return false;
        }

        BlockPos.MutableBlockPos mutable = center.mutable();
        boolean canJump = !this.servercore$hasCollisionAt(chunk, mutable.move(Direction.UP, 2));

        for (Direction direction : Direction.Plane.HORIZONTAL) {
            if (this.servercore$canTravelTo(mutable.setWithOffset(center, direction), canJump)) {
                return true;
            }
        }
        return false;
    }

    @Unique
    private boolean servercore$canTravelTo(BlockPos.MutableBlockPos mutable, boolean canJump) {
        ChunkAccess chunk = ChunkManager.getChunkNow(this.level(), mutable);
        if (chunk == null) {
            return false;
        }

        Block bottom = chunk.getBlockState(mutable).getBlock();
        if (bottom instanceof BedBlock) {
            // Allows iron farms to function normally
            return true;
        }

        if (this.servercore$hasCollisionAt(chunk, mutable.move(Direction.UP))) {
            // Early return if the top block has collision.
            return false;
        }

        // The villager can only jump if:
        // - There is no collision above the villager
        // - There is no collision above the top block
        // - The bottom block is short enough to jump on
        boolean isTallBlock = bottom instanceof FenceBlock || bottom instanceof FenceGateBlock || bottom instanceof WallBlock;
        return !bottom.hasCollision || (canJump && !isTallBlock && !this.servercore$hasCollisionAt(chunk, mutable.move(Direction.UP)));
    }

    @Unique
    private boolean servercore$hasCollisionAt(ChunkAccess chunk, BlockPos pos) {
        return chunk.getBlockState(pos).getBlock().hasCollision;
    }
}
