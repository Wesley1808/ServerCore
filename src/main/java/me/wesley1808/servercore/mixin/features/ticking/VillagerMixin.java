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
        if (!FeatureConfig.LOBOTOMIZE_VILLAGERS.get() || !this.isLobotomized() || this.tickCount % FeatureConfig.LOBOTOMIZED_TICK_INTERVAL.get() == 0) {
            brain.tick(level, (Villager) (Object) this);
        }
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

    private boolean canTravel(BlockPos pos) {
        LevelChunk chunk = ChunkManager.getChunkIfLoaded(this.level, pos);
        if (chunk == null) {
            return false;
        }

        boolean canJump = this.noCollisionAbove(chunk, pos);
        return canTravelTo(pos.east(), canJump) || canTravelTo(pos.west(), canJump) || canTravelTo(pos.north(), canJump) || canTravelTo(pos.south(), canJump);
    }

    private boolean canTravelTo(BlockPos pos, boolean canJump) {
        LevelChunk chunk = ChunkManager.getChunkIfLoaded(this.level, pos);
        if (chunk == null) {
            return false;
        }

        Block bottom = chunk.getBlockState(pos).getBlock();
        if (bottom instanceof BedBlock) {
            // Allows iron farms to function normally
            return true;
        }

        Block top = chunk.getBlockState(pos.above()).getBlock();
        if (top.hasCollision) {
            return false;
        }

        // The villager can only jump if:
        // - There is no collision above the villager
        // - There is no collision above the top block
        // - The bottom block is short enough to jump on
        boolean isTallBlock = bottom instanceof FenceBlock || bottom instanceof FenceGateBlock || bottom instanceof WallBlock;
        return !bottom.hasCollision || (canJump && !isTallBlock && this.noCollisionAbove(chunk, pos));
    }

    // Checks for collisions 2 blocks above the given position
    private boolean noCollisionAbove(LevelChunk chunk, BlockPos pos) {
        return !chunk.getBlockState(pos.above(2)).getBlock().hasCollision;
    }
}
