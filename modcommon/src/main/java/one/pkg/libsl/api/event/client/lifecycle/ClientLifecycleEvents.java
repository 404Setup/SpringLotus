/*
 * Copyright (C) 2026  404Setup.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package one.pkg.libsl.api.event.client.lifecycle;

import net.minecraft.client.Minecraft;
import one.pkg.libsl.api.event.Event;

/**
 * Interface defining lifecycle events for a Minecraft client. These events provide hooks
 * into various stages of the client's lifecycle, allowing developers to attach custom logic
 * during startup, stopping, and shutdown phases.
 */
public interface ClientLifecycleEvents {
    /**
     * An event that triggers when the Minecraft client has completed its initialization process and is ready for use.
     * This event allows for registering listeners to execute custom logic upon the successful startup of the client.
     * Each registered listener is invoked sequentially with the instance of the started client.
     */
    Event<ClientStarted> CLIENT_STARTED = Event.create(ClientStarted.class, callbacks -> client -> {
        for (ClientStarted callback : callbacks) {
            callback.onClientStarted(client);
        }
    });

    /**
     * Represents an event that is triggered when the Minecraft client has fully stopped.
     * This event handles the execution of registered listeners implementing the {@link ClientStopped}
     * functional interface. It allows for any necessary cleanup or resource deallocation
     * after the client lifecycle has completely terminated.
     * <p>
     * Each registered {@link ClientStopped} listener will have its {@code onClientStopped}
     * method invoked with the instance of the stopped Minecraft client.
     */
    Event<ClientStopped> CLIENT_STOPPED = Event.create(ClientStopped.class, callbacks -> client -> {
        for (ClientStopped callback : callbacks) {
            callback.onClientStopped(client);
        }
    });

    /**
     * Represents an event triggered when the Minecraft client is in the process of stopping.
     * This event allows listeners to register cleanup procedures or perform necessary actions
     * prior to the complete shutdown of the client.
     *
     * <p>Listeners registered to this event will be invoked during the client shutdown sequence,
     * ensuring an opportunity to finalize any required tasks or save important data.</p>
     * <p>
     * The event is implemented using a functional interface {@link ClientStopping}, which provides
     * the {@code onClientStopping} method to handle the stopping phase of the client lifecycle.
     */
    Event<ClientStopping> CLIENT_STOPPING = Event.create(ClientStopping.class, callbacks -> client -> {
        for (ClientStopping callback : callbacks) {
            callback.onClientStopping(client);
        }
    });

    /**
     * A functional interface representing an event triggered when the Minecraft client has started.
     * Implementations of this interface define behavior to be executed after the client has
     * successfully completed initialization and is ready for use.
     */
    @FunctionalInterface
    interface ClientStarted {
        /**
         * Called when the Minecraft client has started. This method is invoked once
         * the client initialization is complete and can be used to perform any actions
         * or setups required after the client has launched.
         *
         * @param client the instance of the Minecraft client that has started
         */
        void onClientStarted(Minecraft client);
    }

    /**
     * A functional interface for handling events triggered when the Minecraft client has stopped.
     * This interface is used to execute code when the client lifecycle is fully terminated,
     * allowing for any necessary cleanup or resource deallocation.
     */
    @FunctionalInterface
    interface ClientStopped {
        /**
         * Called when the Minecraft client has stopped. This indicates that the client
         * lifecycle has ended, and any necessary cleanup processes can be performed
         * at this point.
         *
         * @param client the instance of the Minecraft client that has stopped
         */
        void onClientStopped(Minecraft client);
    }

    /**
     * A functional interface for handling events triggered when the Minecraft client is stopping.
     * This event occurs during the shutdown phase of the client and provides an opportunity
     * for performing cleanup tasks or saving necessary data before termination.
     */
    @FunctionalInterface
    interface ClientStopping {
        /**
         * Called when the Minecraft client is in the process of stopping.
         * This method is triggered during the client shutdown sequence and can
         * be used to perform cleanup or save necessary data before the application completely stops.
         *
         * @param client the instance of the Minecraft client that is stopping
         */
        void onClientStopping(Minecraft client);
    }
}
