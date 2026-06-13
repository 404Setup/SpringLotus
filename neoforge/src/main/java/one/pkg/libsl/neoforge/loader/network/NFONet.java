/*
 * Copyright (C) 2026  404Setup.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package one.pkg.libsl.neoforge.loader.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

/**
 * Optional networking implementation for NeoForge.
 */
@SuppressWarnings("unchecked")
public class NFONet extends NFNet {
    private final NFNet parent;

    /**
     * Constructs an {@code Optional} networking instance.
     *
     * @param parent the parent {@code NeoForgeNetworking} instance
     */
    public NFONet(NFNet parent) {
        super((Void) null);
        this.parent = parent;
    }

    @Override
    public NFONet optional() {
        return this;
    }

    @Override
    public void handle(@NotNull CustomPacketPayload payload, @NotNull IPayloadContext context) {
        super.handle(payload, context);
    }
}
