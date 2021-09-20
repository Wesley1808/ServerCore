package org.provim.servercore.mixin.performance.mob_spawning;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.provim.servercore.interfaces.IServerPlayerEntity;
import org.provim.servercore.utils.PooledHashSets;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin implements IServerPlayerEntity {
    private PooledHashSets.PooledObjectLinkedOpenHashSet<ServerPlayerEntity> cachedSingleMobDistanceMap;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(MinecraftServer minecraftServer, ServerWorld serverWorld, GameProfile gameProfile, CallbackInfo ci) {
        this.cachedSingleMobDistanceMap = new PooledHashSets.PooledObjectLinkedOpenHashSet<>((ServerPlayerEntity) (Object) this);
    }

    @Override
    public PooledHashSets.PooledObjectLinkedOpenHashSet<ServerPlayerEntity> getCachedSingleMobDistanceMap() {
        return cachedSingleMobDistanceMap;
    }
}
