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

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Utility class for creating clientbound packets in a Minecraft network environment.
 */
public class NetUtils {
    /**
     * Creates a clientbound packet for the specified payloads.
     *
     * @param payloads The payloads.
     * @return The packet.
     */
    public static Packet<? super ClientGamePacketListener> makeClientbound(@NotNull CustomPacketPayload... payloads) {
        if (payloads.length == 1) {
            return makeClientbound(payloads[0]);
        }
        List<Packet<? super ClientGamePacketListener>> packets = new ArrayList<>(payloads.length);

        for (CustomPacketPayload payload : payloads) {
            packets.add(makeClientbound(payload));
        }

        return new ClientboundBundlePacket(packets);
    }

    /**
     * Creates a clientbound packet for the specified payloads.
     *
     * @param payloads The payloads.
     * @return The packet.
     */
    public static Packet<? super ClientGamePacketListener> makeClientbound(
            @NotNull Collection<? extends CustomPacketPayload> payloads
    ) {
        if (payloads.size() == 1) {
            return makeClientbound(payloads.iterator().next());
        }
        List<Packet<? super ClientGamePacketListener>> packets = new ArrayList<>(payloads.size());

        for (CustomPacketPayload payload : payloads) {
            packets.add(makeClientbound(payload));
        }

        return new ClientboundBundlePacket(packets);
    }

    /**
     * Creates a clientbound packet for the specified payload.
     *
     * @param payload The payload.
     * @return The packet.
     */
    public static Packet<? super ClientGamePacketListener> makeClientbound(@NotNull CustomPacketPayload payload) {
        return new ClientboundCustomPayloadPacket(payload);
    }
}
