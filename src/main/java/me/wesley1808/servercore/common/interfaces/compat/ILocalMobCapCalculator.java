package me.wesley1808.servercore.common.interfaces.compat;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.LocalMobCapCalculator;

public interface ILocalMobCapCalculator {
    IMobCounts getMobCounts(ServerPlayer player, LocalMobCapCalculator.MobCounts defaultValue);
}
