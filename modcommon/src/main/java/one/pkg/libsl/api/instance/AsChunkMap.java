/*
 * Copyright (C) 2026  404Setup.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package one.pkg.libsl.api.instance;

import net.minecraft.server.level.ChunkHolder;
import net.minecraft.world.level.ChunkPos;

/**
 * Represents a mapping mechanism for accessing visible chunks based on their unique keys
 * or positional coordinates. This interface provides methods to retrieve chunk information
 * for rendering or management operations.
 */
@SuppressWarnings("unused")
public interface AsChunkMap {
    /**
     * Retrieves the visible chunk associated with the given unique key.
     *
     * @param key the unique identifier of the chunk to retrieve
     * @return the ChunkHolder representing the visible chunk, or null if no chunk is associated with the provided key
     */
    ChunkHolder getVisibleChunk(long key);

    /**
     * Retrieves the visible chunk associated with the specified position.
     * This method is used to access chunk data for rendering or management
     * based on the provided chunk position.
     *
     * @param pos the position of the chunk to retrieve
     * @return the ChunkHolder representing the visible chunk at the given position,
     * or null if no chunk is available at the specified position
     */
    ChunkHolder getVisibleChunk(ChunkPos pos);
}
