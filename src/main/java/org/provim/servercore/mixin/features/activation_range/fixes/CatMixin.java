package org.provim.servercore.mixin.features.activation_range.fixes;

import net.minecraft.world.entity.animal.Cat;
import org.objectweb.asm.Opcodes;
import org.provim.servercore.interfaces.activation_range.ActivationEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Cat.class)
public abstract class CatMixin {

    @Redirect(method = "removeWhenFarAway", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/world/entity/animal/Cat;tickCount:I"))
    private int fixCatDespawning(Cat cat) {
        return ((ActivationEntity) cat).getFullTickCount();
    }
}
