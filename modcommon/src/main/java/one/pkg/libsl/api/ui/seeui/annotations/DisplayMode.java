/*
 * Copyright (C) 2026  404Setup.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package one.pkg.libsl.api.ui.seeui.annotations;

import one.pkg.libsl.api.ui.seeui.EntryMode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies how a configuration entry should be displayed in the UI.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DisplayMode {
    /**
     * Returns the interaction mode for the configuration entry.
     *
     * @return the interaction mode
     */
    EntryMode value() default EntryMode.TEXT;

    /**
     * Returns the values to cycle through when using {@link EntryMode#CYCLE}.
     *
     * @return an array of cycle values
     */
    String[] cycleValues() default {};
}