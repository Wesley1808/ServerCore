package me.wesley1808.servercore.mixin.features.spawn_chunks;

import me.wesley1808.servercore.common.config.tables.FeatureConfig;
import me.wesley1808.servercore.common.utils.ChunkManager;
import net.minecraft.network.Connection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public abstract class PlayerListMixin {
    @Shadow
    @Final
    private MinecraftServer server;

    @Inject(method = "placeNewPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;invalidateStatus()V"))
    private void servercore$disableSpawnChunks(Connection connection, ServerPlayer player, CallbackInfo ci) {
        // Disable spawn chunks after the player joins.
        // This is only used on singleplayer worlds that aren't published.
        if (!this.server.isPublished() && FeatureConfig.DISABLE_SPAWN_CHUNKS.get()) {
            ChunkManager.disableSpawnChunks(this.server);
        }
    }
}
