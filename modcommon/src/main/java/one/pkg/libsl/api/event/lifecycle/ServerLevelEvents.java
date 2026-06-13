/*
 * Copyright (C) 2026  404Setup.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package one.pkg.libsl.api.event.lifecycle;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.LevelAccessor;
import one.pkg.libsl.api.event.Event;

/**
 * Defines server-level event handlers for managing the lifecycle of levels
 * in a Minecraft server. This interface provides events for when a level is
 * loaded or unloaded, allowing additional behavior to be implemented during
 * these phases.
 * <p>
 * The events use a functional interface-based listener system, enabling the
 * registration of custom behavior through event listeners.
 */
public interface ServerLevelEvents {
    /**
     * Represents the event triggered during the lifecycle of a server when a level is loaded.
     * This event allows custom behavior to be executed whenever a level is loaded on the server.
     * <p>
     * The event provides a mechanism to register multiple {@link Load} listeners,
     * and invokes their {@code onLevelLoad} method sequentially, passing the server
     * and level instances as arguments.
     * <p>
     * Listeners can be registered using the {@code register} method of the {@link Event}.
     */
    Event<Load> LOAD = Event.create(Load.class, callbacks ->
            (server, level) -> {
                for (Load callback : callbacks) {
                    callback.onLevelLoad(server, level);
                }
            });

    /**
     * Event triggered when a server level is unloaded.
     * This event allows the registration of listener callbacks that handle actions
     * such as cleanup or finalization steps when a level is unloaded in the server.
     * <p>
     * The event listeners are invoked sequentially for each registered callback
     * when a level unloads, passing the server instance and the level being unloaded
     * as parameters.
     * <p>
     * The listeners are functional interfaces of type {@link ServerLevelEvents.Unload},
     * which must implement the {@code onLevelUnload} method to define specific actions.
     */
    Event<Unload> UNLOAD = Event.create(Unload.class, callbacks ->
            (server, level) -> {
                for (Unload callback : callbacks) {
                    callback.onLevelUnload(server, level);
                }
            });

    /**
     * Represents an event triggered when a server level is loaded.
     * This functional interface is used in the lifecycle of a Minecraft server
     * to handle actions or processes related to loading a level.
     */
    @FunctionalInterface
    interface Load {
        /**
         * Handles actions to be performed when a level is loaded in the server.
         *
         * @param server The MinecraftServer instance where the level is being loaded.
         * @param level  The LevelAccessor instance representing the level being loaded.
         */
        void onLevelLoad(MinecraftServer server, LevelAccessor level);
    }

    /**
     * Represents an event triggered when a server level is unloaded.
     * This functional interface is used in the lifecycle of a Minecraft server
     * to handle cleanup or other actions related to unloading a level.
     */
    @FunctionalInterface
    interface Unload {
        /**
         * Handles the event triggered when a level is unloaded.
         *
         * @param server the instance of the Minecraft server managing the level.
         * @param level  the level being unloaded.
         */
        void onLevelUnload(MinecraftServer server, LevelAccessor level);
    }
}
