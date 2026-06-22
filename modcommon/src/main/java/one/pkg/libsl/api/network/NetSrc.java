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

/**
 * A container for network packet information.
 *
 * @param <T> The type of the custom packet payload.
 */
public class NetSrc<T extends Object> {
    /**
     * The type of the custom packet payload.
     */
    public net.minecraft.resources.ResourceLocation TYPE;

    /**
     * The encoder for the custom packet payload.
     */
    public java.util.function.BiConsumer<T, FriendlyByteBuf> encoder;

    /**
     * The decoder for the custom packet payload.
     */
    public java.util.function.Function<FriendlyByteBuf, T> decoder;

    /**
     * The handler for the packet on the server.
     */
    public NetHandler Handler;

    /**
     * The handler for the packet on the client.
     */
    public CNetHandler ClientHandler;

    /**
     * The direction of the network source.
     */
    public Direction direction = Direction.PLAY;

    /**
     * Whether the packet can be sent to the client.
     */
    public boolean isClientBound = false;

    /**
     * Whether the packet can be sent to the server.
     */
    public boolean isServerBound = false;

    /**
     * Constructs a NetSrc with a server-side handler.
     *
     * @param type    The packet type.
     * @param encoder The packet encoder.
     * @param decoder The packet decoder.
     * @param handler The server-side handler.
     */
    public NetSrc(net.minecraft.resources.ResourceLocation type,
                  java.util.function.BiConsumer<T, FriendlyByteBuf> encoder,
                  java.util.function.Function<FriendlyByteBuf, T> decoder,
                  NetHandler handler) {
        TYPE = type;
        this.encoder = encoder;
        this.decoder = decoder;
        Handler = handler;
        ClientHandler = null;
    }

    /**
     * Constructs a NetSrc without any handlers.
     *
     * @param type    The packet type.
     * @param encoder The packet encoder.
     * @param decoder The packet decoder.
     */
    public NetSrc(net.minecraft.resources.ResourceLocation type,
                  java.util.function.BiConsumer<T, FriendlyByteBuf> encoder,
                  java.util.function.Function<FriendlyByteBuf, T> decoder) {
        TYPE = type;
        this.encoder = encoder;
        this.decoder = decoder;
        Handler = null;
        ClientHandler = null;
    }

    /**
     * Constructs a NetSrc with a client-side handler.
     *
     * @param type    The packet type.
     * @param encoder The packet encoder.
     * @param decoder The packet decoder.
     * @param handler The client-side handler.
     */
    public NetSrc(net.minecraft.resources.ResourceLocation type,
                  java.util.function.BiConsumer<T, FriendlyByteBuf> encoder,
                  java.util.function.Function<FriendlyByteBuf, T> decoder,
                  CNetHandler handler) {
        TYPE = type;
        this.encoder = encoder;
        this.decoder = decoder;
        Handler = null;
        ClientHandler = handler;
    }

    /**
     * The direction of the network source.
     */
    public enum Direction {
        PLAY, CONFIGURATION,
    }
}
