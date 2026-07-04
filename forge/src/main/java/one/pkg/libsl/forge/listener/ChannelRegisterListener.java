/*
 * Copyright (C) 2026  404Setup.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package one.pkg.libsl.forge.listener;

import one.pkg.libsl.api.loader.JavaLoader;
import one.pkg.libsl.forge.loader.network.FNet;

/**
 * Triggers the construction of the Forge network channels.
 * <p>
 * In Forge 1.21, payloads must be registered at startup via
 * {@link net.minecraftforge.network.ChannelBuilder} rather than through an event.
 * Call {@link #register()} once all payloads have been registered on the {@link FNet} instance.
 */
public class ChannelRegisterListener {

    /**
     * Builds the Forge {@link net.minecraftforge.network.Channel}s for both the
     * main and optional network instances after all payloads have been declared.
     */
    public static void register() {
        var network = (FNet) JavaLoader.INSTANCE.net();
        network.buildChannel();
        network.optional().buildChannel();
    }
}
