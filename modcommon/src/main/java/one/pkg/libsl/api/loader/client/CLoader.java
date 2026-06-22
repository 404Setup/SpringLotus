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

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import one.pkg.libsl.api.network.ICNet;

import java.util.function.Function;

/**
 * The client loader interface.
 */
public interface CLoader {
    /**
     * Sets the screen.
     *
     * @param function the function to apply to the current screen
     */
    default void setScreen(Function<Screen, Screen> function) {
        Minecraft.getInstance().execute(() ->
                Minecraft.getInstance().setScreen(function.apply(Minecraft.getInstance().screen)));
    }

    /**
     * Gets the client network implementation.
     *
     * @return the client network implementation
     */
    //ICNet net();
}
