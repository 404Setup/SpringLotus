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

import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import one.pkg.libsl.Static;
import one.pkg.libsl.api.event.block.BlockPlaceEvent;

@Mod.EventBusSubscriber(modid = Static.MOD_ID)
public class BlockListener {
    @SubscribeEvent
    public void onEntityPlace(BlockEvent.EntityPlaceEvent event) {
        /*if (!*/BlockPlaceEvent.EVENT.invoker().onBlockPlace(
                event.getEntity(),
                event.getPos(),
                event.getLevel(),
                event.getBlockSnapshot().getCurrentBlock(),
                event.getPlacedBlock()
        );/*) {
            event.setCanceled(true);
        }*/
    }
}
