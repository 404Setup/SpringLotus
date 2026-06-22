/*
 * Copyright (C) 2026  404Setup.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package one.pkg.libsl.internal;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import one.pkg.libsl.Static;

/**
 * Internal utility class for shared functionality.
 */
@SuppressWarnings("unused")
public class InternalShared {
    /**
     * Creates an identifier with the mod's namespace.
     *
     * @param id The path for the identifier.
     * @return The identifier.
     */
    public static ResourceLocation of(String id) {
        return ResourceLocation.fromNamespaceAndPath(Static.MOD_ID, id);
    }

    /**
     * Creates a custom packet payload type.
     *
     * @param id  The identifier for the type.
     * @param <T> The payload type.
     * @return The payload type instance.
     */
    public static net.minecraft.resources.ResourceLocation type(String id) {
        return new net.minecraft.resources.ResourceLocation("springlotus", id);
    }

    /**
     * Sends dialog data to the specified player.
     *
     * @param player The player.
     */
    public static void sendDialog(ServerPlayer player) {
        /*for (Map.Entry<ResourceLocation, ModConfig> entry : ConfigRegistry.getConfigEntries()) {
            ResourceLocation location = entry.getKey();
            ModConfig config = entry.getValue();

            if (location.toString().contains("client")) continue;
            ConfigConstants.LOG.info("Sending data for Config '{}'", location);
            ConfigNetwork.INSTANCE.sendToClient(player, new ConfigSyncPayload(location.toString(), config.toJson().toString()));
        }*/
    }
}
