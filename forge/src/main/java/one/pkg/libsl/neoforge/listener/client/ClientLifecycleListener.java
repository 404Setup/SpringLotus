/*
 * Copyright (C) 2026  404Setup.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package one.pkg.libsl.neoforge.listener.client;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.lifecycle.ClientStartedEvent;
import net.neoforged.neoforge.client.event.lifecycle.ClientStoppedEvent;
import net.neoforged.neoforge.client.event.lifecycle.ClientStoppingEvent;
import one.pkg.libsl.api.event.client.lifecycle.ClientLifecycleEvents;

public class ClientLifecycleListener {
    @SubscribeEvent
    public static void onClientStartedEvent(ClientStartedEvent event) {
        ClientLifecycleEvents.CLIENT_STARTED.invoker().onClientStarted(event.getClient());
    }

    @SubscribeEvent
    public static void onClientStoppedEvent(ClientStoppedEvent event) {
        ClientLifecycleEvents.CLIENT_STOPPED.invoker().onClientStopped(event.getClient());
    }

    @SubscribeEvent
    public static void onClientStoppingEvent(ClientStoppingEvent event) {
        ClientLifecycleEvents.CLIENT_STOPPING.invoker().onClientStopping(event.getClient());
    }
}
