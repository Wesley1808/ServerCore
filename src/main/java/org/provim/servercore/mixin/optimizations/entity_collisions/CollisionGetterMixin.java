package org.provim.servercore.mixin.optimizations.entity_collisions;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.phys.AABB;
import org.provim.servercore.interfaces.CollisionEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * From: PaperMC (Optimize-Collision-to-not-load-chunks.patch)
 * License: GPL-3.0 (licenses/GPL.md)
 */

@Mixin(CollisionGetter.class)
public interface CollisionGetterMixin {

    @Inject(method = "noCollision(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;)Z", at = @At("HEAD"))
    private void enableCollisionChunkLoading(Entity entity, AABB collisionBox, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof CollisionEntity collisionEntity) {
            collisionEntity.setCollisionLoadChunks(true);
        }
    }

    @Inject(method = "noCollision(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;)Z", at = @At("RETURN"))
    private void disableCollisionChunkLoading(Entity entity, AABB collisionBox, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof CollisionEntity collisionEntity) {
            collisionEntity.setCollisionLoadChunks(false);
        }
    }
}