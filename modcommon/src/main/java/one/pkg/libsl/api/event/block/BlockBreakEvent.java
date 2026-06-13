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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import one.pkg.libsl.api.event.Event;

/**
 * Represents an event fired when a block is broken by a player.
 * <p>
 * This functional interface provides a mechanism for handling block-breaking actions
 * through registered listeners. Each registered listener implements the {@code onBlockBreak}
 * method, which determines whether the block-breaking action should proceed or be canceled.
 * The event is processed sequentially among all listeners.
 */
@FunctionalInterface
public interface BlockBreakEvent {
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
    Event<BlockBreakEvent> EVENT = Event.create(BlockBreakEvent.class, callbacks -> (
            player, level, pos, state
    ) -> {
        for (BlockBreakEvent callback : callbacks) {
            if (!callback.onBlockBreak(player, level, pos, state))
                return false;
        }
        return true;
    });

    /**
     * Handles the event triggered when a block is broken by a player.
     *
     * @param player the player attempting to break the block
     * @param level  the level in which the block is being broken
     * @param pos    the position of the block being broken
     * @param state  the current state of the block being broken
     * @return true if the block-breaking action is allowed to proceed, false to cancel the action
     */
    boolean onBlockBreak(
            Player player,
            LevelAccessor level,
            BlockPos pos,
            BlockState state
    );
}
