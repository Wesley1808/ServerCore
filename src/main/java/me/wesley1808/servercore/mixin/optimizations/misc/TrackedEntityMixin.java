package me.wesley1808.servercore.mixin.optimizations.misc;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net/minecraft/server/level/ChunkMap$TrackedEntity")
public abstract class TrackedEntityMixin {

    // Avoid stream allocations and unnecessary iterations.
    @Redirect(
            method = "getEffectiveRange",
            require = 0,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;getIndirectPassengers()Ljava/lang/Iterable;"
            )
    )
    private Iterable<Entity> getPassengers(Entity entity) {
        return entity.getPassengers();
    }
}