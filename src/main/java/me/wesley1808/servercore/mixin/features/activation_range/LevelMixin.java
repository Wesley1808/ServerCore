package me.wesley1808.servercore.mixin.features.activation_range;

import me.wesley1808.servercore.common.activation_range.ActivationType;
import me.wesley1808.servercore.common.interfaces.activation_range.LevelInfo;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/**
 * Based on: Paper (Entity-Activation-Range-2.0.patch)
 * Patch Author: Aikar (aikar@aikar.co)
 * License: GPL-3.0 (licenses/GPL.md)
 */

@Mixin(Level.class)
public class LevelMixin implements LevelInfo {
    @Unique
    private final int[] remaining = new int[ActivationType.Wakeup.values().length];

    @Override
    public int getRemaining(ActivationType.Wakeup key) {
        return this.remaining[key.ordinal()];
    }

    @Override
    public void setRemaining(ActivationType.Wakeup key, int value) {
        this.remaining[key.ordinal()] = value;
    }
}
