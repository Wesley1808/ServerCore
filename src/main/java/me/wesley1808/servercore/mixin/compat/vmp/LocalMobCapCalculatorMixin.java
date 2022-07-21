package me.wesley1808.servercore.mixin.compat.vmp;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import me.wesley1808.servercore.common.interfaces.compat.ILocalMobCapCalculator;
import me.wesley1808.servercore.common.interfaces.compat.IMobCounts;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.LocalMobCapCalculator;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = LocalMobCapCalculator.class, priority = 2000)
public abstract class LocalMobCapCalculatorMixin implements ILocalMobCapCalculator {
    @Dynamic
    @Shadow
    private Int2ObjectOpenHashMap<LocalMobCapCalculator.MobCounts> playersIdToDensityCap;

    @Override
    public IMobCounts getMobCounts(ServerPlayer player, LocalMobCapCalculator.MobCounts defaultValue) {
        return (IMobCounts) this.playersIdToDensityCap.getOrDefault(player.getId(), defaultValue);
    }
}
