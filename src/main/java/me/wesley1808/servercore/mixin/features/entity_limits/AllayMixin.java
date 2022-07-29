package me.wesley1808.servercore.mixin.features.entity_limits;

import me.wesley1808.servercore.common.config.tables.EntityLimitConfig;
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
    @Shadow
    protected abstract void resetDuplicationCooldown();

    private AllayMixin(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "duplicateAllay", at = @At("HEAD"), cancellable = true)
    public void servercore$cancelAllayDuplication(CallbackInfo ci) {
        if (BreedingCap.exceedsLimit(this, EntityLimitConfig.ANIMAL_COUNT.get(), EntityLimitConfig.ANIMAL_RANGE.get())) {
            this.resetDuplicationCooldown();
            ci.cancel();
        }
    }
}
