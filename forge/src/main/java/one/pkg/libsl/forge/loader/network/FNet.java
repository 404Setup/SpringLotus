/*
 * Copyright (C) 2026  404Setup.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package one.pkg.libsl.forge.loader.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.network.Channel;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.PacketDistributor;
import one.pkg.libsl.Static;
import one.pkg.libsl.api.loader.JavaLoader;
import one.pkg.libsl.api.network.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("all")
public class FNet implements INet {
    public final Map<Identifier, NetSrc> REGISTRIES = new HashMap<>();
    private final FCNet client = new FCNet(this);
    private final FONet optional;

    /**
     * The built Forge PayloadChannel. Initialized by {@link #buildChannel()}.
     */
    private Channel<CustomPacketPayload> channel;

    /**
     * Whether this FNet is the "optional" variant (allows missing channel on remote).
     */
    private final boolean isOptional;

    public FNet() {
        this.isOptional = false;
        this.optional = new FONet(this);
    }

    FNet(boolean isOptional) {
        this.isOptional = isOptional;
        this.optional = null;
    }

    @Override
    public FONet optional() {
        return optional;
    }

    /**
     * Builds and registers the Forge PayloadChannel from all registered payloads.
     * Must be called after all payloads have been registered via {@link #registerPayload}.
     */
    public void buildChannel() {
        if (REGISTRIES.isEmpty()) return;

        var channelName = Static.MOD_ID + ":" + (isOptional ? "net_optional" : "net");
        var builder = ChannelBuilder
                .named(channelName)
                .networkProtocolVersion(1);

        if (isOptional) {
            builder = builder.optional();
        }

        // Build channel using the any() protocol (accepts any connection phase).
        // We use any() because our StreamCodec<FriendlyByteBuf, T> is not RegistryFriendlyByteBuf,
        // so we cannot use play(). Proper phase validation happens at the application level.
        var flow = builder.payloadChannel().<FriendlyByteBuf, CustomPacketPayload>any().flow((PacketFlow) null);

        for (Map.Entry<Identifier, NetSrc> entry : REGISTRIES.entrySet()) {
            var v = entry.getValue();
            PacketFlow packetFlow = getPacketFlow(v);
            flow = flow.add(v.TYPE, v.CODEC, (payload, ctx) -> handlePayload(payload, ctx));
        }

        this.channel = flow.build();
    }

    @Nullable
    private static PacketFlow getPacketFlow(NetSrc v) {
        if (v.isClientBound && v.isServerBound) return null;
        if (v.isClientBound) return PacketFlow.CLIENTBOUND;
        if (v.isServerBound) return PacketFlow.SERVERBOUND;
        return null;
    }

    void handlePayload(CustomPacketPayload payload, CustomPayloadEvent.Context ctx) {
        var v = REGISTRIES.get(payload.type().id());
        if (v == null) return;

        if (ctx.isServerSide()) {
            if (v.Handler == null) return;
            Runnable runnable;
            if (v.direction == NetSrc.Direction.CONFIGURATION) {
                runnable = v.Handler.handle(
                        JavaLoader.INSTANCE.server(),
                        null,
                        (ServerConfigurationPacketListenerImpl) ctx.getConnection().getPacketListener(),
                        payload
                );
            } else {
                runnable = v.Handler.handle(
                        JavaLoader.INSTANCE.server(),
                        ctx.getSender(),
                        null,
                        payload
                );
            }
            if (runnable != null) ctx.enqueueWork(runnable);
        } else {
            // Client side
            if (v.ClientHandler == null) return;
            Runnable runnable = v.ClientHandler.handle(net.minecraft.client.Minecraft.getInstance(), payload);
            if (runnable != null) ctx.enqueueWork(runnable);
        }
        ctx.setPacketHandled(true);
    }

    /**
     * Returns the built Forge channel. Must call {@link #buildChannel()} first.
     */
    public Channel<CustomPacketPayload> getChannel() {
        return channel;
    }

    @Override
    public boolean canSend(ServerPlayer player, Identifier channelName) {
        return channel != null && channel.isRemotePresent(player.connection.getConnection());
    }

    @Override
    public boolean canSend(ServerPlayer player, CustomPacketPayload payload) {
        return channel != null && channel.isRemotePresent(player.connection.getConnection());
    }

    @Override
    public boolean canSend(@NotNull ServerPlayer player, CustomPacketPayload.Type<?> type) {
        return channel != null && channel.isRemotePresent(player.connection.getConnection());
    }

    @Override
    public boolean canSend(ServerGamePacketListenerImpl listener, Identifier channelName) {
        return channel != null && channel.isRemotePresent(listener.getConnection());
    }

    @Override
    public boolean canSend(ServerGamePacketListenerImpl listener, CustomPacketPayload.Type<?> type) {
        return channel != null && channel.isRemotePresent(listener.getConnection());
    }

    @Override
    public boolean canSend(ServerGamePacketListenerImpl listener, CustomPacketPayload payload) {
        return channel != null && channel.isRemotePresent(listener.getConnection());
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
    public ICNet client() {
        return client;
    }

    /**
     * Sends a payload to a player using the Forge channel.
     */
    public void sendToPlayer(ServerPlayer player, CustomPacketPayload payload) {
        if (channel != null) {
            channel.send(payload, PacketDistributor.PLAYER.with(player));
        }
    }
}
