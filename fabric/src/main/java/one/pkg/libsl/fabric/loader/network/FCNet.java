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

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.resources.ResourceLocation;
import one.pkg.libsl.api.network.CNetHandler;
import one.pkg.libsl.api.network.ICNet;
import one.pkg.libsl.api.network.NetSrc;
import org.jetbrains.annotations.NotNull;

public class FCNet implements ICNet {
    FCNet() {
    }

    @Override
    public void send(@NotNull Object payload, Object... payloads) {
    }

    @SuppressWarnings("all")
    @Override
    public <T extends Object> void registerGlobalReceiver(
            ResourceLocation id,
            CNetHandler handler,
            NetSrc.Direction direction
    ) {
        ClientPlayNetworking.registerGlobalReceiver(id, (client, listener, buf, responseSender) -> {
            Runnable r = handler.handle(client, buf);
            if (r != null) {
                client.execute(r);
            }
        });
    }

    @Override
    public <T extends Object> boolean isRegistered(
            ResourceLocation id) {
        return ClientPlayNetworking.canSend(id);
    }

    @Override
    public boolean canSend(ResourceLocation channelName) {
        return ClientPlayNetworking.canSend(channelName);
    }
}
