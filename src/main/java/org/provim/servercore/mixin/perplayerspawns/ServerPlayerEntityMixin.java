package org.provim.servercore.mixin.perplayerspawns;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.provim.servercore.interfaces.IServerPlayerEntity;
import org.provim.servercore.utils.patches.PooledHashSets;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * From: PaperMC (implement-optional-per-player-mob-spawns.patch)
 * License: GPL-3.0 (licenses/GPL.md)
 */

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin implements IServerPlayerEntity {
    int[] mobCounts = new int[SpawnGroup.values().length];

    PooledHashSets.PooledObjectLinkedOpenHashSet<ServerPlayerEntity> cachedSingleMobDistanceMap;

    @Override
    public PooledHashSets.PooledObjectLinkedOpenHashSet<ServerPlayerEntity> getCachedSingleMobDistanceMap() {
        return cachedSingleMobDistanceMap;
    }

    @Override
    public int[] getMobCounts() {
        return mobCounts;
    }

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    public void init(MinecraftServer server, ServerWorld world, GameProfile profile, CallbackInfo ci) {
        this.cachedSingleMobDistanceMap = new PooledHashSets.PooledObjectLinkedOpenHashSet<>((ServerPlayerEntity) (Object) this);
    }
}