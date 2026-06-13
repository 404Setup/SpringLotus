/*
 * Copyright (C) 2026  404Setup.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package one.pkg.libsl.fabric.mixin;

import one.pkg.libsl.api.loader.JavaLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Mixin configuration plugin for conditional mixin application.
 */
public class MixinPlugin implements IMixinConfigPlugin {
    private final Logger logger = LoggerFactory.getLogger("LibSL MixinBootstrap");

    /**
     * Default constructor.
     */
    public MixinPlugin() {
    }

    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return "";
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        CONFIG config = CONFIG.find(mixinClassName);
        boolean enabled = config == null || config.isEnabled();

        if (enabled) {
            Compatibility compatibility = Compatibility.find(mixinClassName);
            if (compatibility != null && JavaLoader.INSTANCE.loaded(compatibility.modId)) {
                if (compatibility.type == CompatibilityType.DISABLE || compatibility.type == CompatibilityType.ChangeMixinTarget) {
                    enabled = false;
                }
            } else {
                Compatibility alternative = Compatibility.findAlternative(mixinClassName);
                if (alternative != null) {
                    enabled = JavaLoader.INSTANCE.loaded(alternative.modId);
                }
            }
        }

        logger.info("Mixin {} {}", mixinClassName, enabled ? "enabled" : "disabled");
        return enabled;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return List.of();
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    /**
     * Compatibility settings for mixins.
     */
    enum Compatibility {
        ;

        public final String mixinClass;
        public final String modId;
        public final CompatibilityType type;
        public final String alternativeMixin;

        Compatibility(String mixinClass, String modId, CompatibilityType type) {
            this(mixinClass, modId, type, null);
        }

        Compatibility(String mixinClass, String modId, CompatibilityType type, String alternativeMixin) {
            this.mixinClass = mixinClass;
            this.modId = modId;
            this.type = type;
            this.alternativeMixin = alternativeMixin;
        }

        /**
         * Finds compatibility settings for the given mixin class name.
         *
         * @param mixinClassName  The mixin class name.
         * @return The compatibility settings, or null if not found.
         */
        @Nullable
        public static Compatibility find(String mixinClassName) {
            for (Compatibility value : values()) {
                if (value.mixinClass.equals(mixinClassName)) {
                    return value;
                }
            }
            return null;
        }

        /**
         * Finds compatibility settings for an alternative mixin class name.
         *
         * @param mixinClassName  The mixin class name.
         * @return The compatibility settings, or null if not found.
         */
        @Nullable
        public static Compatibility findAlternative(String mixinClassName) {
            for (Compatibility value : values()) {
                if (value.type == CompatibilityType.ChangeMixinTarget && mixinClassName.equals(value.alternativeMixin)) {
                    return value;
                }
            }
            return null;
        }
    }

    /**
     * Types of compatibility actions.
     */
    enum CompatibilityType {
        DISABLE, ChangeMixinTarget
    }

    /**
     * Configuration settings for mixins.
     */
    enum CONFIG {
        ;

        public final String CLASS;
        public final Supplier<Boolean> configTarget;

        CONFIG(String clazz, @NotNull Supplier<Boolean> configTarget) {
            this.CLASS = clazz;
            this.configTarget = configTarget;
        }

        /**
         * Finds configuration settings for the given class name.
         *
         * @param clazz  The class name.
         * @return The configuration settings, or null if not found.
         */
        @Nullable
        public static CONFIG find(String clazz) {
            for (CONFIG config : values()) {
                if (config.CLASS.equals(clazz)) return config;
            }
            return null;
        }

        /**
         * Checks if the mixin is enabled in the configuration.
         *
         * @return True if enabled, false otherwise.
         */
        public boolean isEnabled() {
            return this.configTarget.get();
        }
    }
}