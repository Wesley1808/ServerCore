package me.wesley1808.servercore.mixin.features.misc;

import com.llamalad7.mixinextras.sugar.Local;
import me.wesley1808.servercore.common.config.Config;
import me.wesley1808.servercore.common.utils.ChunkManager;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundMoveVehiclePacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Based on: Paper (Add-option-to-prevent-players-from-moving-into-unloaded-chunks.patch)
 * <p>
 * Patch Author: Gabriele C (sgdc3.mail@gmail.com)
 * <br>
 * License: GPL-3.0 (licenses/GPL.md)
 */
@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin extends ServerCommonPacketListenerImpl {
    @Shadow
    public ServerPlayer player;

    public ServerGamePacketListenerImplMixin(MinecraftServer minecraftServer, Connection connection, CommonListenerCookie cookie) {
        super(minecraftServer, connection, cookie);
    }

    @Shadow
    public abstract void teleport(double x, double y, double z, float yaw, float pitch);

    @Inject(
            method = "handleMoveVehicle",
            cancellable = true,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/phys/Vec3;lengthSqr()D",
                    shift = At.Shift.AFTER,
                    ordinal = 0
            )
    )
    private void servercore$handleMoveVehicle(
            ServerboundMoveVehiclePacket packet, CallbackInfo ci,
            @Local(name = "level") ServerLevel level,
            @Local(name = "vehicle") Entity vehicle,
            @Local(name = "oldX") double oldX,
            @Local(name = "oldZ") double oldZ,
            @Local(name = "targetX") double targetX,
            @Local(name = "targetY") double targetY,
            @Local(name = "targetZ") double targetZ
    ) {
        if (this.servercore$shouldPreventMovement(level, vehicle, oldX, oldZ, targetX, targetY, targetZ)) {
            this.connection.send(ClientboundMoveVehiclePacket.fromEntity(vehicle));
            ci.cancel();
        }
    }

    @Inject(
            method = "handleMovePlayer",
            cancellable = true,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerPlayer;move(Lnet/minecraft/world/entity/MoverType;Lnet/minecraft/world/phys/Vec3;)V",
                    ordinal = 0
            )
    )
    private void servercore$handleMovePlayer(
            ServerboundMovePlayerPacket packet, CallbackInfo ci,
            @Local(name = "level") ServerLevel level,
            @Local(name = "targetYRot") float targetYRot,
            @Local(name = "targetYRot") float targetXRot,
            @Local(name = "targetX") double targetX,
            @Local(name = "targetY") double targetY,
            @Local(name = "targetZ") double targetZ,
            @Local(name = "startX") double startX,
            @Local(name = "startY") double startY,
            @Local(name = "startZ") double startZ
    ) {
        if (this.servercore$shouldPreventMovement(level, this.player, startX, startZ, targetX, targetY, targetZ)) {
            this.teleport(startX, startY, startZ, targetYRot, targetXRot);
            ci.cancel();
        }
    }

    @Unique
    private boolean servercore$shouldPreventMovement(ServerLevel level, Entity entity, double fromX, double fromZ, double toX, double toY, double toZ) {
        return Config.get().features().preventMovingIntoUnloadedChunks()
               && (fromX != toX || fromZ != toZ)
               && !ChunkManager.areChunksLoadedForMove(level, entity.getBoundingBox().expandTowards(new Vec3(toX, toY, toZ).subtract(entity.position())));
    }
}
