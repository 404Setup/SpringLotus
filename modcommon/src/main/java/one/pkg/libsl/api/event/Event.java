/*
 * Copyright (C) 2026  404Setup.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package one.pkg.libsl.api.event;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.function.Function;

/**
 * A simple event registry similar to Fabric API's EventFactory.
 *
 * @param <T> the type of the event listener
 */
public class Event<T> {
    private final Function<T[], T> invokerFactory;
    private T invoker;
    private T[] handlers;

    /**
     * Creates a new event registry.
     *
     * @param type           the type of the event listener
     * @param invokerFactory the factory to create the invoker
     */
    @SuppressWarnings("unchecked")
    public Event(Class<? super T> type, Function<T[], T> invokerFactory) {
        this.invokerFactory = invokerFactory;
        this.handlers = (T[]) Array.newInstance(type, 0);
        this.invoker = invokerFactory.apply(this.handlers);
    }

    /**
     * Creates a new event registry.
     *
     * @param type           the type of the event listener
     * @param invokerFactory the factory to create the invoker
     * @param <T>            the type of the event listener
     * @return the new event registry
     */
    public static <T> Event<T> create(Class<? super T> type, Function<T[], T> invokerFactory) {
        return new Event<>(type, invokerFactory);
    }

    /**
     * Registers a new listener to the event.
     *
     * @param listener the listener to register
     */
    public void register(T listener) {
        this.handlers = Arrays.copyOf(this.handlers, this.handlers.length + 1);
        this.handlers[this.handlers.length - 1] = listener;
        if (this.handlers.length == 1) {
            this.invoker = this.handlers[0];
        } else {
            this.invoker = this.invokerFactory.apply(this.handlers);
        }
    }

    /**
     * Returns the number of listeners registered to the event.
     *
     * @return the number of listeners
     */
    public int size() {
        return this.handlers.length;
    }

    /**
     * Checks if the event can be skipped, i.e., if there are no registered listeners.
     *
     * @return true if the event can be skipped, false otherwise
     */
    public boolean canSkip() {
        return this.handlers.length == 0;
    }

    /**
     * Returns the invoker of the event.
     *
     * @return the invoker
     */
    public final T invoker() {
        return this.invoker;
    }
}
