package me.wesley1808.servercore.mixin.optimizations.ticking;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Marker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.EntityCallbacks.class)
public abstract class EntityCallbacksMixin {

    // Prevents markers from being added to the entity tick list.
    // Markers have no tick logic, but still need to be iterated over every tick.
    @Inject(method = "onTickingStart(Lnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"), cancellable = true)
    private void cancelMarkerTicks(Entity entity, CallbackInfo ci) {
        if (entity instanceof Marker) {
            ci.cancel();
        }
    }
}
