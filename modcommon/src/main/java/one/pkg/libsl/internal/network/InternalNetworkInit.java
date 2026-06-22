/*
 * Copyright (C) 2026  404Setup.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package one.pkg.libsl.internal.network;

import one.pkg.libsl.api.loader.JavaLoader;

/**
 * Internal class for initializing networking.
 */
public class InternalNetworkInit {
    private InternalNetworkInit() {
    }

    /**
     * Initializes the networking.
     */
    public static void init() {
        //INet netImpl = JavaLoader.INSTANCE.net();

        if (JavaLoader.INSTANCE.isClient()) ClientInit.init();
    }

    /**
     * Internal class for client-side networking initialization.
     */
    public static class ClientInit {
        private ClientInit() {
        }

        /**
         * Initializes client-side networking handlers.
         */
        public static void init() {
            /*INet netImpl = JavaLoader.INSTANCE.net();

            netImpl.optional().clientHandler(DialogPayload.TYPE,
                    (ctx, payload) -> () ->
                            DialogPayload.ClientHandle.handle((DialogPayload) payload)
            );
            netImpl.optional().clientHandler(DialogsPayload.TYPE,
                    (ctx, payload) -> () ->
                            DialogsPayload.ClientHandle.handle((DialogsPayload) payload)
            );*/
        }
    }
}
