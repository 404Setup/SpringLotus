/*
 * Copyright (C) 2026  404Setup.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package one.pkg.libsl.api.loader.client;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import one.pkg.libsl.api.ui.seeui.SeeUIBuilder;

/**
 * A GUI screen for sharing configurations.
 */
@SuppressWarnings("unused")
public class SharedConfigGUI extends Screen {
    private final SeeUIBuilder builder;

    /**
     * Constructs a new SharedConfigGUI with the specified builder.
     *
     * @param builder the UI builder
     */
    public SharedConfigGUI(SeeUIBuilder builder) {
        super(Component.empty());
        this.builder = builder;
    }

    /**
     * Constructs a new SharedConfigGUI with the specified parent screen, class, and save callback.
     *
     * @param parent the parent screen
     * @param clazz the configuration class
     * @param onSaved the callback to run when saved
     */
    public SharedConfigGUI(Screen parent, Class<?> clazz, Runnable onSaved) {
        this(SeeUIBuilder.builder().clazz(clazz).lastScreen(parent).onSaved(onSaved));
    }

    /**
     * Gets a new instance of SharedConfigGUI.
     *
     * @param builder the UI builder
     * @return a new SharedConfigGUI instance
     */
    public static SharedConfigGUI get(SeeUIBuilder builder) {
        return new SharedConfigGUI(builder);
    }

    /**
     * Gets a new instance of SharedConfigGUI.
     *
     * @param parent the parent screen
     * @param clazz the configuration class
     * @param onSaved the callback to run when saved
     * @return a new SharedConfigGUI instance
     */
    public static SharedConfigGUI get(Screen parent, Class<?> clazz, Runnable onSaved) {
        return new SharedConfigGUI(parent, clazz, onSaved);
    }

    @Override
    protected void init() {
        minecraft.setScreen(builder.build());
    }
}