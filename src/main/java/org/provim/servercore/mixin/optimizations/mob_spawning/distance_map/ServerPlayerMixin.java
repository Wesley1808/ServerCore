package org.provim.servercore.mixin.optimizations.mob_spawning.distance_map;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.provim.servercore.interfaces.IServerPlayer;
import org.provim.servercore.utils.data.PooledHashSets;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * From: PaperMC (implement-optional-per-player-mob-spawns.patch)
 * License: GPL-3.0 (licenses/GPL.md)
 */

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin implements IServerPlayer {
    @Unique
    private PooledHashSets.PooledObjectLinkedOpenHashSet<ServerPlayer> cachedSingleMobDistanceMap;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(MinecraftServer minecraftServer, ServerLevel serverLevel, GameProfile gameProfile, CallbackInfo ci) {
        this.cachedSingleMobDistanceMap = new PooledHashSets.PooledObjectLinkedOpenHashSet<>((ServerPlayer) (Object) this);
    }

    @Override
    public PooledHashSets.PooledObjectLinkedOpenHashSet<ServerPlayer> getCachedSingleMobDistanceMap() {
        return this.cachedSingleMobDistanceMap;
    }
}
