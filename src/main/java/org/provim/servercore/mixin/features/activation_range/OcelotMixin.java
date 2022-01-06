package org.provim.servercore.mixin.features.activation_range;

import net.minecraft.world.entity.animal.Ocelot;
import org.objectweb.asm.Opcodes;
import org.provim.servercore.interfaces.activation_range.ActivationEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Ocelot.class)
public abstract class OcelotMixin {

    @Redirect(method = "removeWhenFarAway", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/world/entity/animal/Ocelot;tickCount:I"))
    private int fixOcelotDespawning(Ocelot ocelot) {
        return ((ActivationEntity) ocelot).getFullTickCount();
    }
}
