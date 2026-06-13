/*
 * Copyright (C) 2026  404Setup.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package one.pkg.libsl.mixin.access;

import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Accessor interface for {@link ServerboundMovePlayerPacket}.
 */
@Mixin(ServerboundMovePlayerPacket.class)
public interface ServerboundMovePlayerPacketAccess {
    /**
     * Gets the yaw rotation from the packet.
     *
     * @return The yaw rotation.
     */
    @Accessor
    float getYRot();

    /**
     * Gets the pitch rotation from the packet.
     *
     * @return The pitch rotation.
     */
    @Accessor
    float getXRot();

    /**
     * Gets the x-coordinate from the packet.
     *
     * @return The x-coordinate.
     */
    @Accessor
    double getX();

    /**
     * Gets the y-coordinate from the packet.
     *
     * @return The y-coordinate.
     */
    @Accessor
    double getY();

    /**
     * Gets the z-coordinate from the packet.
     *
     * @return The z-coordinate.
     */
    @Accessor
    double getZ();

}
