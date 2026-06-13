/*
 * Copyright (C) 2026  404Setup.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package net.fabricmc.loader.impl.discovery;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.fabricmc.loader.impl.ModContainerImpl;
import net.fabricmc.loader.impl.game.GameProvider;
import net.fabricmc.loader.impl.launch.FabricLauncherBase;
import net.fabricmc.loader.impl.metadata.DependencyOverrides;
import net.fabricmc.loader.impl.metadata.LoaderModMetadata;
import net.fabricmc.loader.impl.metadata.ModMetadataParser;
import net.fabricmc.loader.impl.metadata.VersionOverrides;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Utility for dynamically loading mod JARs and adding them to the Fabric Loader.
 */
@SuppressWarnings("all")
public class PassLoader {
    private static final Logger logger = LoggerFactory.getLogger(PassLoader.class);
    private static final Field modMapField;
    private static final Field modsField;

    static {
        try {
            FabricLoaderImpl loader = (FabricLoaderImpl) FabricLoader.getInstance();
            Class<?> fabricLoaderClass = loader.getClass();
            modMapField = fabricLoaderClass.getDeclaredField("modMap");
            modsField = fabricLoaderClass.getDeclaredField("mods");
            modMapField.setAccessible(true);
            modsField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Failed to initialize PassLoader static fields", e);
        }
    }

    /**
     * Loads the given JAR files into the classpath.
     *
     * @param jarFiles  The JAR files to load.
     */
    public static void loadNestedJars(File... jarFiles) {
        try {
            for (File jarFile : jarFiles) {
                FabricLauncherBase.getLauncher().addToClassPath(jarFile.toPath());
                logger.info("Extracted and added nested JAR to classpath: {}", jarFile.getName());
            }
        } catch (Exception e) {
            logger.error("Failed to load nested JARs: {}", e.getMessage(), e);
        }
    }

    /**
     * Adds a mod container to the Fabric Loader's mod list.
     *
     * @param container  The mod container to add.
     * @return True if successful, false otherwise.
     */
    public static boolean addModContainer(ModContainerImpl container) {
        try {
            FabricLoaderImpl loader = (FabricLoaderImpl) FabricLoader.getInstance();

            List<ModContainerImpl> mods = (List<ModContainerImpl>) modsField.get(loader);
            Map<String, ModContainerImpl> modMap = (Map<String, ModContainerImpl>) modMapField.get(loader);

            mods.add(container);

            LoaderModMetadata metadata = container.getMetadata();
            String modId = metadata.getId();
            modMap.put(modId, container);

            logger.info("Successfully added mod container for {} to Fabric Loader", modId);
            return true;
        } catch (Exception e) {
            logger.error("Failed to add mod container to Fabric Loader: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Loads mod metadata from a JAR file.
     *
     * @param jarFile  The JAR file.
     * @return The mod metadata, or null if loading failed.
     */
    @Nullable
    public static LoaderModMetadata loadMetadataFromJar(@NotNull File jarFile) {
        try {
            try (ZipFile zip = new ZipFile(jarFile)) {
                ZipEntry entry = zip.getEntry("fabric.mod.json");
                if (entry == null) throw new IllegalArgumentException("No fabric.mod.json found in JAR");

                VersionOverrides versionOverrides = new VersionOverrides();

                DependencyOverrides depOverrides = new DependencyOverrides(Path.of(""));
                LoaderModMetadata metadata = ModMetadataParser.parseMetadata(
                        zip.getInputStream(entry),
                        jarFile.getName(),
                        List.of(),
                        versionOverrides,
                        depOverrides,
                        false
                );

                logger.info("Successfully loaded metadata from JAR: {}", metadata.getId());
                return metadata;
            }
        } catch (Exception e) {
            logger.error("Failed to load metadata from JAR: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Creates a mod candidate from the given metadata and JAR file.
     *
     * @param metadata  The mod metadata.
     * @param jarFile   The JAR file.
     * @return The mod candidate, or null if creation failed.
     */
    public static ModCandidateImpl createModCandidate(LoaderModMetadata metadata, File jarFile) {
        try {
            @NotNull ModCandidateImpl candidate = createPlain(
                    List.of(jarFile.toPath()),
                    metadata,
                    false,
                    List.of()
            );

            logger.info("Successfully created mod candidate for {} with JAR path: {}", metadata.getId(), jarFile.getAbsolutePath());
            return candidate;
        } catch (Exception e) {
            logger.error("Failed to create mod candidate: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Gets the ID and version comparator for mod candidates.
     *
     * @return The comparator.
     */
    @NotNull
    public static Comparator<ModCandidateImpl> getIDVersionComparator() {
        return ModCandidateImpl.ID_VERSION_COMPARATOR;
    }

    /**
     * Creates a builtin mod candidate.
     *
     * @param mod               The builtin mod definition.
     * @param versionOverrides  Version overrides.
     * @param depOverrides      Dependency overrides.
     * @return The mod candidate.
     */
    @NotNull
    @Contract("_,_,_->new")
    public static ModCandidateImpl createBuiltin(@NotNull GameProvider.BuiltinMod mod,
                                                 @NotNull VersionOverrides versionOverrides,
                                                 @NotNull DependencyOverrides depOverrides) {
        return ModCandidateImpl.createBuiltin(mod, versionOverrides, depOverrides);
    }

    /**
     * Creates a nested mod candidate.
     *
     * @param localPath      The local path.
     * @param hash           The hash.
     * @param metadata       Mod metadata.
     * @param requiresRemap  Whether remap is required.
     * @param nestedMods     Nested mod candidates.
     * @return The mod candidate.
     */
    @NotNull
    @Contract(value = "_,_,_,_,_->new", pure = true)
    public static ModCandidateImpl createNested(String localPath,
                                                long hash,
                                                LoaderModMetadata metadata,
                                                boolean requiresRemap,
                                                Collection<ModCandidateImpl> nestedMods) {
        return ModCandidateImpl.createNested(localPath, hash, metadata, requiresRemap, nestedMods);
    }

    /**
     * Creates a plain mod candidate.
     *
     * @param paths          The paths.
     * @param metadata       Mod metadata.
     * @param requiresRemap  Whether remap is required.
     * @param nestedMods     Nested mod candidates.
     * @return The mod candidate.
     */
    @NotNull
    @Contract(value = "_,_,_,_->new", pure = true)
    public static ModCandidateImpl createPlain(List<Path> paths,
                                               LoaderModMetadata metadata,
                                               boolean requiresRemap,
                                               Collection<ModCandidateImpl> nestedMods) {
        return ModCandidateImpl.createPlain(paths, metadata, requiresRemap, nestedMods);
    }

    /**
     * Computes the hash for the given ZIP entry.
     *
     * @param zipEntry  The ZIP entry.
     * @return The hash.
     */
    public static long hash(@NotNull ZipEntry zipEntry) {
        return ModCandidateImpl.hash(zipEntry);
    }
}
