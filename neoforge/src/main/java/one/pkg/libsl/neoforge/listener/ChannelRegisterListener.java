/*
 * Copyright (C) 2026  404Setup.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package one.pkg.libsl.neoforge.listener;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import one.pkg.libsl.api.loader.JavaLoader;
import one.pkg.libsl.neoforge.loader.network.NFNet;

public class ChannelRegisterListener {
    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        var network = (NFNet) JavaLoader.INSTANCE.net();
        final PayloadRegistrar registrar = event.registrar("1");
        network.onPayloadRegistrar(registrar);
        final PayloadRegistrar registrar2 = registrar.optional();
        network.optional().onPayloadRegistrar(registrar2);
    }
}
