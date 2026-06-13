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

/**
 * Defines the available interaction modes for configuration entries in the UI.
 */
public enum EntryMode {
    /**
     * A simple text input field.
     */
    TEXT,
    /**
     * A button that cycles through a set of predefined values.
     */
    CYCLE,
    /**
     * A slider for selecting a value within a range.
     */
    SLIDER,
    /**
     * A group of buttons where each button represents a specific value.
     */
    BUTTON_GROUP
}