package org.provim.servercore.mixin.accessor;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ServerPlayerEntity.class)
public interface ServerPlayerEntityAccessor {

    @Invoker("moveToSpawn")
    void movePlayerToSpawn(ServerWorld world);
}
