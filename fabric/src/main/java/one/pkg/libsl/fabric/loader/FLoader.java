/*
 * Copyright (C) 2026  404Setup.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package one.pkg.libsl.fabric.loader;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.impl.launch.FabricLauncherBase;
import net.minecraft.server.MinecraftServer;
import one.pkg.libsl.api.loader.ILoader;
import one.pkg.libsl.api.loader.Mod;
import one.pkg.libsl.api.loader.client.CLoader;
import one.pkg.libsl.api.network.INet;
import one.pkg.libsl.fabric.loader.client.FCLoader;
import one.pkg.libsl.fabric.loader.network.FNet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Optional;

public class FLoader extends one.pkg.libsl.api.loader.CLoader implements ILoader {
    private final Logger logger = LoggerFactory.getLogger(FLoader.class);
    //private final INet networking = new FNet();
    //private final CLoader clientLoader = isClient() ? new FCLoader(this) : null;
    private MinecraftServer server;

    /**
     * Updates the current server instance.
     *
     * @param server The new server instance.
     */
    public void updateServer(@Nullable MinecraftServer server) {
        this.server = server;
    }

    @Override
    public Path getConfigPath() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public Path getGamePath() {
        return FabricLoader.getInstance().getGameDir();
    }

    @Override
    public Path getModsPath() {
        return FabricLoader.getInstance().getGameDir().resolve("mods");
    }

    @Override
    public @Nullable MinecraftServer server() {
        return server;
    }

    /*
    @Override
    public CLoader client() {
        return clientLoader;
    }*/

    @Override
    public boolean isClient() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }

    @Override
    public boolean loaded(@NotNull String modid) {
        return FabricLoader.getInstance().isModLoaded(modid);
    }

    @Override
    public @Nullable Mod mod(@NotNull String modid) {
        Optional<ModContainer> modContainer = FabricLoader.getInstance().getModContainer(modid);
        if (modContainer.isPresent()) {
            return new FMod(modContainer.get());
        }
        return null;
    }

    /*@Override
    public @NotNull INet net() {
        return networking;
    }*/

    @Override
    public @NotNull ClassLoader getClassLoader() {
        return FabricLauncherBase.getLauncher().getTargetClassLoader();
    }
}
