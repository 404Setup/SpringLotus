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

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import one.pkg.libsl.api.network.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FNet implements INet {
    private final ICNet net = new FCNet();

    @Override
    public ICNet client() {
        return net;
    }

    @Override
    public boolean canSend(ServerPlayer player, Identifier channelName) {
        return ServerPlayNetworking.canSend(player, channelName);
    }

    @Override
    public boolean canSend(ServerPlayer player, CustomPacketPayload payload) {
        return ServerPlayNetworking.canSend(player, payload.type());
    }

    @Override
    public boolean canSend(@NotNull ServerPlayer player, CustomPacketPayload.Type<?> type) {
        return ServerPlayNetworking.canSend(player, type);
    }

    @Override
    public boolean canSend(ServerGamePacketListenerImpl listener, Identifier channelName) {
        return ServerPlayNetworking.canSend(listener, channelName);
    }

    @Override
    public boolean canSend(ServerGamePacketListenerImpl listener, CustomPacketPayload.Type<?> type) {
        return ServerPlayNetworking.canSend(listener, type);
    }

    @Override
    public boolean canSend(ServerGamePacketListenerImpl listener, CustomPacketPayload payload) {
        return ServerPlayNetworking.canSend(listener, payload.type());
    }

    @Override
    public <T extends CustomPacketPayload> void registerPayload(
            @NotNull CustomPacketPayload.Type<T> type,
            @NotNull StreamCodec<FriendlyByteBuf, T> codec,
            @Nullable NetSrc.Direction direction,
            boolean clientBound,
            boolean serverBound
    ) {
        if (serverBound) {
            if (direction == NetSrc.Direction.CONFIGURATION)
                PayloadTypeRegistry.serverboundConfiguration().register(type, codec);
            else PayloadTypeRegistry.serverboundPlay().register(type, codec);
        }
        if (clientBound) {
            if (direction == NetSrc.Direction.CONFIGURATION)
                PayloadTypeRegistry.clientboundConfiguration().register(type, codec);
            else PayloadTypeRegistry.clientboundPlay().register(type, codec);
        }
    }

    @Override
    public <T extends CustomPacketPayload> void serverHandler(
            @NotNull CustomPacketPayload.Type<T> type,
            @NotNull NetHandler handler
    ) {
        try {
            ServerPlayNetworking.registerGlobalReceiver(type, (payload, ctx) -> {
                var run = handler.handle(ctx.server(), ctx.player(), null, payload);
                if (run != null) ctx.server().execute(run);
            });
        } catch (Exception ignored) {}
        try {
            ServerConfigurationNetworking.registerGlobalReceiver(type, (payload, ctx) -> {
                var run = handler.handle(ctx.server(), null, ctx.packetListener(), payload);
                if (run != null) ctx.server().execute(run);
            });
        } catch (Exception ignored) {}
    }

    @Override
    public <T extends CustomPacketPayload> void clientHandler(
            @NotNull CustomPacketPayload.Type<T> type,
            @NotNull CNetHandler handler
    ) {
        try {
            net.registerGlobalReceiver(type, handler, NetSrc.Direction.PLAY);
        } catch (Exception ignored) {}
        try {
            net.registerGlobalReceiver(type, handler, NetSrc.Direction.CONFIGURATION);
        } catch (Exception ignored) {}
    }
}
