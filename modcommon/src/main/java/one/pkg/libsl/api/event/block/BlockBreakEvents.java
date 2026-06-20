/*
 * Copyright (C) 2026  404Setup.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package one.pkg.libsl.api.event.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import one.pkg.libsl.api.event.Event;

/**
 * Interface representing block break events and their associated handlers.
 * <p>
 * This interface organizes and handles events related to block-breaking or block
 * updates triggered by entities, allowing event-specific listeners to be registered
 * and notified accordingly. Listeners can intervene in the event process to determine
 * whether actions—such as block breaking or entity interaction—should proceed or
 * be canceled.
 */
public interface BlockBreakEvents {
    /**
     * The event instance for block break events.
     * <p>
     * This event is triggered when a block is broken by a player. It notifies
     * all registered listeners, allowing them to handle the block-breaking
     * action. Each listener's {@code onBlockBreak} method is invoked sequentially.
     * If any listener returns {@code false}, the block-breaking action is
     * cancelled.
     * <p>
     * The invoker is created using a combination of all registered listeners. This
     * ensures that each listener is given the opportunity to process the event.
     * <p>
     * Listeners should properly handle event parameters:
     * - {@code player}: The player attempting to break the block.
     * - {@code level}: The level where the block is being broken.
     * - {@code pos}: The position of the block being broken.
     * - {@code state}: The current state of the block being broken.
     * <p>
     * The result of the event depends on all listeners' responses:
     * - Returning {@code true} allows the block-breaking action.
     * - Returning {@code false} cancels the block-breaking action.
     */
    Event<PlayerBreak> PLAYER_BREAK = Event.create(PlayerBreak.class, callbacks -> (
            player, level, pos, state
    ) -> {
        for (PlayerBreak callback : callbacks) {
            if (!callback.onPlayerBreak(player, level, pos, state))
                return false;
        }
        return true;
    });

    /**
     * Event fired when an entity interacts with or updates a block.
     * <p>
     * This event is triggered to handle interactions or updates caused by an entity
     * in the context of a specific block. The event is processed by notifying all
     * registered {@link EntityUpdate} listeners in sequence. If any listener returns
     * {@code false}, further processing is halted, and the update is canceled.
     * <p>
     * The {@link EntityUpdate} interface must be implemented by listeners wishing to
     * handle this event. Listeners have access to information about the entity, the
     * level in which the event occurs, the block's position, and its current state.
     * <p>
     * Listeners can register themselves to this event to respond to relevant updates
     * appropriately. During the event handling, all registered listeners are invoked
     * in the order of registration.
     *
     * @see BlockBreakEvents
     * @see EntityUpdate
     */
    Event<EntityUpdate> ENTITY_UPDATE = Event.create(EntityUpdate.class, callbacks -> (
            (entity, level, pos, state) -> {
                for (EntityUpdate callback : callbacks) {
                    if (!callback.onEntityUpdate(entity, level, pos, state)) {
                        return false;
                    }
                }

                return true;
            }));

    @FunctionalInterface
    interface PlayerBreak {
        /**
         * Handles the event triggered when a block is broken by a player.
         *
         * @param player the player attempting to break the block
         * @param level  the level in which the block is being broken
         * @param pos    the position of the block being broken
         * @param state  the current state of the block being broken
         * @return true if the block-breaking action is allowed to proceed, false to cancel the action
         */
        boolean onPlayerBreak(
                Player player,
                LevelAccessor level,
                BlockPos pos,
                BlockState state
        );
    }

    /**
     * Functional interface for handling entity update events in a specific block context.
     * <p>
     * This interface is designed to be implemented by listeners that wish to respond
     * when an entity interacts with a block. The event provides information about the
     * entity, the level, the block position, and the block's state at the time of
     * the interaction.
     * <p>
     * The {@code onEntityUpdate} method will be called for each registered listener.
     * If any listener returns {@code false}, the update action is canceled, and no further
     * listeners are notified.
     *
     * @see Entity
     * @see BlockPos
     * @see LevelAccessor
     * @see BlockState
     */
    @FunctionalInterface
    interface EntityUpdate {
        /**
         * Handles the event triggered when an entity interacts with or updates within the context
         * of a specific block.
         *
         * @param entity        the entity involved in the update event
         * @param levelAccessor the level in which the block and entity reside
         * @param pos           the position of the block being interacted with
         * @param state         the current block state at the specified position
         * @return true if the entity update is allowed to proceed, false to cancel the update
         */
        boolean onEntityUpdate(
                Entity entity,
                LevelAccessor levelAccessor,
                BlockPos pos,
                BlockState state
        );
    }
}
