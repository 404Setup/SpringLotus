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
import one.pkg.libsl.api.event.Event;

public interface ServerLifecycleEvents {
    /**
     * Event triggered when the Minecraft server is about to start.
     * This event is invoked in the early initialization phase, providing an opportunity
     * for listeners to prepare or configure resources before the server fully initializes.
     * <p>
     * The {@link AboutStarting} functional interface defines the callback mechanism
     * for this event. Listeners registered to this event can execute their custom logic
     * by implementing the {@code onServerAboutStarting} method of the {@link AboutStarting} interface.
     *
     * <p>
     * Example use cases for this event include:
     * - Setting up configurations or initializing services that need to be ready
     * before other server components are loaded.
     * - Performing early validation or pre-checks for dependencies crucial
     * to the server lifecycle.
     */
    Event<AboutStarting> ABOUT_STARTING = Event.create(AboutStarting.class, callbacks ->
            server -> {
                for (AboutStarting about : callbacks) {
                    about.onServerAboutStarting(server);
                }
            });

    /**
     * Event triggered when the Minecraft server has completed its startup process.
     * This event allows registered listeners to execute behaviors or logic at the
     * point when the server is fully operational and ready to handle tasks or respond
     * to other events.
     * <p>
     * The event uses the {@link Started} functional interface to define actions
     * that are executed for each registered listener. The responsibility of this
     * interface is to handle post-startup logic, which could include initializing
     * components, emitting readiness signals to other systems, or performing tasks
     * that rely on a fully initialized server.
     * <p>
     * Listeners are called using the {@code onServerStarted} method defined in the
     * {@link Started} interface, passing the fully started Minecraft server instance
     * as a parameter. This ensures that any required interactions with the server can
     * happen after all startup tasks have been completed.
     */
    Event<Started> STARTED = Event.create(Started.class, callbacks ->
            server -> {
                for (Started started : callbacks) {
                    started.onServerStarted(server);
                }
            });

    /**
     * The {@code STARTING} event is triggered during the starting phase of the Minecraft server's
     * lifecycle, prior to the server becoming fully initialized and operational. This event allows
     * behavior to be defined and executed at this stage, providing an opportunity to perform
     * setup tasks, initialize resources, or register components necessary for the server's operation.
     * <p>
     * The {@code callbacks} parameter consists of all registered {@link Starting} listeners, which
     * are invoked sequentially through their {@code onServerStarting} method. Each registered
     * listener can implement custom logic required during this phase.
     * <p>
     * This event is created using the {@link Event#create(Class, java.util.function.Function)} factory method.
     */
    Event<Starting> STARTING = Event.create(Starting.class, callbacks ->
            server -> {
                for (Starting starting : callbacks) {
                    starting.onServerStarting(server);
                }
            });

    /**
     * Represents an event triggered during the shutdown process of the Minecraft server.
     * The STOPPING event is invoked when the server is actively stopping, providing an
     * opportunity for tasks such as cleanup, resource management, and saving data before
     * the server terminates.
     * <p>
     * Implementors of the {@link Stopping} interface can register their callbacks to this
     * event. Each registered callback's {@code onServerStopping} method is executed with
     * the server instance as an argument.
     */
    Event<Stopping> STOPPING = Event.create(Stopping.class, callbacks ->
            server -> {
                for (Stopping stopping : callbacks) {
                    stopping.onServerStopping(server);
                }
            });

    /**
     * An event invoked when the Minecraft server has completely stopped. This event
     * allows listeners to perform cleanup operations, close resources, or execute any
     * logic necessary after the server has fully terminated its lifecycle.
     * <p>
     * Each registered {@code Stopped} listener is called in sequence with the
     * instance of the stopped {@code MinecraftServer}.
     */
    Event<Stopped> STOPPED = Event.create(Stopped.class, callbacks ->
            server -> {
                for (Stopped stopped : callbacks) {
                    stopped.onServerStopped(server);
                }
            });

    /**
     * Represents a functional interface that defines behavior to be executed
     * when the Minecraft server is about to start. This phase occurs just before
     * the server begins its startup process and provides an opportunity to prepare
     * or initialize resources that should be available prior to the server's initialization.
     * <p>
     * The {@code onServerAboutStarting} method allows custom logic to be executed
     * during this phase, such as configuring settings, loading essential services,
     * or preparing systems that require initialization before the actual server start.
     */
    @FunctionalInterface
    interface AboutStarting {
        /**
         * Called when the server is about to start.
         *
         * @param server the Minecraft server instance
         */
        void onServerAboutStarting(MinecraftServer server);
    }

    /**
     * Represents a functional interface that defines behavior to be executed
     * when the Minecraft server has completed its startup process.
     * <p>
     * The {@code onServerStarted} method provides an entry point for any post-startup
     * logic, such as initializing post-load components, notifying other systems that
     * the server is ready, or executing deferred actions that require the server to
     * be fully operational.
     */
    @FunctionalInterface
    interface Started {
        /**
         * Called when the Minecraft server has finished starting.
         *
         * @param server the Minecraft server instance
         */
        void onServerStarted(MinecraftServer server);
    }

    /**
     * Represents a functional interface that can be used to define behavior to occur
     * during the starting phase of the Minecraft server's lifecycle. This phase
     * occurs before the server is fully initialized and operational.
     * <p>
     * The {@code onServerStarting} method provides an entry point for initializing
     * resources, registering custom components, or performing setup tasks required
     * before the server's startup is complete.
     */
    @FunctionalInterface
    interface Starting {
        /**
         * Called when the Minecraft server is starting. This event can be used to initialize
         * resources, register custom components, or perform any setup necessary before the
         * server fully starts.
         *
         * @param server the Minecraft server instance
         */
        void onServerStarting(MinecraftServer server);
    }

    /**
     * Represents an event that is triggered when the Minecraft server is in the process of stopping.
     * This functional interface is designed to allow implementors to define custom behavior to be
     * executed during the server's shutdown sequence.
     * <p>
     * The {@code onServerStopping} method is called during the shutdown process, providing the
     * Minecraft server instance, enabling tasks such as resource cleanup, graceful shutdown of
     * processes, or saving data.
     */
    @FunctionalInterface
    interface Stopping {
        /**
         * Called when the Minecraft server is stopping. This method is invoked during the server's
         * shutdown process and can be used to perform cleanup operations or save data before the
         * server fully stops.
         *
         * @param server the instance of the Minecraft server that is stopping
         */
        void onServerStopping(MinecraftServer server);
    }

    @FunctionalInterface
    interface Stopped {
        /**
         * Called when the Minecraft server has completely stopped. This event provides an opportunity
         * to clean up resources, close open connections, or perform any logic required after
         * the server has fully terminated.
         *
         * @param server the instance of the Minecraft server that has stopped
         */
        void onServerStopped(MinecraftServer server);
    }
}
