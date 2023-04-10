package me.wesley1808.servercore.mixin.features.ticking;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import me.wesley1808.servercore.common.config.tables.FeatureConfig;
import me.wesley1808.servercore.common.utils.ChunkManager;
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
    private boolean lobotomized = false;

    @Unique
    private int notLobotomizedCount = 0;

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
        return !FeatureConfig.LOBOTOMIZE_VILLAGERS.get() || !this.isLobotomized() || this.tickCount % FeatureConfig.LOBOTOMIZED_TICK_INTERVAL.get() == 0;
    }

    private boolean isLobotomized() {
        // Check half as often if not lobotomized for the last 3+ consecutive checks
        if (this.tickCount % (this.notLobotomizedCount > 3 ? 600 : 300) == 0) {
            // Offset Y for short blocks like dirt_path/farmland
            this.lobotomized = this.isPassenger() || !this.canTravel(new BlockPos(this.getX(), this.getY() + 0.0625D, this.getZ()));

            if (this.lobotomized) {
                this.notLobotomizedCount = 0;
            } else {
                this.notLobotomizedCount++;
            }
        }

        return this.lobotomized;
    }

    private boolean canTravel(BlockPos center) {
        ChunkAccess chunk = ChunkManager.getChunkNow(this.level, center);
        if (chunk == null) {
            return false;
        }

        BlockPos.MutableBlockPos mutable = center.mutable();
        boolean canJump = !this.hasCollisionAt(chunk, mutable.move(Direction.UP, 2));

        for (Direction direction : Direction.Plane.HORIZONTAL) {
            if (this.canTravelTo(mutable.setWithOffset(center, direction), canJump)) {
                return true;
            }
        }
        return false;
    }

    private boolean canTravelTo(BlockPos.MutableBlockPos mutable, boolean canJump) {
        ChunkAccess chunk = ChunkManager.getChunkNow(this.level, mutable);
        if (chunk == null) {
            return false;
        }

        Block bottom = chunk.getBlockState(mutable).getBlock();
        if (bottom instanceof BedBlock) {
            // Allows iron farms to function normally
            return true;
        }

        if (this.hasCollisionAt(chunk, mutable.move(Direction.UP))) {
            // Early return if the top block has collision.
            return false;
        }

        // The villager can only jump if:
        // - There is no collision above the villager
        // - There is no collision above the top block
        // - The bottom block is short enough to jump on
        boolean isTallBlock = bottom instanceof FenceBlock || bottom instanceof FenceGateBlock || bottom instanceof WallBlock;
        return !bottom.hasCollision || (canJump && !isTallBlock && !this.hasCollisionAt(chunk, mutable.move(Direction.UP)));
    }

    private boolean hasCollisionAt(ChunkAccess chunk, BlockPos pos) {
        return chunk.getBlockState(pos).getBlock().hasCollision;
    }
}
