package org.provim.servercore.mixin.optimizations.entity_collisions;

import net.minecraft.world.entity.Entity;
import org.provim.servercore.interfaces.CollisionEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Entity.class)
public abstract class EntityMixin implements CollisionEntity {
    private boolean collisionLoadChunks = false;

    @Override
    public void setCollisionLoadChunks(boolean collisionLoadChunks) {
        this.collisionLoadChunks = collisionLoadChunks;
    }

    @Override
    public boolean shouldCollisionLoadChunks() {
        return this.collisionLoadChunks;
    }
}