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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EndGatewayBlock;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import net.minecraft.world.level.portal.Object;
import one.pkg.libsl.api.Vec3d;
import one.pkg.libsl.api.event.entity.ServerPlayerEvents;
import one.pkg.libsl.api.instance.AsEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.w3c.dom.Entity;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin implements Entity {
    @Shadow
    private String language;

    @Shadow
    public abstract ServerLevel level();

    @Inject(method = "updateOptions", at = @At("HEAD"))
    private void libsl$updateOptions(net.minecraft.network.protocol.game.ServerboundClientInformationPacket packet, CallbackInfo ci) {
        ServerPlayerEvents.CLIENT_OPTIONS_CHANGED.invoker().onClientOptionsChanged(
                (ServerPlayer) (Object) this,
                packet
        );
        if ((this.language == null || !this.language.equals(packet.language())))
            ServerPlayerEvents.LANG_CHANGED.invoker().onLangChanged((ServerPlayer) (Object) this,
                    packet.language());
    }

    @Inject(
            method = "teleport(Lnet/minecraft/world/level/portal/Object;)Lnet/minecraft/server/level/ServerPlayer;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/portal/Object;asPassenger()Z",
                    shift = At.Shift.BEFORE
            ),
            cancellable = true
    )
    private void libsl$teleport(
            Object transition, CallbackInfoReturnable<ServerPlayer> cir,
            @Local(name = "newLevel") ServerLevel newLevel
    ) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        AsEntity asPlayerEntity = (AsEntity) player;
        Vec3d pos = asPlayerEntity.getPos();

        PositionMoveRotation absolutePosition = PositionMoveRotation.calculateAbsolute(
                PositionMoveRotation.of(player), PositionMoveRotation.of(transition), transition.relatives());
        Vec3d newPos = new Vec3d(absolutePosition.position(), newLevel,
                absolutePosition.yRot(), absolutePosition.xRot());

        boolean result = (asPlayerEntity.getPortalProcessor() != null &&
                asPlayerEntity.getPortalProcessor().isSamePortal(((EndGatewayBlock) Blocks.END_GATEWAY)) &&
                level().getBlockEntity(asPlayerEntity.getPortalProcessor().getEntryPosition())
                        instanceof TheEndGatewayBlockEntity blockEntity) ?
                ServerPlayerEvents.END_GATEWAY.invoker().onEndGatewayTeleport(player, pos, newPos, blockEntity) :
                ServerPlayerEvents.TELEPORT.invoker().onTeleport(player, pos, newPos);

        if (!result)
            cir.setReturnValue(null);
    }
}
