/*
 * Copyright (C) 2026  404Setup.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package one.pkg.libsl.mixin.event;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.RelativeMovement;
import one.pkg.libsl.api.Vec3d;
import one.pkg.libsl.api.event.entity.ServerPlayerEvents;
import one.pkg.libsl.api.instance.AsEntity;
import one.pkg.libsl.api.instance.AsServerGamePacketListenerImpl;
import one.pkg.libsl.mixin.access.ServerboundMovePlayerPacketAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;


@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin implements AsServerGamePacketListenerImpl {
    @Shadow
    public ServerPlayer player;

    @Unique
    private double springLotus$lastPosX = Double.MAX_VALUE;

    @Unique
    private double springLotus$lastPosY = Double.MAX_VALUE;

    @Unique
    private double springLotus$lastPosZ = Double.MAX_VALUE;

    @Unique
    private float springLotus$lastYaw = Float.MAX_VALUE;

    @Unique
    private float springLotus$lastPitch = Float.MAX_VALUE;

    @Shadow
    public void teleport(double x, double y, double z, float yRot, float xRot) {
    }

    @Inject(
            method = "handleMovePlayer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerPlayer;absMoveTo(DDDFF)V",
                    shift = At.Shift.BEFORE,
                    ordinal = 1
            ),
            cancellable = true
    )
    private void libsl$handleMovePlayer(
            CallbackInfo ci,
            @Local(argsOnly = true) ServerboundMovePlayerPacket packet
    ) {
        double toX = player.getX();
        double toY = player.getY();
        double toZ = player.getZ();
        float toYaw = player.getYRot();
        float toPitch = player.getXRot();

        ServerboundMovePlayerPacketAccess access = (ServerboundMovePlayerPacketAccess) packet;

        if (packet.hasPosition()) {
            toX = access.getX();
            toY = access.getY();
            toZ = access.getZ();
        }

        if (packet.hasRotation()) {
            toYaw = access.getYRot();
            toPitch = access.getXRot();
        }

        double delta = Mth.square(this.springLotus$lastPosX - toX) +
                Mth.square(this.springLotus$lastPosY - toY) + Mth.square(this.springLotus$lastPosZ - toZ);
        float deltaAngle = Mth.abs(this.springLotus$lastYaw - toYaw) +
                Mth.abs(this.springLotus$lastPitch - toPitch);

        if ((delta > 1f / 256 || deltaAngle > 10f) && !this.player.isSleeping() && !this.player.isDeadOrDying()) {
            double fromX = this.springLotus$lastPosX;
            double fromY = this.springLotus$lastPosY;
            double fromZ = this.springLotus$lastPosZ;
            float fromYaw = this.springLotus$lastYaw;
            float fromPitch = this.springLotus$lastPitch;

            this.springLotus$lastPosX = toX;
            this.springLotus$lastPosY = toY;
            this.springLotus$lastPosZ = toZ;
            this.springLotus$lastYaw = toYaw;
            this.springLotus$lastPitch = toPitch;

            if (!ServerPlayerEvents.MOVE.canSkip()) {
                Vec3d from = new Vec3d(fromX, fromY, fromZ, fromYaw, fromPitch, player.level());
                Vec3d to = new Vec3d(toX, toY, toZ, toYaw, toPitch, player.level());
                if (!ServerPlayerEvents.MOVE.invoker().onMove(player, from, to)) {
                    this.teleport(fromX, fromY, fromZ, fromYaw, fromPitch);
                    ci.cancel();
                }
            }
        }
    }

    @Inject(
            method = "handleMoveVehicle",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerChunkCache;move(Lnet/minecraft/server/level/ServerPlayer;)V",
                    shift = At.Shift.BEFORE
            ),
            cancellable = true)
    private void libsl$handleMoveVehicle(
            CallbackInfo ci,
            @Local(argsOnly = true) ServerboundMoveVehiclePacket packet
    ) {
        double toX = packet.getX();
        double toY = packet.getY();
        double toZ = packet.getZ();
        float toYaw = packet.getYRot();
        float toPitch = packet.getXRot();

        double delta = Mth.square(this.springLotus$lastPosX - toX) +
                Mth.square(this.springLotus$lastPosY - toY) + Mth.square(this.springLotus$lastPosZ - toZ);
        float deltaAngle = Mth.abs(this.springLotus$lastYaw - toYaw) +
                Mth.abs(this.springLotus$lastPitch - toPitch);

        if ((delta > 1f / 256 || deltaAngle > 10f) && !this.player.isSleeping() && !this.player.isDeadOrDying()) {
            double fromX = this.springLotus$lastPosX;
            double fromY = this.springLotus$lastPosY;
            double fromZ = this.springLotus$lastPosZ;
            float fromYaw = this.springLotus$lastYaw;
            float fromPitch = this.springLotus$lastPitch;

            this.springLotus$lastPosX = toX;
            this.springLotus$lastPosY = toY;
            this.springLotus$lastPosZ = toZ;
            this.springLotus$lastYaw = toYaw;
            this.springLotus$lastPitch = toPitch;

            if (!ServerPlayerEvents.MOVE.canSkip()) {
                Vec3d from = new Vec3d(fromX, fromY, fromZ, fromYaw, fromPitch, this.player.level());
                Vec3d to = new Vec3d(toX, toY, toZ, toYaw, toPitch, this.player.level());

                if (!ServerPlayerEvents.MOVE.invoker().onMove(player, from, to)) {
                    this.teleport(fromX, fromY, fromZ, fromYaw, fromPitch);
                    ci.cancel();
                }
            }
        }
    }

    @Override
    public void teleport(Vec3d pos) {
        teleport(pos.getX(), pos.getY(), pos.getZ(), pos.getYaw(), pos.getPitch());
    }

    @Inject(
            method = "teleport(DDDFFLjava/util/Set;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void libsl$teleport(double x, double y, double z, float yaw, float pitch, Set<RelativeMovement> relatives, CallbackInfo ci) {
        if (ServerPlayerEvents.TELEPORT.canSkip()) return;

        ServerPlayer player = this.player;
        AsEntity asPlayerEntity = (AsEntity) player;
        Vec3d current = asPlayerEntity.getPos();

        Vec3d to = new Vec3d(x, y, z, yaw, pitch, this.player.level());
        if (current.equals(to)) return;
        if (!ServerPlayerEvents.TELEPORT.invoker().onTeleport(player, current, to)) ci.cancel();
    }
}
