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
import net.minecraft.resources.ResourceLocation;
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
    default void send(RemotePlayer player, Object... payloads) {
        throw new UnsupportedOperationException("Remote players are not supported");
    }

    /**
     * Sends one or more custom packet payloads to the specified local player.
     *
     * @param player   The local player to whom the packets will be sent.
     * @param payloads One or more custom packet payloads to send.
     */
    default void send(LocalPlayer player, Object... payloads) {
        player.connection.send(NetUtils.makeClientbound(payloads));
    }

    /**
     * Sends a payload to the connected server.
     *
     * @param payload  The payload to send.
     * @param payloads Additional payloads to send.
     */
    void send(@NotNull Object payload, Object... payloads);

    /**
     * Registers a global receiver for a specific packet type.
     *
     * @param id        The packet type ID.
     * @param handler   The handler to process the packet.
     * @param direction The direction of the network operation.
     * @param <T>       The type of the custom packet payload.
     */
    <T extends Object> void registerGlobalReceiver(
            net.minecraft.resources.ResourceLocation id,
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
    <T extends Object> boolean isRegistered(net.minecraft.resources.ResourceLocation id);

    /**
     * Checks if a packet can be sent on the specified channel.
     *
     * @param channelName The channel identifier.
     * @return True if the packet can be sent, false otherwise.
     */
    boolean canSend(ResourceLocation channelName);

    /**
     * Checks if a packet of the specified type can be sent.
     *
     * @param type The packet type.
     * @return True if the packet can be sent, false otherwise.
     */
    

    /**
     * Checks if the specified payload can be sent.
     *
     * @param payload The packet payload.
     * @return True if the payload can be sent, false otherwise.
     */
    
}
