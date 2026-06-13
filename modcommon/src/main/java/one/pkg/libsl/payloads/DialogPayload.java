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

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import one.pkg.libsl.api.loader.JavaLoader;
import one.pkg.libsl.api.ui.oreui.OreUIDialog;
import one.pkg.libsl.internal.InternalShared;
import org.jspecify.annotations.NonNull;

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
) implements CustomPacketPayload {
    /**
     * The type of the dialog payload.
     */
    public static final CustomPacketPayload.Type<DialogPayload> TYPE = InternalShared.type("dialog");

    /**
     * The codec for the dialog payload.
     */
    public static final StreamCodec<FriendlyByteBuf, DialogPayload> CODEC =
            StreamCodec.composite(ByteBufCodecs.STRING_UTF8, DialogPayload::title,
                    ByteBufCodecs.STRING_UTF8,
                    DialogPayload::desc,
                    ByteBufCodecs.BOOL,
                    DialogPayload::showCanceled,
                    DialogPayload::new);

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
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
            JavaLoader.INSTANCE.client()
                    .setScreen((lastScreen) ->
                            new OreUIDialog(Component.literal(payload.title()), lastScreen)
                                    .content(Component.literal(payload.desc()))
                                    .showCancel(payload.showCanceled())
                    );
        }
    }
}
