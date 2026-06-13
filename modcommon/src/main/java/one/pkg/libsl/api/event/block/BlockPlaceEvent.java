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
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import one.pkg.libsl.api.event.Event;

/**
 * Event for when a block is placed by an entity.
 */
@FunctionalInterface
public interface BlockPlaceEvent {
    /**
     * The event instance for block placement events.
     */
    Event<BlockPlaceEvent> EVENT = Event.create(BlockPlaceEvent.class, callbacks -> (
            entity, pos, level, older, placed
    ) -> {
        for (BlockPlaceEvent callback : callbacks) {
            if (!callback.onBlockPlace(entity, pos, level, older, placed)) return false;
        }
        return true;
    });

    /**
     * Called when a block is placed.
     *
     * @param entity     the entity that placed the block
     * @param pos        the position of the block
     * @param level      the level where the block was placed
     * @param older      the old block state
     * @param placed     the new block state
     * @return true to allow the block placement, false to cancel the event
     */
    boolean onBlockPlace(Entity entity, BlockPos pos, LevelAccessor level,
                      BlockState older, BlockState placed);
}
