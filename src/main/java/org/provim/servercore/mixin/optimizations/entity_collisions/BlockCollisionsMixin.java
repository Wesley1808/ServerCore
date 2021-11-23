package org.provim.servercore.mixin.optimizations.entity_collisions;

import com.google.common.collect.AbstractIterator;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.*;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.provim.servercore.config.tables.FeatureConfig;
import org.provim.servercore.interfaces.CollisionEntity;
import org.provim.servercore.utils.ChunkManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/**
 * From: PaperMC (Optimize-Collision-to-not-load-chunks.patch)
 * License: GPL-3.0 (licenses/GPL.md)
 */

@Mixin(BlockCollisions.class)
public abstract class BlockCollisionsMixin extends AbstractIterator<VoxelShape> {

    @Shadow
    @Final
    private CollisionGetter collisionGetter;

    @Shadow
    @Final
    private BlockPos.MutableBlockPos pos;

    @Shadow
    @Final
    private CollisionContext context;

    @Unique
    private Entity entity;

    @Unique
    private boolean isFar;

    @Unique
    private boolean isNull;

    @Shadow
    @Nullable
    protected abstract BlockGetter getChunk(int i, int j);

    @Redirect(method = "computeNext()Lnet/minecraft/world/phys/shapes/VoxelShape;", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos$MutableBlockPos;set(III)Lnet/minecraft/core/BlockPos$MutableBlockPos;"))
    private BlockPos.MutableBlockPos cancelOp(BlockPos.MutableBlockPos pos, int x, int y, int z) {
        return pos;
    }

    @Inject(method = "computeNext()Lnet/minecraft/world/phys/shapes/VoxelShape;", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", shift = At.Shift.BEFORE, target = "Lnet/minecraft/world/level/BlockCollisions;getChunk(II)Lnet/minecraft/world/level/BlockGetter;"))
    private void prepareVariables(CallbackInfoReturnable<VoxelShape> cir, int x, int y, int z, int l) {
        this.entity = this.context instanceof EntityCollisionContext entityContext ? entityContext.getEntity() : null;
        this.isFar = this.entity != null && this.entity.distanceToSqr(x, this.entity.getY(), z) > 14;
        this.pos.set(x, y, z);
    }

    @Redirect(method = "computeNext()Lnet/minecraft/world/phys/shapes/VoxelShape;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/BlockCollisions;getChunk(II)Lnet/minecraft/world/level/BlockGetter;"))
    private BlockGetter onlyComputeIfLoaded(BlockCollisions collisions, int x, int z) {
        BlockGetter blockGetter;
        if (this.collisionGetter instanceof WorldGenRegion) {
            this.isNull = false;
            return this.getChunk(x, z);
        } else if ((!this.isFar && this.entity instanceof ServerPlayer) || (this.entity instanceof CollisionEntity collisionEntity && collisionEntity.shouldCollisionLoadChunks())) {
            blockGetter = this.getChunk(x, z);
        } else {
            blockGetter = this.getChunkIfLoaded(x >> 4, z >> 4);
        }

        this.isNull = blockGetter == null;
        return blockGetter;
    }

    @Inject(method = "computeNext()Lnet/minecraft/world/phys/shapes/VoxelShape;", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/world/level/BlockCollisions;getChunk(II)Lnet/minecraft/world/level/BlockGetter;"), cancellable = true)
    private void onPostGetChunk(CallbackInfoReturnable<VoxelShape> cir, int x, int y, int z, int l) {
        if (this.isNull) {
            if (!(this.entity instanceof ServerPlayer) || FeatureConfig.PREVENT_MOVING_INTO_UNLOADED_CHUNKS.get()) {
                cir.setReturnValue(Shapes.create(this.isFar ? this.entity.getBoundingBox() : new AABB(new BlockPos(x, y, z))));
            }
        }
    }

    private BlockGetter getChunkIfLoaded(int chunkX, int chunkZ) {
        if (this.collisionGetter instanceof Level level) {
            return ChunkManager.getChunkIfLoaded(level, chunkX, chunkZ);
        } else if (this.collisionGetter instanceof PathNavigationRegion region) {
            return ChunkManager.getChunkIfLoaded(region.level, chunkX, chunkZ);
        }
        return null; // Should never happen
    }
}
