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

import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import one.pkg.libsl.Static;
import one.pkg.libsl.api.event.client.command.ClientCommandRegistrationEvent;

@Mod.EventBusSubscriber(modid = Static.MOD_ID)
public class ClientCommandListener {
    @SubscribeEvent
    public static void onClientCommandRegister(RegisterClientCommandsEvent event) {
        ClientCommandRegistrationEvent.EVENT.invoker().register(
                event.getDispatcher(), event.getBuildContext()
        );
    }
}
