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

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import one.pkg.libsl.api.loader.client.CLoader;
import one.pkg.libsl.api.network.INet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

/**
 * The implementation of the Java loader.
 */
@SuppressWarnings("unused")
public interface ILoader {
    /**
     * Gets the name of the server mod.
     *
     * @return the server mod name
     */
    String getServerModName();

    /**
     * Sets the name of the server mod.
     *
     * @param name the new server mod name
     */
    void setServerModName(String name);

    /**
     * Gets the server motd
     *
     * @return the motd
     */
    Component getServerMotd();

    /**
     * sets the server motd
     *
     * @param component the motd
     */
    void setServerMotd(Component component);

    /**
     * Gets the path to the configuration directory.
     *
     * @return the configuration path
     */
    Path getConfigPath();

    /**
     * Gets the path to the game directory.
     *
     * @return the game path
     */
    Path getGamePath();

    /**
     * Gets the path to the mods directory.
     *
     * @return the mods path
     */
    Path getModsPath();

    /**
     * Gets the current Minecraft server instance.
     *
     * @return the server instance, or null if not available
     */
    @Nullable
    MinecraftServer server();

    /**
     * Gets the client loader instance.
     *
     * @return the client loader instance
     */
    //CLoader client();

    /**
     * Checks if the current environment is a client.
     *
     * @return true if it is a client, false otherwise
     */
    boolean isClient();

    /**
     * Checks if a mod with the specified ID is loaded.
     *
     * @param modid the mod ID
     * @return true if the mod is loaded, false otherwise
     */
    boolean loaded(@NotNull String modid);

    /**
     * Gets the mod instance for the specified ID.
     *
     * @param modid the mod ID
     * @return the mod instance, or null if not found
     */
    @Nullable
    Mod mod(@NotNull String modid);

    /*@NotNull
    INet net();*/

    /**
     * Gets the class loader used by the loader.
     *
     * @return the class loader
     */
    @NotNull
    ClassLoader getClassLoader();
}
