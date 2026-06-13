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

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import one.pkg.libsl.api.event.Event;

/**
 * Functional interface representing an event triggered by block explosions in the game.
 * <p>
 * The {@code BlockExplosionEvent} interface is used to handle scenarios where blocks are
 * destroyed as a result of explosions. Implementations of this interface allow custom behaviors
 * to be defined when such events occur, such as altering the explosion mechanics, modifying
 * the affected blocks, or applying specific game rules to the event.
 * <p>
 * The event provides the following information:
 * - {@code owner}: The entity responsible for causing the explosion, if applicable.
 * - {@code level}: The game level (or world) where the explosion occurred.
 * - {@code explosions}: A list of block positions that are impacted or destroyed by the explosion.
 * <p>
 * Listeners implementing this interface must define the {@code onBlockExplosion} method to process
 * or modify the event behavior. This event instance facilitates dynamic registration of multiple
 * listeners.
 */
@FunctionalInterface
public interface BlockExplosionEvent {
    /**
     * Represents the event instance for handling block explosion events.
     * <p>
     * This event is triggered when one or more blocks are destroyed by an explosion in the game.
     * It allows listeners to respond to the explosion event by implementing the
     * {@code onBlockExplosion} method defined in the {@link BlockExplosionEvent} interface.
     * <p>
     * The event notifies all registered listeners sequentially and provides the following
     * parameters relevant to the explosion:
     * - {@code owner}: The entity responsible for the explosion, if applicable.
     * - {@code level}: The game level (world) in which the explosion occurred.
     * - {@code explosions}: A list of block positions affected by the explosion.
     * <p>
     * This event supports dynamic listener registration, and any number of listeners can be
     * attached to process or modify the explosion logic. The event is invoked for every block
     * explosion that takes place in the game.
     * <p>
     * Listeners can be used to handle tasks such as modifying the affected blocks, logging
     * explosion details, or applying game-specific effects.
     */
    Event<BlockExplosionEvent> EVENT = Event.create(BlockExplosionEvent.class, callbacks ->
            (owner, level, explosions) -> {
                for (BlockExplosionEvent callback : callbacks) {
                    callback.onBlockExplosion(owner, level, explosions);
                }
            });

    /**
     * Handles the event triggered when blocks are affected by an explosion.
     *
     * @param owner      the entity responsible for causing the explosion, may be null if not applicable
     * @param level      the level (world) where the explosion occurred
     * @param explosions the list of block positions impacted by the explosion
     */
    void onBlockExplosion(Entity owner, Level level, ObjectArrayList<BlockPos> explosions);
}
