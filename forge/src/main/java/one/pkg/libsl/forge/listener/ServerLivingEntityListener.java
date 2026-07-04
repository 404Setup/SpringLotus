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

import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import one.pkg.libsl.Static;
import one.pkg.libsl.api.event.entity.ServerLivingEntityEvents;

@Mod.EventBusSubscriber(modid = Static.MOD_ID)
public class ServerLivingEntityListener {
    @SubscribeEvent
    public static void onLivingEntityDeath(LivingDeathEvent event) {
        ServerLivingEntityEvents.AFTER_DEATH.invoker().afterDeath(event.getEntity(), event.getSource());
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        ServerLivingEntityEvents.AFTER_DAMAGE.invoker().afterDamage(
                event.getEntity(),
                event.getSource(),
                event.getAmount(),
                event.getAmount(),
                event.getAmount() != 0f
        );
    }
}
