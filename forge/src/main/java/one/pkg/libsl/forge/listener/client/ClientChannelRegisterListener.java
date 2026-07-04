/*
 * Copyright (C) 2026  404Setup.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package one.pkg.libsl.forge.listener.client;

/**
 * Client-side channel registration for Forge.
 * <p>
 * In Forge 1.21, client-bound payload handlers are registered as part of the
 * main {@link net.minecraftforge.network.PayloadChannel} via
 * {@link one.pkg.libsl.forge.loader.network.FNet#buildChannel()}.
 * A separate client-side event listener is not required.
 * <p>
 * This class is kept as a placeholder for API symmetry with the NeoForge module.
 */
public class ClientChannelRegisterListener {
    // No-op: Forge's unified PayloadChannel handles both client and server payloads
    // in the BiConsumer passed to PayloadFlow#add(), with side detection via
    // CustomPayloadEvent.Context#isClientSide().
}
