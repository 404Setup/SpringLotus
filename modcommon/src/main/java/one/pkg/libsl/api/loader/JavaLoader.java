/*
 * Copyright (C) 2026  404Setup.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package one.pkg.libsl.api.loader;

import java.util.ServiceLoader;

/**
 * A utility class for loading services.
 */
@SuppressWarnings("all")
public class JavaLoader {
    /**
     * The singleton instance of the JavaLoader.
     */
    public static final ILoader INSTANCE = load(ILoader.class);

    private JavaLoader() {
    }

    /**
     * Loads a service for the specified class.
     *
     * @param clazz the class of the service
     * @param <T>   the type of the service
     * @return the service instance
     * @throws NullPointerException if the service fails to load
     */
    private static <T> T load(Class<T> clazz) {
        return ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
    }
}
