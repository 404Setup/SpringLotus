/*
 * Copyright (C) 2026  404Setup.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package one.pkg.libsl.api.event.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import one.pkg.libsl.api.event.Event;

/**
 * Represents a functional interface that provides a callback mechanism for registering commands
 * dynamically. Implementations of this interface are invoked during the command registration
 * process to add new commands to the provided dispatcher.
 * <p>
 * This interface is typically used to integrate custom commands with a command dispatcher
 * depending on a specific registration context or environment.
 */
@FunctionalInterface
public interface CommandRegistrationEvent {
    /**
     * An event that allows for the dynamic registration of commands by invoking all registered
     * {@link CommandRegistrationEvent} listeners. Each listener is responsible for registering
     * commands to the provided dispatcher within the given registration context and environment.
     * <p>
     * This event is created using the {@link Event#create(Class, java.util.function.Function)} method,
     * which initializes a registry for listeners of type {@link CommandRegistrationEvent}. When
     * triggered, all registered listeners are called in the order they were added.
     * <p>
     * The event is invoked with the following parameters:
     * - The command dispatcher used to register commands.
     * - The registration context providing additional metadata or utilities.
     * - The environment specifying which commands should be registered.
     */
    Event<CommandRegistrationEvent> EVENT = Event.create(CommandRegistrationEvent.class, listeners -> (dispatcher, context, environment) -> {
        for (CommandRegistrationEvent listener : listeners) {
            listener.register(dispatcher, context, environment);
        }
    });

    /**
     * Registers commands for the given dispatch environment.
     *
     * @param dispatcher  the command dispatcher used to register new commands
     * @param context     the context providing additional information for command registration
     * @param environment the selection of the command registration environment
     */
    void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context, Commands.CommandSelection environment);
}
