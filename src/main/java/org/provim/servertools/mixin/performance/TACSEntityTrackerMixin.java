package org.provim.servertools.mixin.performance;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(targets = "net.minecraft.server.world.ThreadedAnvilChunkStorage$EntityTracker")
abstract class TACSEntityTrackerMixin {
    @Shadow
    @Final
    Entity entity;
    @Shadow
    @Final
    private int maxDistance;

    @Shadow
    protected abstract int adjustTrackingDistance(int initialDistance);

    /**
     * @author Wesley1808
     * @Reason Replace stream code and remove useless iterations.
     */

    @Overwrite
    private int getMaxTrackDistance() {
        int trackDistance = this.maxDistance;
        for (Entity passenger : this.entity.getPassengerList()) {
            trackDistance = Math.max(trackDistance, passenger.getType().getMaxTrackDistance() * 16);
        }
        return this.adjustTrackingDistance(trackDistance);
    }
}