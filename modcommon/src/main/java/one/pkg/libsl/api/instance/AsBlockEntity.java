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

import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;

/**
 * An interface representing a block entity that can be loaded with components.
 */
@SuppressWarnings("unused")
public interface AsBlockEntity {
    /**
     * Loads the block entity with components from a NBT tag.
     *
     * @param problemReporter the problem reporter
     * @param registryAccess the registry access
     * @param tag the NBT tag
     */
    void loadWithComponents(ProblemReporter problemReporter, RegistryAccess registryAccess, CompoundTag tag);
}
