package me.wesley1808.servercore.mixin.features.activation_range.inactive_ticks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Bee.class)
public abstract class BeeMixin extends Animal {
    @Shadow
    @Nullable
    BlockPos hivePos;

    protected BeeMixin(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow
    abstract boolean isHiveValid();

    @Override
    public void servercore$inactiveTick() {
        if (this.servercore$getFullTickCount() % 20 == 0 && !this.isHiveValid()) {
            this.hivePos = null;
        }

        super.servercore$inactiveTick();
    }
}
