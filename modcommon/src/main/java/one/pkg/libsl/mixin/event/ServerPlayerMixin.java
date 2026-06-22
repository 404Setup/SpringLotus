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


}
