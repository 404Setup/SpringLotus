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

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.server.*;
import one.pkg.libsl.api.event.lifecycle.ServerLevelEvents;
import one.pkg.libsl.api.event.lifecycle.ServerLifecycleEvents;


public class LifecycleListener {
    @SubscribeEvent
    public static void onLevelLoad(LevelEvent.Load event) {
        ServerLevelEvents.LOAD.invoker().onLevelLoad(event.getLevel().getServer(),
                event.getLevel());
    }

    @SubscribeEvent
    public static void onLevelUnload(LevelEvent.Unload event) {
        ServerLevelEvents.UNLOAD.invoker().onLevelUnload(event.getLevel().getServer(),
                event.getLevel());
    }

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        ServerLifecycleEvents.STARTING.invoker().onServerStarting(event.getServer());
    }

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        ServerLifecycleEvents.STARTED.invoker().onServerStarted(event.getServer());
    }

    @SubscribeEvent
    public static void onServerAboutStarting(ServerAboutToStartEvent event) {
        ServerLifecycleEvents.ABOUT_STARTING.invoker().onServerAboutStarting(event.getServer());
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        ServerLifecycleEvents.STOPPING.invoker().onServerStopping(event.getServer());
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        ServerLifecycleEvents.STOPPED.invoker().onServerStopped(event.getServer());
    }
}
