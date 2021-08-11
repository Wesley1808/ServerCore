package org.provim.servercore.mixin.performance;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net.minecraft.server.world.ThreadedAnvilChunkStorage$EntityTracker")
public abstract class TACSEntityTrackerMixin {

    // Avoid stream code.
    @Redirect(method = "getMaxTrackDistance()I", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getPassengersDeep()Ljava/lang/Iterable;"))
    private Iterable<Entity> getMaxTrackDistance(Entity entity) {
        return entity.getPassengerList();
    }
}