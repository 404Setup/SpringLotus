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

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;
import one.pkg.libsl.api.Vec3d;

/**
 * An interface representing an entity.
 */
@SuppressWarnings("unused")
public interface AsEntity {
    /**
     * Gets the position of the entity.      *      * @return the position
     */
    Vec3d getPos();

    /**
     * Loads the entity from a NBT tag.      *      * @param problemReporter the problem reporter      * @param registryAccess  the registry access      * @param tag             the NBT tag
     */
    void load(CompoundTag tag);

    /**
     * Teleports the entity to the specified position.      *      * @param pos The target position to teleport to.
     */
    void teleportTo(Vec3d pos);

    /**
     * Teleports the entity to the specified position.      *      * @param pos the target position to which the entity should be teleported
     */
    void teleportTo(Vec3 pos);
}
