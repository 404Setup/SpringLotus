/*
 * Copyright (C) 2026  404Setup.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package one.pkg.libsl.payloads;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import one.pkg.libsl.api.ui.oreui.OreUIDialog;

/**
 * A packet payload for showing a dialog on the client.
 *
 * @param title        The dialog title.
 * @param desc         The dialog description.
 * @param showCanceled Whether to show the cancel button.
 */
public record DialogPayload(
        String title,
        String desc,
        boolean showCanceled
) {
    /**
     * The type of the dialog payload.
     */
    public static final net.minecraft.resources.ResourceLocation TYPE = new net.minecraft.resources.ResourceLocation("springlotus", "dialog");


    public net.minecraft.resources.ResourceLocation type() {
        return TYPE;
    }

    public static class ClientHandle {
        private ClientHandle() {
        }

        /**
         * Handles the dialog payload on the client.
         *
         * @param payload The dialog payload.
         */
        public static void handle(DialogPayload payload) {
            Minecraft.getInstance().execute(() -> {
                Screen lastScreen = Minecraft.getInstance().screen;
                Minecraft.getInstance().setScreen(new OreUIDialog(Component.literal(payload.title()), lastScreen)
                        .content(Component.literal(payload.desc()))
                        .showCancel(payload.showCanceled()));
            });
        }
    }
}
