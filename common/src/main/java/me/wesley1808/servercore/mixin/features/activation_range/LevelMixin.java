package me.wesley1808.servercore.mixin.features.activation_range;

import me.wesley1808.servercore.common.activation_range.ActivationType;
import me.wesley1808.servercore.common.interfaces.activation_range.LevelInfo;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/**
 * Based on: Paper (Entity-Activation-Range-2.0.patch)
 * <p>
 * Patch Author: Aikar (aikar@aikar.co)
 * <br>
 * License: GPL-3.0 (licenses/GPL.md)
 */
@Mixin(Level.class)
public class LevelMixin implements LevelInfo {
    @Unique
    private final int[] servercore$remaining = new int[ActivationType.Wakeup.values().length];

    @Override
    public int servercore$getRemaining(ActivationType.Wakeup key) {
        return this.servercore$remaining[key.ordinal()];
    }

    @Override
    public void servercore$setRemaining(ActivationType.Wakeup key, int value) {
        this.servercore$remaining[key.ordinal()] = value;
    }
}
