/*
 * Copyright (C) 2026  404Setup.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package one.pkg.libsl.api.event.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

import one.pkg.libsl.api.event.Event;

/**
 * Defines events related to interactions within an inventory menu.
 * The {@code InventoryEvents} interface provides a mechanism to register
 * custom event listeners that handle specific interactions, offering a way
 * to modify or expand upon the default behavior of inventory operations.
 * <p>
 * Events exposed by this interface are designed to be extensible and allow
 * integration with additional gameplay logic, restriction mechanisms, or
 * other forms of customization.
 */
@SuppressWarnings("all")
public interface InventoryEvents {
    /**
     * Represents an event that is triggered when a click interaction is performed in an inventory menu.
     * This event allows registering callbacks to handle custom behavior in response to such interactions,
     * such as preventing the default action, modifying the interaction, or triggering additional gameplay logic.
     * <p>
     * The event processes all registered {@link InventoryEvents.Clicked} listeners in sequence.
     * Each listener can provide customized handling or cancel further processing of the event
     * by returning {@code false} from the {@code onClicked} method. If any listener returns {@code false},
     * the event is considered "cancelled," and no further callbacks are executed.
     * <p>
     * The parameters provided to the {@code onClicked} method include details about the menu being interacted with,
     * the slot index, button number, the input container, and the player performing the interaction.
     * <p>
     * Listeners are registered to this event via the {@link Event#register} method.
     */
    Event<Clicked> CLICKED = Event.create(Clicked.class, callbacks -> (
            (menu, slotIndex, buttonNum, containerInput, player) -> {
                for (Clicked callback : callbacks) {
                    if (!callback.onClicked(menu, slotIndex, buttonNum, containerInput, player)) {
                        return false;
                    }
                }
                return true;
            }
    ));

    /**
     * Represents a functional interface for handling click interactions within an inventory menu.
     * <p>
     * This interface is used to define custom behaviors that should occur when a user interacts with
     * an inventory menu by clicking on a specific slot. Implementations of this interface can modify
     * the behavior of the interaction, enforce restrictions, or trigger additional actions.
     * <p>
     * The method {@code onClicked} is called when the `Clicked` event is fired, passing detailed
     * information about the click interaction. Each listener processes these parameters and may
     * decide to cancel the event by returning {@code false}. If any listener cancels the event,
     * further listeners are not invoked.
     * <p>
     * This interface is intended to be used as part of the {@code InventoryEvents.CLICKED} event
     * workflow. Listeners are registered to the event using the {@code Event.register} method,
     * allowing for extensible and dynamic handling of click interactions.
     */
    interface Clicked {
        /**
         * Handles a click interaction in an inventory menu. This method is triggered when an inventory
         * slot is clicked by a player and allows for custom logic to be executed in response to the interaction.
         * <p>
         * Returning {@code false} cancels further processing of the click event. If a listener cancels the
         * event, no other listeners are invoked.
         *
         * @param menu           The inventory menu being interacted with. Represents the current container context.
         * @param slotIndex      The index of the slot that was clicked within the menu.
         * @param buttonNum      The button number used during the click interaction. Typically represents which mouse
         *                       button was pressed or other interaction methods.
         * @param containerInput The input data associated with the click action. Provides additional details
         *                       about the interaction.
         * @param player         The player who performed the click interaction.
         * @return {@code true} if the event should proceed with its default behavior and allow further
         * event listeners to be notified. {@code false} cancels the click event and prevents
         * further listeners from being invoked.
         */
        boolean onClicked(AbstractContainerMenu menu, int slotIndex,
                          int buttonNum, Object containerInput, Player player);
    }
}
