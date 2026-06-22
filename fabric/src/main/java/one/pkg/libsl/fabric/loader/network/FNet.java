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

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import one.pkg.libsl.api.network.*;
import org.jetbrains.annotations.NotNull;

public class FNet implements INet {
    private final ICNet net = new FCNet();

    @Override
    public ICNet client() {
        return net;
    }

    @Override
    public boolean canSend(ServerPlayer player, ResourceLocation channelName) {
        return ServerPlayNetworking.canSend(player, channelName);
    }

    @Override
    public boolean canSend(ServerGamePacketListenerImpl listener, ResourceLocation channelName) {
        return ServerPlayNetworking.canSend(listener, channelName);
    }

    @Override
    public <T extends Object> void serverHandler(
            @NotNull ResourceLocation type,
            @NotNull NetHandler handler
    ) {
        try {
            ServerPlayNetworking.registerGlobalReceiver(type, (server, player, listener, buf, responseSender) -> {
                Runnable run = handler.handle(server, player, buf);
                if (run != null) server.execute(run);
            });
        } catch (Exception ignored) {}
    }

    @Override
    public <T extends Object> void clientHandler(
            @NotNull ResourceLocation type,
            @NotNull CNetHandler handler
    ) {
        try {
            net.registerGlobalReceiver(type, handler, NetSrc.Direction.PLAY);
        } catch (Exception ignored) {}
    }
}
