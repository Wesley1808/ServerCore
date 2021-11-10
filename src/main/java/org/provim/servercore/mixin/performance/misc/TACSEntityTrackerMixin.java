package org.provim.servercore.mixin.performance.misc;

import net.minecraft.server.level.ChunkMap;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChunkMap.TrackedEntity.class)
public abstract class TACSEntityTrackerMixin {

    // Avoid stream allocations.
    @Redirect(method = "getEffectiveRange", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getIndirectPassengers()Ljava/lang/Iterable;"))
    private Iterable<Entity> getPassengers(Entity entity) {
        return entity.getPassengers();
    }
}