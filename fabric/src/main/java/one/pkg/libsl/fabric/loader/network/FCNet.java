/*
 * Copyright (C) 2026  404Setup.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package one.pkg.libsl.fabric.loader.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import one.pkg.libsl.api.network.CNetHandler;
import one.pkg.libsl.api.network.ICNet;
import one.pkg.libsl.api.network.NetSrc;
import org.jetbrains.annotations.NotNull;

public class FCNet implements ICNet {
    FCNet() {
    }

    @Override
    public void send(@NotNull CustomPacketPayload payload, CustomPacketPayload... payloads) {
        if (canSend(payload)) ClientPlayNetworking.send(payload);
        if (payloads != null) {
            for (CustomPacketPayload payloadN : payloads) {
                if (canSend(payloadN)) ClientPlayNetworking.send(payloadN);
            }
        }
    }

    @SuppressWarnings("all")
    @Override
    public <T extends CustomPacketPayload> void registerGlobalReceiver(
            CustomPacketPayload.Type<T> id,
            CNetHandler handler,
            NetSrc.Direction direction
    ) {
        if (direction == NetSrc.Direction.CONFIGURATION)
            ClientConfigurationNetworking.registerGlobalReceiver(id, (payload, ctx) -> {
                ctx.client().execute(handler.handle(ctx.client(), payload));
            });
        else ClientPlayNetworking.registerGlobalReceiver(id, (payload, ctx) -> {
            ctx.client().execute(handler.handle(ctx.client(), payload));
        });
    }

    @Override
    public <T extends CustomPacketPayload> boolean isRegistered(
            CustomPacketPayload.Type<T> id) {
        return ClientPlayNetworking.canSend(id);
    }

    @Override
    public boolean canSend(Identifier channelName) {
        return ClientPlayNetworking.canSend(channelName);
    }

    @Override
    public boolean canSend(CustomPacketPayload.Type<?> type) {
        return ClientPlayNetworking.canSend(type);
    }

    @Override
    public boolean canSend(CustomPacketPayload payload) {
        return ClientPlayNetworking.canSend(payload.type());
    }
}
