/*
 * Copyright (C) 2026  404Setup.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package one.pkg.libsl.internal.config;

import one.pkg.config.SewliaConfig;
import one.pkg.config.annotation.config.ConfigEntry;
import one.pkg.config.annotation.config.ConfigTarget;
import one.pkg.config.metadata.ConfigMeta;
import one.pkg.libsl.api.loader.JavaLoader;

@ConfigEntry("SpringLotus")
public class SLConfig {
    public static final SewliaConfig config;

    @ConfigTarget(
            group = "debug", value = "event",
            comment = "When enabled, all test events will be registered on next startup"
    )
    public boolean debugEvent = false;

    static {
        config = new SewliaConfig(ConfigMeta.of(
                SLConfig.class,
                JavaLoader.INSTANCE.getConfigPath().resolve("springlotus.yaml"))
        );
    }

    private SLConfig() {
    }
}
