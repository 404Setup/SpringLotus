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

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

/**
 * Interface for client-side networking implementations.
 */
@SuppressWarnings("unused")
public interface ICNet {
    /**
     * Sends one or more custom packet payloads to a remote player.
     *
     * @param player   The remote player to whom the payloads are sent.
     * @param payloads The custom packet payloads to send.
     *                 The method supports sending multiple payloads at once.
     * @throws UnsupportedOperationException If the operation is attempted, as
     *                                       remote players are not supported.
     */
    default void send(RemotePlayer player, CustomPacketPayload... payloads) {
        throw new UnsupportedOperationException("Remote players are not supported");
    }

    /**
     * Sends one or more custom packet payloads to the specified local player.
     *
     * @param player   The local player to whom the packets will be sent.
     * @param payloads One or more custom packet payloads to send.
     */
    default void send(LocalPlayer player, CustomPacketPayload... payloads) {
        player.connection.send(NetUtils.makeClientbound(payloads));
    }

    /**
     * Sends a payload to the connected server.
     *
     * @param payload  The payload to send.
     * @param payloads Additional payloads to send.
     */
    void send(@NotNull CustomPacketPayload payload, CustomPacketPayload... payloads);

    /**
     * Registers a global receiver for a specific packet type.
     *
     * @param id        The packet type ID.
     * @param handler   The handler to process the packet.
     * @param direction The direction of the network operation.
     * @param <T>       The type of the custom packet payload.
     */
    <T extends CustomPacketPayload> void registerGlobalReceiver(
            CustomPacketPayload.Type<T> id,
            CNetHandler handler,
            NetSrc.Direction direction
    );

    /**
     * Checks if the connected server declared the ability to receive a payload on a specified channel name.
     *
     * @param id  The payload type.
     * @param <T> The type of the custom packet payload.
     * @return True if the connected server has declared the ability to receive a payload on the specified channel.
     */
    <T extends CustomPacketPayload> boolean isRegistered(CustomPacketPayload.Type<T> id);

    /**
     * Checks if a packet can be sent on the specified channel.
     *
     * @param channelName The channel identifier.
     * @return True if the packet can be sent, false otherwise.
     */
    boolean canSend(Identifier channelName);

    /**
     * Checks if a packet of the specified type can be sent.
     *
     * @param type The packet type.
     * @return True if the packet can be sent, false otherwise.
     */
    boolean canSend(CustomPacketPayload.Type<?> type);

    /**
     * Checks if the specified payload can be sent.
     *
     * @param payload The packet payload.
     * @return True if the payload can be sent, false otherwise.
     */
    boolean canSend(CustomPacketPayload payload);
}
