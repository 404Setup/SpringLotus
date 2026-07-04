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

import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraftforge.network.PacketDistributor;
import one.pkg.libsl.api.network.CNetHandler;
import one.pkg.libsl.api.network.ICNet;
import one.pkg.libsl.api.network.NetSrc;
import org.jetbrains.annotations.NotNull;

/**
 * Client-side networking implementation for Forge.
 */
public class FCNet implements ICNet {
    private final FNet parent;

    /**
     * Constructs a {@code Client} networking instance.
     *
     * @param parent the parent {@code ForgeNetworking} instance
     */
    public FCNet(FNet parent) {
        this.parent = parent;
    }

    @Override
    public void send(@NotNull CustomPacketPayload payload, CustomPacketPayload... payloads) {
        var channel = parent.getChannel();
        if (channel != null) {
            channel.send(payload, PacketDistributor.SERVER.noArg());
            for (var p : payloads) {
                channel.send(p, PacketDistributor.SERVER.noArg());
            }
        }
    }

    @Override
    public boolean canSend(Identifier channelName) {
        var channel = parent.getChannel();
        if (channel == null) return false;
        var connection = Minecraft.getInstance().getConnection();
        return connection != null && channel.isRemotePresent(connection.getConnection());
    }

    @Override
    public boolean canSend(CustomPacketPayload.Type<?> type) {
        return canSend((Identifier) null);
    }

    @Override
    public boolean canSend(CustomPacketPayload payload) {
        return canSend((Identifier) null);
    }

    @Override
    public <T extends CustomPacketPayload> void registerGlobalReceiver(
            CustomPacketPayload.Type<T> id,
            CNetHandler handler,
            NetSrc.Direction direction
    ) {
        // Forge handles this through the channel registration in FNet
    }

    @Override
    public <T extends CustomPacketPayload> boolean isRegistered(CustomPacketPayload.Type<T> id) {
        return parent.REGISTRIES.containsKey(id.id());
    }
}
