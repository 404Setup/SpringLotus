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

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import org.jetbrains.annotations.Nullable;

/**
 * Handler for network packets on the server side.
 */
public interface NetHandler {
    /**
     * Handles a packet payload.
     *
     * @param server   The Minecraft server.
     * @param player   The player who sent the packet.
     * @param listener The packet listener.
     * @param payload  The packet payload.
     * @return A Runnable to be executed on the server thread, or null.
     */
    Runnable handle(MinecraftServer server, @Nullable ServerPlayer player, @Nullable ServerConfigurationPacketListenerImpl listener, Object payload);
}
