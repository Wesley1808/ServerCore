package me.wesley1808.servercore.mixin.features.activation_range.fixes;

import net.minecraft.world.entity.animal.Ocelot;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Ocelot.class)
public class OcelotMixin {

    @Redirect(
            method = "removeWhenFarAway",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/entity/animal/Ocelot;tickCount:I",
                    opcode = Opcodes.GETFIELD
            )
    )
    private int servercore$fixOcelotDespawning(Ocelot ocelot) {
        return ocelot.servercore$getFullTickCount();
    }
}
