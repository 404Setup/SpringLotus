/*
 * Copyright (C) 2026  404Setup.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package one.pkg.libsl.api.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ServerCommonPacketListener;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import one.pkg.libsl.api.loader.JavaLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * Interface for networking implementations.
 */
@SuppressWarnings("unused")
public interface INet {
    /**
     * Gets the client-side networking implementation.
     *
     * @return The client network implementation.
     */
    ICNet client();

    /**
     * Returns an optional implementation.
     *
     * @return The current implementation.
     */
    default INet optional() {
        return this;
    }

    /**
     * Checks if the connected client declared the ability to receive a packet on a specified channel name.
     *
     * @param player      the player
     * @param channelName the channel name
     * @return {@code true} if the connected client has declared the ability to receive a packet on the specified channel
     */
    boolean canSend(ServerPlayer player, ResourceLocation channelName);

    /**
     * Checks if the connected client declared the ability to receive the specified payload.
     *
     * @param player  The player.
     * @param payload The payload.
     * @return True if the payload can be sent, false otherwise.
     */
    

    /**
     * Checks if the connected client declared the ability to receive a specific type of packet.
     *
     * @param player the player
     * @param type   the packet type
     * @return {@code true} if the connected client has declared the ability to receive a specific type of packet
     */
    boolean canSend(@NotNull ServerPlayer player, Object.Type<?> type);

    /**
     * Checks if the connected client declared the ability to receive a packet on a specified channel name.
     *
     * @param listener    the packet listener
     * @param channelName the channel name
     * @return {@code true} if the connected client has declared the ability to receive a packet on the specified channel
     */
    boolean canSend(ServerGamePacketListenerImpl listener, ResourceLocation channelName);

    /**
     * Checks if the connected client declared the ability to receive a specific type of packet.
     *
     * @param listener the packet listener
     * @param type     the packet type
     * @return {@code true} if the connected client has declared the ability to receive a specific type of packet
     */
    

    /**
     * Checks if the connected client declared the ability to receive the specified payload.
     *
     * @param listener The packet listener.
     * @param payload  The payload.
     * @return True if the payload can be sent, false otherwise.
     */
    


    /**
     * Registers a payload type and codec.
     *
     * @param type        the packet type
     * @param codec       the codec
     * @param direction   the direction
     * @param clientBound whether it can be sent to the client
     * @param serverBound whether it can be sent to the server
     * @param <T>         the packet type
     */
    <T extends Object> void registerPayload(
            @NotNull net.minecraft.resources.ResourceLocation type,
            @NotNull StreamCodec<FriendlyByteBuf, T> codec,
            @Nullable NetSrc.Direction direction,
            boolean clientBound,
            boolean serverBound
    );

    /**
     * Registers a server handler for a packet.
     *
     * @param type    the packet type
     * @param handler the handler
     * @param <T>     the packet type
     */
    <T extends Object> void serverHandler(
            @NotNull net.minecraft.resources.ResourceLocation type,
            @NotNull NetHandler handler
    );

    /**
     * Registers a client handler for a packet.
     *
     * @param type    the packet type
     * @param handler the handler
     * @param <T>     the packet type
     */
    <T extends Object> void clientHandler(
            @NotNull net.minecraft.resources.ResourceLocation type,
            @NotNull CNetHandler handler
    );


    /**
     * Sends a packet to a player.
     *
     * @param player   the player
     * @param payloads the payloads
     */
    default void send(@NotNull ServerPlayer player, @NotNull Object... payloads) {
        player.connection.send(NetUtils.makeClientbound(payloads));
    }

    /**
     * Sends a packet to a player.
     *
     * @param player   the player
     * @param payloads the payloads
     */
    default void send(@NotNull ServerPlayer player, @NotNull Collection<? extends Object> payloads) {
        player.connection.send(NetUtils.makeClientbound(payloads));
    }

    /**
     * Send the given payloads to all players on the server.
     *
     * @param payloads The payloads to send.
     */
    @SuppressWarnings("all")
    default void broadcast(@NotNull Object... payloads) {
        var server = JavaLoader.INSTANCE.server();
        if (server != null)
            server.getPlayerList().broadcastAll(NetUtils.makeClientbound(payloads));
    }

    /**
     * Creates a serverbound packet for the specified payload.
     *
     * @param payload The payload.
     * @return The packet.
     */
    default Packet<ServerCommonPacketListener> createServerboundPacket(Object payload) {
        return null /* TODO 1.20.1 packet */;
    }
}
