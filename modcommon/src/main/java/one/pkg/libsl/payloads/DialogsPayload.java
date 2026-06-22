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

import com.google.gson.Gson;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import one.pkg.libsl.api.ui.oreui.OreUIDialog;

import java.util.List;

@SuppressWarnings("unused")
public class DialogsPayload {
    /**
     * The type of the dialogs payload.
     */
    public static final ResourceLocation TYPE = new ResourceLocation("springlotus", "dialogs");


    private static final Gson GSON = new Gson();

    private final String dialogs;
    private final DialogPayload[] dialogPayloads;

    public DialogsPayload(String dialogs) {
        this.dialogs = dialogs;
        this.dialogPayloads = GSON.fromJson(dialogs, DialogPayload[].class);
    }

    public DialogsPayload(DialogPayload... payloads) {
        this.dialogs = GSON.toJson(payloads);
        this.dialogPayloads = payloads;
    }

    public DialogsPayload(List<DialogPayload> payloads) {
        this.dialogs = GSON.toJson(payloads);
        this.dialogPayloads = payloads.toArray(new DialogPayload[0]);
    }

    public void send(ServerPlayer player) {
        /*if (JavaLoader.INSTANCE.net().canSend(player, this.type())) {
            JavaLoader.INSTANCE.net().send(player, this);
        }*/
    }

    public String dialogs() {
        return dialogs;
    }

    public DialogPayload[] dialogPayloads() {
        return dialogPayloads;
    }

    public ResourceLocation type() {
        return TYPE;
    }

    public static class ClientHandle {
        private ClientHandle() {
        }

        public static void send(LocalPlayer player, DialogPayload payload) {
            //JavaLoader.INSTANCE.net().client().send(player, payload);
        }

        /**
         * Handles the dialog payload on the client.
         *
         * @param payload The dialog payload.
         */
        public static void handle(DialogsPayload payload) {
            if (payload.dialogPayloads != null && payload.dialogPayloads.length > 0) {
                showDialog(payload, 0, null);
            }
        }

        private static void showDialog(DialogsPayload payload, int index, Screen cachedScreen) {
            Minecraft.getInstance().execute(() -> {
                Screen currentScreen = Minecraft.getInstance().screen;
                Screen actualLastScreen = cachedScreen != null ? cachedScreen : currentScreen;
                OreUIDialog dialog = new OreUIDialog(Component.literal(payload.dialogPayloads[index].title()), actualLastScreen)
                        .content(Component.literal(payload.dialogPayloads[index].desc()))
                        .confirmText(Component.literal(index == payload.dialogPayloads.length - 1 ?
                                "Close" : "Next"))
                        .cancelText(Component.literal(index == 0 ? "Close" : "Previous"))
                        .onConfirm(() -> {
                            if (index < payload.dialogPayloads.length - 1) {
                                Minecraft.getInstance().setScreen(actualLastScreen);
                                showDialog(payload, index + 1, actualLastScreen);
                            }
                        })
                        .onCancel(() -> {
                            if (index > 0) {
                                Minecraft.getInstance().setScreen(actualLastScreen);
                                showDialog(payload, index - 1, actualLastScreen);
                            }
                        });
                Minecraft.getInstance().setScreen(dialog);
            });
        }
    }
}
