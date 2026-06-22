/*
 * Copyright (C) 2026  404Setup.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package one.pkg.libsl.neoforge.loader;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.server.ServerLifecycleHooks;
import one.pkg.libsl.api.loader.ILoader;
import one.pkg.libsl.api.loader.Mod;
import one.pkg.libsl.api.loader.client.CLoader;
import one.pkg.libsl.api.network.INet;
import one.pkg.libsl.neoforge.loader.client.NFCLoader;
import one.pkg.libsl.neoforge.loader.network.NFNet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class NFLoader extends one.pkg.libsl.api.loader.CLoader implements ILoader {
    private final Logger logger = LoggerFactory.getLogger(NFLoader.class);
    private final INet networking = new NFNet();
    private final CLoader clientLoader = isClient() ? new NFCLoader(this) : null;

    @Override
    public Path getConfigPath() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public Path getGamePath() {
        return FMLPaths.GAMEDIR.get();
    }

    @Override
    public Path getModsPath() {
        return FMLPaths.MODSDIR.get();
    }

    @Override
    public @Nullable MinecraftServer server() {
        return ServerLifecycleHooks.getCurrentServer();
    }

    @Override
    public CLoader client() {
        return clientLoader;
    }

    @Override
    public boolean isClient() {
        return FMLEnvironment.dist.isClient();
    }

    @Override
    public boolean loaded(@NotNull String modid) {
        return ModList.get().isLoaded(modid);
    }

    @Override
    public Mod mod(@NotNull String modid) {
        var m = ModList.get().getModContainerById(modid).orElse(null);
        return m == null ? null : new NFMod(m.getModInfo());
    }

    @Override
    public @NotNull INet net() {
        return networking;
    }

    @Override
    public @NotNull ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }
}
