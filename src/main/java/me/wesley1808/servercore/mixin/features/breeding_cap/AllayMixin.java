package me.wesley1808.servercore.mixin.features.breeding_cap;

import me.wesley1808.servercore.common.utils.BreedingCap;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Allay.class)
public abstract class AllayMixin extends PathfinderMob {
    private AllayMixin(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow
    protected abstract void resetDuplicationCooldown();

    @Inject(method = "duplicateAllay", at = @At("HEAD"), cancellable = true)
    public void servercore$enforceBreedCap(CallbackInfo ci) {
        if (BreedingCap.ANIMAL.exceedsLimit(this)) {
            this.resetDuplicationCooldown();
            ci.cancel();
        }
    }
}
