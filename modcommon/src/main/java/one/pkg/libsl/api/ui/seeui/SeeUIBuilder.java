/*
 * Copyright (C) 2026  404Setup.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package one.pkg.libsl.api.ui.seeui;

import net.minecraft.client.gui.screens.Screen;

/**
 * Builder for creating configuration screens.
 */
public class SeeUIBuilder {
    private Class<?> clazz;
    private Screen lastScreen;
    private Runnable onSaved;
    private boolean useOreUI = false;

    /**
     * Creates a new builder instance.
     *
     * @return A new builder.
     */
    public static SeeUIBuilder builder() {
        return new SeeUIBuilder();
    }

    /**
     * Sets the configuration class.
     *
     * @param clazz The configuration class.
     * @return This builder.
     */
    public SeeUIBuilder clazz(Class<?> clazz) {
        this.clazz = clazz;
        return this;
    }

    /**
     * Sets the previous screen.
     *
     * @param lastScreen The previous screen.
     * @return This builder.
     */
    public SeeUIBuilder lastScreen(Screen lastScreen) {
        this.lastScreen = lastScreen;
        return this;
    }

    /**
     * Sets the callback to run when settings are saved.
     *
     * @param onSaved The callback.
     * @return This builder.
     */
    public SeeUIBuilder onSaved(Runnable onSaved) {
        this.onSaved = onSaved;
        return this;
    }

    /**
     * Sets whether to use OreUI for the configuration screen.
     *
     * @param useOreUI True to use OreUI, false for standard UI.
     * @return This builder.
     */
    public SeeUIBuilder useOreUI(boolean useOreUI) {
        this.useOreUI = useOreUI;
        return this;
    }

    /**
     * Builds the configuration screen.
     *
     * @return The created screen.
     */
    public Screen build() {
        if (useOreUI) {
            return new OreUIConfigScreen(clazz, lastScreen, onSaved);
        }
        return new SeeUIConfigScreen(clazz, lastScreen, onSaved);
    }
}
