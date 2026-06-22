/*
 * Copyright (C) 2026  404Setup.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package one.pkg.libsl.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.toasts.SystemToast;

import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.util.List;

@SuppressWarnings("unused")
public final class Toasts {
    static MethodHandle call;

    static {
        try {
            Constructor<SystemToast> constructor = SystemToast.class.getDeclaredConstructor(SystemToast.SystemToastIds.class, Component.class, List.class, int.class);
            constructor.setAccessible(true);

            call = MethodHandles.lookup().unreflectConstructor(constructor);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private Toasts() {
    }

    /**
     * Sends a multiline toast.
     *
     * @param title       The title of the toast.
     * @param description The description of the toast.
     * @param id          The id of the toast.
     * @return The created toast.
     *
     */
    public static SystemToast multiline(Component title, Component description, SystemToast.SystemToastIds id) {
        Font font = Minecraft.getInstance().font;
        List<FormattedCharSequence> list = font.split(description, 230);
        int max = 230;
        for (FormattedCharSequence seq : list) {
            max = Math.max(max, font.width(seq));
        }
        int i = max;
        //Math.max(160, 30 + Math.max(Minecraft.getInstance().font.width(title), message == null ? 0 : Minecraft.getInstance().font.width(message)))
        try {
            return (SystemToast) call.invoke(id, title, list, i + 30);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sends a toast.
     *
     * @param title       The title of the toast.
     * @param description The description of the toast.
     * @param id          The id of the toast.
     */
    private static void sendToast(Component title, Component description, SystemToast.SystemToastIds id) {
        Minecraft.getInstance().execute(() -> {
            ToastComponent manager = Minecraft.getInstance().getToasts();
            manager.addToast(multiline(title, description, id));
        });
    }
}
