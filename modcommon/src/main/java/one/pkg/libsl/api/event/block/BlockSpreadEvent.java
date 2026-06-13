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
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import one.pkg.libsl.api.event.Event;

/**
 * Represents an event triggered when a block is spread from one position to another.
 * <p>
 * This functional interface provides a mechanism for handling block-spreading behavior
 * through registered listeners. The {@code EVENT} field serves as the central instance
 * of the event, aggregating all registered listeners. Each listener's {@code onSpread}
 * method is invoked sequentially to process the spread event.
 * <p>
 * The event parameters are as follows:
 * - {@code level}: The level in which the block spread occurs.
 * - {@code newState}: The new block state resulting from the spread.
 * - {@code sourcePos}: The position of the source block causing the spread.
 * - {@code targetPos}: The position where the spread occurs.
 * <p>
 * Listeners can control the continuation of the block-spreading process:
 * - Returning {@code true} enables the spread to proceed.
 * - Returning {@code false} prevents the block spread.
 * <p>
 * The {@code EVENT} field uses an invoker to consolidate all listeners, ensuring that
 * each listener can process the event in sequence. If any listener returns {@code false},
 * the spread is canceled for that specific invocation.
 */
@FunctionalInterface
public interface BlockSpreadEvent {
    /**
     * Represents the central event dispatcher for block spread events.
     * <p>
     * This event is triggered whenever a block spreads from one position to another.
     * The event consolidates all registered listeners and invokes their handling methods.
     * If any listener returns false, the block spread is canceled.
     * <p>
     * The event's dispatch parameters are:
     * - {@code level}: The level in which the block spread occurs.
     * - {@code newState}: The new block state resulting from the spread.
     * - {@code sourcePos}: The position of the source block causing the spread.
     * - {@code targetPos}: The position where the spread occurs.
     */
    Event<BlockSpreadEvent> EVENT = Event.create(BlockSpreadEvent.class, callbacks ->
            (level, newState, sourcePos, targetPos) -> {
                for (BlockSpreadEvent callback : callbacks) {
                    if (!callback.onBlockSpread(level, newState, sourcePos, targetPos))
                        return false;
                }
                return true;
            });

    /**
     * Handles the event triggered when a block spreads from a source position to a target position.
     *
     * @param level     the level in which the block spread occurs
     * @param newState  the new block state resulting from the spread
     * @param sourcePos the position of the source block causing the spread
     * @param targetPos the position where the spread occurs
     * @return true if the block spread is allowed to proceed, false to cancel the block spread
     */
    boolean onBlockSpread(LevelAccessor level, BlockState newState, BlockPos sourcePos, BlockPos targetPos);
}
