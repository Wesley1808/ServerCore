package org.provim.servercore.mixin.features.misc;

import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundMoveVehiclePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import org.provim.servercore.config.tables.FeatureConfig;
import org.provim.servercore.utils.ChunkManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Collections;
import java.util.Set;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {

    @Shadow
    @Final
    public Connection connection;

    @Shadow
    public ServerPlayer player;

    @Shadow
    public abstract void teleport(double d, double e, double f, float g, float h, Set<ClientboundPlayerPositionPacket.RelativeArgument> set, boolean bl);

    // Paper - Prevent moving into unloaded chunks
    @Inject(method = "handleMoveVehicle", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", shift = At.Shift.AFTER, ordinal = 0, target = "Lnet/minecraft/world/phys/Vec3;lengthSqr()D"), cancellable = true)
    private void handleMoveVehicle(ServerboundMoveVehiclePacket packet, CallbackInfo ci, Entity entity, ServerLevel serverLevel, double d, double e, double f, double g, double h, double i, float j, float k, double l, double m, double n) {
        if (FeatureConfig.PREVENT_MOVING_INTO_UNLOADED_CHUNKS.get() && !ChunkManager.isChunkLoaded(serverLevel, (int) Math.floor(packet.getX()) >> 4, (int) Math.floor(packet.getZ()) >> 4)) {
            this.connection.send(new ClientboundMoveVehiclePacket(entity));
            ci.cancel();
        }
    }

    // Paper - Prevent moving into unloaded chunks
    @Inject(method = "handleMovePlayer", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", shift = At.Shift.BEFORE, ordinal = 0, target = "Lnet/minecraft/server/level/ServerPlayer;isChangingDimension()Z"), cancellable = true)
    private void handleMovePlayer(ServerboundMovePlayerPacket packet, CallbackInfo ci, ServerLevel serverLevel, double d, double e, double f, float g, float h, double x, double y, double z, double l, double m, double n, double o, double p, double q, int r) {
        final double toX = packet.getX(x);
        final double toZ = packet.getZ(z);
        if (FeatureConfig.PREVENT_MOVING_INTO_UNLOADED_CHUNKS.get() && (x != toX || z != toZ) && !ChunkManager.isChunkLoaded(serverLevel, (int) Math.floor(toX) >> 4, (int) Math.floor(toZ) >> 4)) {
            this.teleport(x, y, z, this.player.getYRot(), this.player.getXRot(), Collections.emptySet(), true);
            ci.cancel();
        }
    }
}
