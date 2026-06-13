/*
 * Copyright (C) 2026  404Setup.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package one.pkg.libsl.api.event.client.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import one.pkg.libsl.api.event.Event;

/**
 * A functional interface used to handle the registration of client-side commands.
 *
 * <p>The {@code ClientCommandRegistrationCallback} interface defines a callback that is invoked
 * whenever client commands need to be registered. Implementations of this interface can define
 * custom command registration logic, which will be executed during the appropriate event.
 *
 * <p>This interface provides the static {@code EVENT} field, which is used as an event bus for
 * listeners. The listeners are invoked sequentially in the order they are registered, allowing
 * multiple callbacks to contribute to the command registration process.
 *
 * <p>Each registered callback is provided with a {@link CommandDispatcher}
 * and a {@link CommandBuildContext} object to facilitate the registration
 * of custom commands.
 */
@FunctionalInterface
public interface ClientCommandRegistrationEvent {
    /**
     * An event used to handle the registration of client-side commands by invoking
     * all registered {@link ClientCommandRegistrationEvent} listeners sequentially.
     *
     * <p>The {@code EVENT} field serves as the event bus for the {@link ClientCommandRegistrationEvent}.
     * Listeners registered to this event are executed in the order of their registration.
     * Each listener is provided with a {@link CommandDispatcher} and a {@link CommandBuildContext}
     * to facilitate the registration of custom commands.
     *
     * <p>This event is implemented using the {@link Event} class, which aggregates all registered
     * listeners and provides an invoker that dispatches calls to each listener in sequence. The
     * listeners are expected to contribute their command registration logic when the event is invoked.
     */
    Event<ClientCommandRegistrationEvent> EVENT = Event.create(ClientCommandRegistrationEvent.class,
            (callbacks) ->
                    (dispatcher, context) -> {
                        for (ClientCommandRegistrationEvent callback : callbacks) {
                            callback.register(dispatcher, context);
                        }
                    });

    /**
     * Registers client-side commands using the provided {@link CommandDispatcher} and {@link CommandBuildContext}.
     * This method is invoked during the client command registration event to allow custom command definitions.
     *
     * @param dispatcher the command dispatcher used to register commands.
     * @param context    the context providing information and utilities for command registration.
     */
    void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context);
}
