/*
 * Copyright (C) 2026  404Setup.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package one.pkg.libsl.neoforge.loader.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import one.pkg.libsl.api.loader.JavaLoader;
import one.pkg.libsl.api.network.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("all")
public class NFNet implements INet, NetPayloadRegistrar,
        IPayloadHandler<CustomPacketPayload> {
    public final Map<Identifier, NetSrc> REGISTRIES = new HashMap<>();
    private final NFCNet client = new NFCNet(this);
    private final NFONet optional;

    public NFNet() {
        this.optional = new NFONet(this);
    }

    NFNet(Void v) {
        this.optional = null;
    }

    @Override
    public NFONet optional() {
        return optional;
    }

    @Override
    public boolean canSend(ServerPlayer player, Identifier channelName) {
        return player.connection.hasChannel(channelName);
    }

    @Override
    public boolean canSend(ServerPlayer player, CustomPacketPayload payload) {
        return player.connection.hasChannel(payload);
    }

    @Override
    public boolean canSend(@NotNull ServerPlayer player, CustomPacketPayload.Type<?> type) {
        return player.connection.hasChannel(type);
    }

    @Override
    public boolean canSend(ServerGamePacketListenerImpl listener, Identifier channelName) {
        return listener.hasChannel(channelName);
    }

    @Override
    public boolean canSend(ServerGamePacketListenerImpl listener, CustomPacketPayload.Type<?> type) {
        return listener.hasChannel(type);
    }

    @Override
    public boolean canSend(ServerGamePacketListenerImpl listener, CustomPacketPayload payload) {
        return listener.hasChannel(payload);
    }

    @Override
    public <T extends CustomPacketPayload> void registerPayload(
            @NotNull CustomPacketPayload.Type<T> type,
            @NotNull StreamCodec<FriendlyByteBuf, T> codec,
            @Nullable NetSrc.Direction direction,
            boolean clientBound,
            boolean serverBound
    ) {
        NetSrc<T> n = REGISTRIES.get(type.id());
        if (n == null) {
            n = new NetSrc<>(type, codec);
        }
        if (direction != null) n.direction = direction;
        if (clientBound) n.isClientBound = true;
        if (serverBound) n.isServerBound = true;
        REGISTRIES.put(type.id(), n);
    }

    @Override
    public <T extends CustomPacketPayload> void serverHandler(
            @NotNull CustomPacketPayload.Type<T> type,
            @NotNull NetHandler handler
    ) {
        NetSrc<T> v = REGISTRIES.get(type.id());
        if (v != null) {
            v.Handler = handler;
        }
    }

    @Override
    public <T extends CustomPacketPayload> void clientHandler(
            @NotNull CustomPacketPayload.Type<T> type,
            @NotNull CNetHandler handler
    ) {
        NetSrc<T> v = REGISTRIES.get(type.id());
        if (v != null) {
            v.ClientHandler = handler;
        }
    }

    @Override
    public void handle(@NotNull CustomPacketPayload payload, @NotNull IPayloadContext context) {
        var v = REGISTRIES.get(payload.type().id());
        if (v == null || v.Handler == null) return;
        Runnable runnable = v.direction == NetSrc.Direction.CONFIGURATION ?
                v.Handler.handle(JavaLoader.INSTANCE.server(),
                    null, (ServerConfigurationPacketListenerImpl) context.listener(), payload)
                :  v.Handler.handle(JavaLoader.INSTANCE.server(),
                    (ServerPlayer) context.player(), null, payload);
        if (runnable != null) context.enqueueWork(runnable);
    }

    @Override
    public void onPayloadRegistrar(PayloadRegistrar registrar) {
        for (Map.Entry<Identifier, NetSrc> entry : REGISTRIES.entrySet()) {
            var v = entry.getValue();
            
            boolean bidirectional = v.isClientBound && v.isServerBound;
            boolean isPlay = v.direction == NetSrc.Direction.PLAY;
            
            if (bidirectional) {
                if (isPlay) registrar.playBidirectional(v.TYPE, v.CODEC, this);
                else registrar.configurationBidirectional(v.TYPE, v.CODEC, this);
            } else if (v.isServerBound) {
                // To Server
                if (isPlay) registrar.playToServer(v.TYPE, v.CODEC, this);
                else registrar.configurationToServer(v.TYPE, v.CODEC, this);
            } else if (v.isClientBound) {
                // To Client
                if (isPlay) registrar.playToClient(v.TYPE, v.CODEC);
                else registrar.configurationToClient(v.TYPE, v.CODEC);
            }
        }
    }

    @Override
    public ICNet client() {
        return client;
    }
}
