package org.provim.servercore.mixin.performance;

import net.minecraft.server.world.ChunkHolder;
import org.provim.servercore.interfaces.ChunkHolderInterface;
import org.provim.servercore.utils.TickUtils;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ChunkHolder.class)
public class ChunkHolderMixin implements ChunkHolderInterface {
    private boolean active = false;

    @Override
    public boolean isActive() {
        return !TickUtils.isActive() || active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }
}
