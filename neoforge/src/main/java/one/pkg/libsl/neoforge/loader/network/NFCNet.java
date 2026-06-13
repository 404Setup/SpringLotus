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

import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import one.pkg.libsl.api.network.CNetHandler;
import one.pkg.libsl.api.network.ICNet;
import one.pkg.libsl.api.network.NetSrc;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Client-side networking implementation for NeoForge.
 */
public class NFCNet implements ICNet, IPayloadHandler<CustomPacketPayload> {
    private final NFNet parent;

    /**
     * Constructs a {@code Client} networking instance.
     *
     * @param parent the parent {@code NeoForgeNetworking} instance
     */
    public NFCNet(NFNet parent) {
        this.parent = parent;
    }

    @Override
    public void send(@NotNull CustomPacketPayload payload, CustomPacketPayload... payloads) {
        ClientPacketDistributor.sendToServer(payload, payloads);
    }

    @Override
    public boolean canSend(Identifier channelName) {
        var connection = Minecraft.getInstance().getConnection();
        return connection != null && connection.hasChannel(channelName);
    }

    @Override
    public boolean canSend(CustomPacketPayload.Type<?> type) {
        var connection = Minecraft.getInstance().getConnection();
        return connection != null && connection.hasChannel(type);
    }

    @Override
    public boolean canSend(CustomPacketPayload payload) {
        var connection = Minecraft.getInstance().getConnection();
        return connection != null && connection.hasChannel(payload);
    }

    @Override
    public void handle(@NotNull CustomPacketPayload payload, @NotNull IPayloadContext context) {
        var v = parent.REGISTRIES.get(payload.type().id());
        if (v == null || v.ClientHandler == null) return;
        var run = v.ClientHandler.handle(Minecraft.getInstance(), payload);
        if (run != null) context.enqueueWork(run);
    }

    @Override
    public <T extends CustomPacketPayload> void registerGlobalReceiver(
            CustomPacketPayload.Type<T> id,
            CNetHandler handler,
            NetSrc.Direction direction
    ) {
    }

    @Override
    public <T extends CustomPacketPayload> boolean isRegistered(CustomPacketPayload.Type<T> id) {
        return parent.REGISTRIES.containsKey(id.id());
    }

    /**
     * Registers client payload handlers.
     *
     * @param event the event to register the handlers
     */
    public void runRegister(RegisterClientPayloadHandlersEvent event) {
        runRegister(event, new HashSet<>());
    }

    /**
     * Registers client payload handlers that have not already been registered by another network registry.
     *
     * @param event the event to register the handlers
     * @param registeredTypes the payload types already registered during this event
     */
    public void runRegister(RegisterClientPayloadHandlersEvent event, Set<Identifier> registeredTypes) {
        for (Map.Entry<Identifier, NetSrc> entry : parent.REGISTRIES.entrySet()) {
            var v = entry.getValue();
            if (v.isClientBound && registeredTypes.add(entry.getKey())) event.register(v.TYPE, this);
        }
    }
}
