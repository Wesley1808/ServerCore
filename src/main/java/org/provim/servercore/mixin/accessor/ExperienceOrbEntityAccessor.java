package org.provim.servercore.mixin.accessor;

import net.minecraft.entity.ExperienceOrbEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ExperienceOrbEntity.class)
public interface ExperienceOrbEntityAccessor {

    @Accessor("pickingCount")
    int getPickingCount();

    @Accessor("orbAge")
    int getOrbAge();
}
