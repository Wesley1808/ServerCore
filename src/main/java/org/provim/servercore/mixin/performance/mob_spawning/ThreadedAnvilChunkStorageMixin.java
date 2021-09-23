package org.provim.servercore.mixin.performance.mob_spawning;

import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import org.provim.servercore.interfaces.IThreadedAnvilChunkStorage;
import org.provim.servercore.utils.PlayerMobDistanceMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThreadedAnvilChunkStorage.class)
public abstract class ThreadedAnvilChunkStorageMixin implements IThreadedAnvilChunkStorage {
    private PlayerMobDistanceMap distanceMap = new PlayerMobDistanceMap();

    @Inject(method = "save(Z)V", at = @At("TAIL"))
    private void resetDistanceMap(boolean flush, CallbackInfo ci) {
        // Resets the PlayerMobDistanceMap whenever the worlds save.
        // The PlayerMobDistanceMap may rarely fail to remove a player object from the HashSet.
        // This causes areas affected by this to be unable to spawn any mobs.
        this.distanceMap = new PlayerMobDistanceMap();
    }

    @Override
    public PlayerMobDistanceMap getDistanceMap() {
        return this.distanceMap;
    }
}
