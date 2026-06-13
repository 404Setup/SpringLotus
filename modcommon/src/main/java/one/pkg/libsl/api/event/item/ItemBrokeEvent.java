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

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import one.pkg.libsl.api.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents an event that is triggered when an item breaks during its usage.
 * This functional interface allows for implementing custom behavior when an item
 * is broken, such as logging, modifying gameplay mechanics, or triggering other actions.
 * <p>
 * The event can propagate through multiple registered listeners. Each listener
 * is executed sequentially, allowing for custom handling or cancellation of
 * subsequent event logic.
 */
@FunctionalInterface
public interface ItemBrokeEvent {
    /**
     * Represents a global event that fires whenever an item is broken during use.
     * This event is used to register callbacks that handle custom logic or behavior
     * in response to an item break event.
     *
     * <p>The event processes all registered {@link ItemBrokeEvent} listeners in sequence.
     * Each listener can implement custom handling logic by overriding the {@code onItemBroken}
     * method, which receives the player responsible for the action (if applicable) and the item
     * that was broken.
     *
     * <p>Listeners are registered to this event via the {@link Event#register} method.
     */
    Event<ItemBrokeEvent> EVENT = Event.create(ItemBrokeEvent.class, callbacks ->
            (usedPlayer, broken) -> {
                for (ItemBrokeEvent callback : callbacks) {
                    callback.onItemBroken(usedPlayer, broken);
                }
            });

    /**
     * Handles the event when an item breaks during usage.
     * This method is typically invoked to trigger custom behavior such as notifying
     * the player, logging, or modifying gameplay mechanics when an item reaches its
     * breaking point.
     *
     * @param usedPlayer The player who was using the item when it broke.
     *                   Can be null, indicating a non-player context such as an automated process.
     * @param brokenItem The item that has broken. Must not be null.
     */
    void onItemBroken(@Nullable ServerPlayer usedPlayer, @NotNull Item brokenItem);
}
