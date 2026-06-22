/*
 * Copyright (C) 2026  404Setup.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package one.pkg.libsl.neoforge;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.common.MinecraftForge;
import one.pkg.libsl.Static;
import one.pkg.libsl.api.event.command.CommandRegistrationEvent;
import one.pkg.libsl.api.loader.JavaLoader;
import one.pkg.libsl.internal.command.SLCommand;
import one.pkg.libsl.internal.network.InternalNetworkInit;
import one.pkg.libsl.neoforge.listener.BlockListener;
import one.pkg.libsl.neoforge.listener.ChannelRegisterListener;
import one.pkg.libsl.neoforge.listener.LifecycleListener;
import one.pkg.libsl.neoforge.listener.ServerLivingEntityListener;
import one.pkg.libsl.neoforge.listener.client.ClientChannelRegisterListener;
import one.pkg.libsl.neoforge.listener.client.ClientCommandListener;
import one.pkg.libsl.neoforge.listener.client.ClientLifecycleListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(Static.MOD_ID)
public class SpringLotusMod {
    Logger logger = LoggerFactory.getLogger(Static.MOD_NAME);

    public SpringLotusMod(IEventBus bus, ModContainer container) {
        CommandRegistrationEvent.EVENT.register(
                (dispatcher, ignored, selection) ->
                        SLCommand.register(dispatcher, selection)
        );

        MinecraftForge.EVENT_BUS.register(LifecycleListener.class);
        bus.register(ChannelRegisterListener.class);
        MinecraftForge.EVENT_BUS.register(new BlockListener());
        MinecraftForge.EVENT_BUS.register(new ServerLivingEntityListener());

        if (JavaLoader.INSTANCE.isClient()) {
            MinecraftForge.EVENT_BUS.register(ClientLifecycleListener.class);
            bus.register(ClientChannelRegisterListener.class);
            MinecraftForge.EVENT_BUS.register(ClientCommandListener.class);
        }

        InternalNetworkInit.init();
    }
}
