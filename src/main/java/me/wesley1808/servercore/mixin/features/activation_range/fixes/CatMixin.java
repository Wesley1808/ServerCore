package me.wesley1808.servercore.mixin.features.activation_range.fixes;

import net.minecraft.world.entity.animal.Cat;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Cat.class)
public class CatMixin {

    @Redirect(
            method = "removeWhenFarAway",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/entity/animal/Cat;tickCount:I",
                    opcode = Opcodes.GETFIELD
            )
    )
    private int servercore$fixCatDespawning(Cat cat) {
        return cat.getFullTickCount();
    }
}
