/*
 * Copyright (C) 2026  404Setup.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package one.pkg.libsl.api.instance;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import one.pkg.libsl.api.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

/**
 * Interface for objects that can act as a Level (World) and provide entity lookup.
 */
@SuppressWarnings("unused")
public interface AsLevel {
    /**
     * Gets the dimension type of the level.
     *
     * @return the dimension type
     */
    String getName();

    /**
     * Gets the chunk at the specified position.
     *
     * @param pos    the chunk position
     * @param forced whether to force load the chunk
     * @return the chunk at the specified position
     */
    boolean updateChunkForced(ChunkPos pos, boolean forced);

    /**
     * Gets the chunk at the specified position.
     *
     * @param pos    the chunk position
     * @param forced whether to force load the chunk
     * @return the chunk at the specified position
     */
    boolean updateChunkForced(long pos, boolean forced);

    /**
     * Checks whether the chunk at the specified position is loaded.
     *
     * @param pos The position of the chunk, typically represented as a long value
     *            encoding chunk coordinates.
     * @return A CompletableFuture that completes with true if the chunk is loaded,
     * or false otherwise.
     */
    CompletableFuture<Boolean> isChunkLoaded(long pos);

    /**
     * Checks asynchronously if the specified chunk is loaded.
     *
     * @param pos the position of the chunk to check
     * @return a CompletableFuture that will complete with {@code true} if the chunk is loaded,
     * or {@code false} otherwise
     */
    CompletableFuture<Boolean> isChunkLoaded(ChunkPos pos);

    /**
     * Checks if the chunk at the specified coordinates is loaded.
     *
     * @param x the x-coordinate of the chunk
     * @param z the z-coordinate of the chunk
     * @return a CompletableFuture containing a boolean indicating whether the chunk is loaded
     */
    CompletableFuture<Boolean> isChunkLoaded(int x, int z);

    /**
     * Gets entities within a bounding box.
     *
     * @param aabb   the bounding box to search within
     * @param filter a predicate to filter entities
     * @return a list of matching entities
     */
    List<Entity> getNearbyEntities(@NotNull AABB aabb, @Nullable Predicate<? super Entity> filter);

    /**
     * Gets entities within a radius around a position.
     *
     * @param vec3d  the center position
     * @param x      the x-axis radius
     * @param y      the y-axis radius
     * @param z      the z-axis radius
     * @param filter a predicate to filter entities
     * @return a list of matching entities
     */
    List<Entity> getNearbyEntities(Vec3d vec3d, double x, double y, double z, Predicate<? super Entity> filter);

    /**
     * Gets entities within a radius around a position.
     *
     * @param vec3d the center position
     * @param x     the x-axis radius
     * @param y     the y-axis radius
     * @param z     the z-axis radius
     * @return a list of matching entities
     */
    default List<Entity> getNearbyEntities(@NotNull Vec3d vec3d, double x, double y, double z) {
        return getNearbyEntities(vec3d, x, y, z, null);
    }

    /**
     * Gets entities of a specific type within a radius around a position.
     *
     * @param <T>       the type of entities to search for
     * @param clazz     the class object of the entity type
     * @param vec3d     the center position
     * @param xRadius   the x-axis radius
     * @param yRadius   the y-axis radius
     * @param zRadius   the z-axis radius
     * @param predicate a predicate to filter entities
     * @return a list of matching entities
     */
    @SuppressWarnings("unchecked")
    default <T extends Entity> @NotNull List<T> getNearbyEntitiesByType(
            @Nullable Class<? extends T> clazz,
            @NotNull Vec3d vec3d,
            double xRadius,
            double yRadius,
            double zRadius,
            @Nullable Predicate<? super T> predicate
    ) {
        if (this instanceof LevelAccessor levelAccessor && clazz != null) {
            AABB aabb = new AABB(
                    vec3d.getX() - xRadius, vec3d.getY() - yRadius,
                    vec3d.getZ() - zRadius, vec3d.getX() + xRadius,
                    vec3d.getY() + yRadius, vec3d.getZ() + zRadius
            );
            return levelAccessor.getEntitiesOfClass((Class<T>) clazz, aabb, predicate != null ? predicate : com.google.common.base.Predicates.alwaysTrue());
        }
        Predicate<Entity> filter = entity -> {
            if (clazz != null && !clazz.isInstance(entity)) return false;
            return predicate == null || predicate.test((T) entity);
        };
        return (List<T>) this.getNearbyEntities(vec3d, xRadius, yRadius, zRadius, filter);
    }

    /**
     * Gets living entities within a radius around a position.
     *
     * @param vec3d  the center position
     * @param radius the search radius
     * @return a list of matching living entities
     */
    default @NotNull List<LivingEntity> getNearbyLivingEntities(@NotNull Vec3d vec3d, double radius) {
        return this.getNearbyEntitiesByType(LivingEntity.class, vec3d, radius, radius, radius);
    }

    /**
     * Gets living entities within a specified xz and y radius around a position.
     *
     * @param vec3d    the center position
     * @param xzRadius the x and z axis radius
     * @param yRadius  the y-axis radius
     * @return a list of matching living entities
     */
    default @NotNull List<LivingEntity> getNearbyLivingEntities(
            @NotNull Vec3d vec3d,
            double xzRadius,
            double yRadius
    ) {
        return this.getNearbyEntitiesByType(LivingEntity.class, vec3d, xzRadius, yRadius, xzRadius);
    }

    /**
     * Gets living entities within a specified x, y, and z radius around a position.
     *
     * @param vec3d   the center position
     * @param xRadius the x-axis radius
     * @param yRadius the y-axis radius
     * @param zRadius the z-axis radius
     * @return a list of matching living entities
     */
    default @NotNull List<LivingEntity> getNearbyLivingEntities(
            @NotNull Vec3d vec3d,
            double xRadius,
            double yRadius,
            double zRadius
    ) {
        return this.getNearbyEntitiesByType(LivingEntity.class, vec3d, xRadius, yRadius, zRadius);
    }

    /**
     * Gets living entities within a radius around a position, with a filter.
     *
     * @param vec3d     the center position
     * @param radius    the search radius
     * @param predicate a predicate to filter entities
     * @return a list of matching living entities
     */
    default @NotNull List<LivingEntity> getNearbyLivingEntities(
            @NotNull Vec3d vec3d,
            double radius,
            @Nullable Predicate<? super LivingEntity> predicate
    ) {
        return this.getNearbyEntitiesByType(LivingEntity.class, vec3d, radius, radius, radius, predicate);
    }

    /**
     * Gets living entities within a specified xz and y radius around a position, with a filter.
     *
     * @param vec3d     the center position
     * @param xzRadius  the x and z axis radius
     * @param yRadius   the y-axis radius
     * @param predicate a predicate to filter entities
     * @return a list of matching living entities
     */
    default @NotNull List<LivingEntity> getNearbyLivingEntities(
            @NotNull Vec3d vec3d,
            double xzRadius,
            double yRadius,
            @Nullable Predicate<? super LivingEntity> predicate
    ) {
        return this.getNearbyEntitiesByType(LivingEntity.class, vec3d, xzRadius, yRadius, xzRadius, predicate);
    }

    /**
     * Gets living entities within a specified x, y, and z radius around a position, with a filter.
     *
     * @param vec3d     the center position
     * @param xRadius   the x-axis radius
     * @param yRadius   the y-axis radius
     * @param zRadius   the z-axis radius
     * @param predicate a predicate to filter entities
     * @return a list of matching living entities
     */
    default @NotNull List<LivingEntity> getNearbyLivingEntities(
            @NotNull Vec3d vec3d,
            double xRadius,
            double yRadius,
            double zRadius,
            @Nullable Predicate<? super LivingEntity> predicate
    ) {
        return this.getNearbyEntitiesByType(LivingEntity.class, vec3d, xRadius, yRadius, zRadius, predicate);
    }

    /**
     * Gets players within a radius around a position.
     *
     * @param vec3d  the center position
     * @param radius the search radius
     * @return a list of matching players
     */
    default @NotNull List<Player> getNearbyPlayers(@NotNull Vec3d vec3d, final double radius) {
        return this.getNearbyEntitiesByType(Player.class, vec3d, radius, radius, radius);
    }

    /**
     * Gets players within a specified xz and y radius around a position.
     *
     * @param vec3d    the center position
     * @param xzRadius the x and z axis radius
     * @param yRadius  the y-axis radius
     * @return a list of matching players
     */
    default @NotNull List<Player> getNearbyPlayers(
            @NotNull Vec3d vec3d,
            double xzRadius,
            double yRadius
    ) {
        return this.getNearbyEntitiesByType(Player.class, vec3d, xzRadius, yRadius, xzRadius);
    }

    /**
     * Gets players within a specified x, y, and z radius around a position.
     *
     * @param vec3d   the center position
     * @param xRadius the x-axis radius
     * @param yRadius the y-axis radius
     * @param zRadius the z-axis radius
     * @return a list of matching players
     */
    default @NotNull List<Player> getNearbyPlayers(
            @NotNull Vec3d vec3d,
            double xRadius,
            double yRadius,
            double zRadius
    ) {
        return this.getNearbyEntitiesByType(Player.class, vec3d, xRadius, yRadius, zRadius);
    }

    /**
     * Gets players within a radius around a position, with a filter.
     *
     * @param vec3d     the center position
     * @param radius    the search radius
     * @param predicate a predicate to filter players
     * @return a list of matching players
     */
    default @NotNull List<Player> getNearbyPlayers(
            @NotNull Vec3d vec3d,
            double radius,
            @Nullable Predicate<? super Player> predicate
    ) {
        return this.getNearbyEntitiesByType(Player.class, vec3d, radius, radius, radius, predicate);
    }

    /**
     * Gets players within a specified xz and y radius around a position, with a filter.
     *
     * @param vec3d     the center position
     * @param xzRadius  the x and z axis radius
     * @param yRadius   the y-axis radius
     * @param predicate a predicate to filter players
     * @return a list of matching players
     */
    default @NotNull List<Player> getNearbyPlayers(
            @NotNull Vec3d vec3d,
            double xzRadius,
            double yRadius,
            @Nullable Predicate<? super Player> predicate
    ) {
        return this.getNearbyEntitiesByType(Player.class, vec3d, xzRadius, yRadius, xzRadius, predicate);
    }

    /**
     * Gets players within a specified x, y, and z radius around a position, with a filter.
     *
     * @param vec3d     the center position
     * @param xRadius   the x-axis radius
     * @param yRadius   the y-axis radius
     * @param zRadius   the z-axis radius
     * @param predicate a predicate to filter players
     * @return a list of matching players
     */
    default @NotNull List<Player> getNearbyPlayers(
            @NotNull Vec3d vec3d,
            double xRadius,
            double yRadius,
            double zRadius,
            @Nullable Predicate<? super Player> predicate
    ) {
        return this.getNearbyEntitiesByType(Player.class, vec3d, xRadius, yRadius, zRadius, predicate);
    }

    /**
     * Gets entities of a specific type within a radius around a position.
     *
     * @param <T>    the type of entities to search for
     * @param clazz  the class object of the entity type
     * @param vec3d  the center position
     * @param radius the search radius
     * @return a list of matching entities
     */
    default @NotNull <T extends Entity> List<T> getNearbyEntitiesByType(
            @Nullable Class<? extends T> clazz,
            @NotNull Vec3d vec3d,
            double radius
    ) {
        return this.getNearbyEntitiesByType(clazz, vec3d, radius, radius, radius, null);
    }

    /**
     * Gets entities of a specific type within a specified xz and y radius around a position.
     *
     * @param <T>      the type of entities to search for
     * @param clazz    the class object of the entity type
     * @param vec3d    the center position
     * @param xzRadius the x and z axis radius
     * @param yRadius  the y-axis radius
     * @return a list of matching entities
     */
    default @NotNull <T extends Entity> List<T> getNearbyEntitiesByType(
            @Nullable Class<? extends T> clazz,
            @NotNull Vec3d vec3d,
            double xzRadius,
            double yRadius
    ) {
        return this.getNearbyEntitiesByType(clazz, vec3d, xzRadius, yRadius, xzRadius, null);
    }

    /**
     * Gets entities of a specific type within a specified x, y, and z radius around a position.
     *
     * @param <T>     the type of entities to search for
     * @param clazz   the class object of the entity type
     * @param vec3d   the center position
     * @param xRadius the x-axis radius
     * @param yRadius the y-axis radius
     * @param zRadius the z-axis radius
     * @return a list of matching entities
     */
    default @NotNull <T extends Entity> List<T> getNearbyEntitiesByType(
            @Nullable Class<? extends T> clazz,
            @NotNull Vec3d vec3d,
            double xRadius,
            double yRadius,
            double zRadius
    ) {
        return this.getNearbyEntitiesByType(clazz, vec3d, xRadius, yRadius, zRadius, null);
    }

    /**
     * Gets entities of a specific type within a radius around a position, with a filter.
     *
     * @param <T>       the type of entities to search for
     * @param clazz     the class object of the entity type
     * @param vec3d     the center position
     * @param radius    the search radius
     * @param predicate a predicate to filter entities
     * @return a list of matching entities
     */
    default @NotNull <T extends Entity> List<T> getNearbyEntitiesByType(
            @Nullable Class<? extends T> clazz,
            @NotNull Vec3d vec3d,
            double radius,
            @Nullable Predicate<? super T> predicate
    ) {
        return this.getNearbyEntitiesByType(clazz, vec3d, radius, radius, radius, predicate);
    }

    /**
     * Gets entities of a specific type within a specified xz and y radius around a position, with a filter.
     *
     * @param <T>       the type of entities to search for
     * @param clazz     the class object of the entity type
     * @param vec3d     the center position
     * @param xzRadius  the x and z axis radius
     * @param yRadius   the y-axis radius
     * @param predicate a predicate to filter entities
     * @return a list of matching entities
     */
    default @NotNull <T extends Entity> List<T> getNearbyEntitiesByType(
            @Nullable Class<? extends T> clazz,
            @NotNull Vec3d vec3d,
            double xzRadius,
            double yRadius,
            @Nullable Predicate<? super T> predicate
    ) {
        return this.getNearbyEntitiesByType(clazz, vec3d, xzRadius, yRadius, xzRadius, predicate);
    }
}
