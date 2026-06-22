/*
 * Copyright (C) 2026  404Setup.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package one.pkg.libsl.api;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;
import one.pkg.libsl.api.instance.AsLevel;
import one.pkg.libsl.api.loader.JavaLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.function.Predicate;

/**
 * A class representing a 3D vector with orientation (yaw, pitch) and an associated {@link Level}.
 */
@SuppressWarnings("all")
public class Vec3d {
    private double x, y, z;
    private float yaw, pitch;
    private Reference<Level> level;

    /**
     * Constructs a new Vec3d with coordinates and a level.
     *
     * @param x     The x-coordinate.
     * @param y     The y-coordinate.
     * @param z     The z-coordinate.
     * @param level The associated level.
     */
    public Vec3d(double x, double y, double z, Level level) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.level = new WeakReference<>(level);
    }

    /**
     * Constructs a new Vec3d with coordinates, orientation, and a level.
     *
     * @param x     The x-coordinate.
     * @param y     The y-coordinate.
     * @param z     The z-coordinate.
     * @param yaw   The yaw orientation.
     * @param pitch The pitch orientation.
     * @param level The associated level.
     */
    public Vec3d(double x, double y, double z, float yaw, float pitch, Level level) {
        this(x, y, z, level);
        this.yaw = yaw;
        this.pitch = pitch;
    }

    /**
     * Constructs a new Vec3d from a {@link Vec3}, a level, and orientation.
     *
     * @param vec   The source vector.
     * @param level The associated level.
     * @param yaw   The yaw orientation.
     * @param pitch The pitch orientation.
     */
    public Vec3d(Vec3 vec, Level level, float yaw, float pitch) {
        this(vec.x, vec.y, vec.z, yaw, pitch, level);
    }

    /**
     * Gets the x-coordinate.
     *
     * @return The x-coordinate.
     */
    public double getX() {
        return x;
    }

    /**
     * Sets the x-coordinate.
     *
     * @param x The new x-coordinate.
     * @return This Vec3d instance for chaining.
     */
    @NotNull
    public Vec3d setX(double x) {
        this.x = x;
        return this;
    }

    /**
     * Gets the y-coordinate.
     *
     * @return The y-coordinate.
     */
    public double getY() {
        return y;
    }

    /**
     * Sets the y-coordinate.
     *
     * @param y The new y-coordinate.
     * @return This Vec3d instance for chaining.
     */
    @NotNull
    public Vec3d setY(double y) {
        this.y = y;
        return this;
    }

    /**
     * Gets the z-coordinate.
     *
     * @return The z-coordinate.
     */
    public double getZ() {
        return z;
    }

    /**
     * Sets the z-coordinate.
     *
     * @param z The new z-coordinate.
     * @return This Vec3d instance for chaining.
     */
    @NotNull
    public Vec3d setZ(double z) {
        this.z = z;
        return this;
    }

    /**
     * Gets the yaw orientation.
     *
     * @return The yaw orientation.
     */
    public float getYaw() {
        return yaw;
    }

    /**
     * Sets the yaw orientation.
     *
     * @param yaw The new yaw orientation.
     * @return This Vec3d instance for chaining.
     */
    @NotNull
    public Vec3d setYaw(float yaw) {
        this.yaw = yaw;
        return this;
    }

    /**
     * Gets the pitch orientation.
     *
     * @return The pitch orientation.
     */
    public float getPitch() {
        return pitch;
    }

    /**
     * Sets the pitch orientation.
     *
     * @param pitch The new pitch orientation.
     * @return This Vec3d instance for chaining.
     */
    @NotNull
    public Vec3d setPitch(float pitch) {
        this.pitch = pitch;
        return this;
    }

    /**
     * Gets the associated level.
     *
     * @return The associated level, or {@code null} if not set.
     * @throws IllegalStateException If the level has been garbage collected.
     */
    @Nullable
    public Level getLevel() throws IllegalStateException {
        if (this.level == null) {
            return null;
        }

        Level level1 = this.level.get();
        if (level1 == null) {
            throw new IllegalStateException("Location has no world");
        }
        return level1;
    }

    /**
     * Sets the associated level.
     *
     * @param level The new associated level.
     */
    public void setLevel(@Nullable Level level) {
        this.level = (level == null) ? null : new WeakReference<>(level);
    }

    /**
     * Gets the associated level as {@link AsLevel}.
     *
     * @return The level as {@link AsLevel}, or {@code null} if not set.
     * @throws IllegalStateException If the level has been garbage collected.
     */
    @Nullable
    public AsLevel getAsLevel() throws IllegalStateException {
        return (AsLevel) getLevel();
    }

    /**
     * Checks if the level associated with this vector is currently loaded.
     *
     * @return {@code true} if the level is loaded, {@code false} otherwise.
     */
    public boolean isLevelLoaded() {
        if (this.level == null) {
            return false;
        }

        Level level1 = this.level.get();

        MinecraftServer server = JavaLoader.INSTANCE.server();
        return level1 != null && server != null && server.getLevel(level1.dimension()) != null;
    }

    /**
     * Gets the {@link ChunkPos} of the chunk containing this vector.
     *
     * @return The chunk position, or {@code null} if the level is not loaded.
     */
    @Nullable
    public ChunkPos getChunkPos() {
        if (isLevelLoaded()) {
            return new ChunkPos((int) Math.floor(x / 16.0), (int) Math.floor(z / 16.0));
        }
        return null;
    }

    /**
     * Gets the {@link LevelChunk} containing this vector.
     *
     * @return The level chunk, or {@code null} if the block position is invalid.
     */
    @Nullable
    public LevelChunk getLevelChunk() {
        BlockPos pos = getBlockPos();
        if (pos != null) {
            return getLevel().getChunkAt(pos);
        }
        return null;
    }

    /**
     * Gets the {@link ChunkAccess} containing this vector.
     *
     * @return The chunk access, or {@code null} if the level chunk is not found.
     */
    @Nullable
    public ChunkAccess getChunk() {
        return getLevelChunk();
    }

    /**
     * Gets the {@link BlockPos} corresponding to this vector's coordinates.
     *
     * @return The block position, or {@code null} if the level is not loaded.
     */
    @Nullable
    public BlockPos getBlockPos() {
        if (isLevelLoaded()) {
            return new BlockPos((int) Math.floor(x), (int) Math.floor(y), (int) Math.floor(z));
        }
        return null;
    }

    /**
     * Gets the {@link BlockState} at this vector's coordinates.
     *
     * @return The block state, or {@code null} if the block position is invalid.
     */
    @Nullable
    public BlockState getBlockState() {
        BlockPos pos = getBlockPos();
        if (pos != null) {
            return getLevel().getBlockState(pos);
        }

        return null;
    }

    /**
     * Gets a list of entities within a specified radius of this vector.
     *
     * @param x The x-radius.
     * @param y The y-radius.
     * @param z The z-radius.
     * @return A list of nearby entities.
     * @throws IllegalArgumentException If the vector has no associated level.
     */
    public @NotNull List<Entity> getNearbyEntities(final double x, final double y, final double z) {
        AsLevel level1 = this.getAsLevel();
        if (level1 == null) {
            throw new IllegalArgumentException("Location has no world");
        }
        return level1.getNearbyEntities(this, x, y, z);
    }

    /**
     * Gets a list of living entities within a specified radius of this vector.
     *
     * @param radius The radius in all directions.
     * @return A list of nearby living entities.
     */
    public @NotNull List<LivingEntity> getNearbyLivingEntities(final double radius) {
        return this.getNearbyEntitiesByType(LivingEntity.class, radius, radius, radius);
    }

    /**
     * Gets a list of living entities within specified radii of this vector.
     *
     * @param xzRadius The horizontal radius.
     * @param yRadius  The vertical radius.
     * @return A list of nearby living entities.
     */
    public @NotNull List<LivingEntity> getNearbyLivingEntities(
            double xzRadius,
            double yRadius
    ) {
        return this.getNearbyEntitiesByType(LivingEntity.class, xzRadius, yRadius, xzRadius);
    }

    /**
     * Gets a list of living entities within specified radii of this vector.
     *
     * @param xRadius The x-radius.
     * @param yRadius The y-radius.
     * @param zRadius The z-radius.
     * @return A list of nearby living entities.
     */
    public @NotNull List<LivingEntity> getNearbyLivingEntities(
            double xRadius,
            double yRadius,
            double zRadius
    ) {
        return this.getNearbyEntitiesByType(LivingEntity.class, xRadius, yRadius, zRadius);
    }

    /**
     * Gets a list of living entities within a specified radius and matching a predicate.
     *
     * @param radius    The radius in all directions.
     * @param predicate The filter predicate.
     * @return A list of matching nearby living entities.
     */
    public @NotNull List<LivingEntity> getNearbyLivingEntities(
            double radius,
            @Nullable Predicate<? super LivingEntity> predicate
    ) {
        return this.getNearbyEntitiesByType(LivingEntity.class, radius, radius, radius, predicate);
    }

    /**
     * Gets a list of living entities within specified radii and matching a predicate.
     *
     * @param xzRadius  The horizontal radius.
     * @param yRadius   The vertical radius.
     * @param predicate The filter predicate.
     * @return A list of matching nearby living entities.
     */
    public @NotNull List<LivingEntity> getNearbyLivingEntities(
            double xzRadius,
            double yRadius,
            @Nullable Predicate<? super LivingEntity> predicate
    ) {
        return this.getNearbyEntitiesByType(LivingEntity.class, xzRadius, yRadius, xzRadius, predicate);
    }

    /**
     * Gets a list of living entities within specified radii and matching a predicate.
     *
     * @param xRadius   The x-radius.
     * @param yRadius   The y-radius.
     * @param zRadius   The z-radius.
     * @param predicate The filter predicate.
     * @return A list of matching nearby living entities.
     */
    public @NotNull List<LivingEntity> getNearbyLivingEntities(
            double xRadius,
            double yRadius,
            double zRadius,
            @Nullable Predicate<? super LivingEntity> predicate
    ) {
        return this.getNearbyEntitiesByType(LivingEntity.class, xRadius, yRadius, zRadius, predicate);
    }

    /**
     * Gets a list of players within a specified radius.
     *
     * @param radius The radius in all directions.
     * @return A list of nearby players.
     */
    public @NotNull List<Player> getNearbyPlayers(double radius) {
        return this.getNearbyEntitiesByType(Player.class, radius, radius, radius);
    }

    /**
     * Gets a list of players within specified horizontal and vertical radii.
     *
     * @param xzRadius The horizontal radius.
     * @param yRadius  The vertical radius.
     * @return A list of nearby players.
     */
    public @NotNull List<Player> getNearbyPlayers(double xzRadius, double yRadius) {
        return this.getNearbyEntitiesByType(Player.class, xzRadius, yRadius, xzRadius);
    }

    /**
     * Gets a list of players within specified radii.
     *
     * @param xRadius The x-radius.
     * @param yRadius The y-radius.
     * @param zRadius The z-radius.
     * @return A list of nearby players.
     */
    public @NotNull List<Player> getNearbyPlayers(
            double xRadius,
            double yRadius,
            double zRadius
    ) {
        return this.getNearbyEntitiesByType(Player.class, xRadius, yRadius, zRadius);
    }

    /**
     * Gets a list of players within a radius and matching a predicate.
     *
     * @param radius    The radius in all directions.
     * @param predicate The filter predicate.
     * @return A list of matching nearby players.
     */
    public @NotNull List<Player> getNearbyPlayers(
            double radius,
            @Nullable Predicate<? super Player> predicate
    ) {
        return this.getNearbyEntitiesByType(Player.class, radius, radius, radius, predicate);
    }

    /**
     * Gets a list of players within horizontal and vertical radii and matching a predicate.
     *
     * @param xzRadius  The horizontal radius.
     * @param yRadius   The vertical radius.
     * @param predicate The filter predicate.
     * @return A list of matching nearby players.
     */
    public @NotNull List<Player> getNearbyPlayers(
            double xzRadius,
            double yRadius,
            @Nullable Predicate<? super Player> predicate
    ) {
        return this.getNearbyEntitiesByType(Player.class, xzRadius, yRadius, xzRadius, predicate);
    }

    /**
     * Gets a list of players within specified radii and matching a predicate.
     *
     * @param xRadius   The x-radius.
     * @param yRadius   The y-radius.
     * @param zRadius   The z-radius.
     * @param predicate The filter predicate.
     * @return A list of matching nearby players.
     */
    public @NotNull List<Player> getNearbyPlayers(
            double xRadius,
            double yRadius,
            double zRadius,
            @Nullable Predicate<? super Player> predicate
    ) {
        return this.getNearbyEntitiesByType(Player.class, xRadius, yRadius, zRadius, predicate);
    }

    /**
     * Gets entities of a specific type within a radius.
     *
     * @param clazz  The entity class.
     * @param radius The radius in all directions.
     * @param <T>    The entity type.
     * @return A list of nearby entities of the specified type.
     */
    public @NotNull <T extends Entity> List<T> getNearbyEntitiesByType(
            @Nullable Class<? extends T> clazz,
            double radius
    ) {
        return this.getNearbyEntitiesByType(clazz, radius, radius, radius, null);
    }

    /**
     * Gets entities of a specific type within specified horizontal and vertical radii.
     *
     * @param clazz    The entity class.
     * @param xzRadius The horizontal radius.
     * @param yRadius  The vertical radius.
     * @param <T>      The entity type.
     * @return A list of nearby entities of the specified type.
     */
    public @NotNull <T extends Entity> List<T> getNearbyEntitiesByType(
            @Nullable Class<? extends T> clazz,
            double xzRadius,
            double yRadius
    ) {
        return this.getNearbyEntitiesByType(clazz, xzRadius, yRadius, xzRadius, null);
    }

    /**
     * Gets entities of a specific type within specified radii.
     *
     * @param clazz   The entity class.
     * @param xRadius The x-radius.
     * @param yRadius The y-radius.
     * @param zRadius The z-radius.
     * @param <T>     The entity type.
     * @return A list of nearby entities of the specified type.
     */
    public @NotNull <T extends Entity> List<T> getNearbyEntitiesByType(
            @Nullable Class<? extends T> clazz,
            double xRadius,
            double yRadius,
            double zRadius
    ) {
        return this.getNearbyEntitiesByType(clazz, xRadius, yRadius, zRadius, null);
    }

    /**
     * Gets entities of a specific type within a radius and matching a predicate.
     *
     * @param clazz     The entity class.
     * @param radius    The radius in all directions.
     * @param predicate The filter predicate.
     * @param <T>       The entity type.
     * @return A list of matching nearby entities of the specified type.
     */
    public @NotNull <T extends Entity> List<T> getNearbyEntitiesByType(
            @Nullable Class<? extends T> clazz,
            double radius,
            @Nullable Predicate<? super T> predicate
    ) {
        return this.getNearbyEntitiesByType(clazz, radius, radius, radius, predicate);
    }

    /**
     * Gets entities of a specific type within horizontal and vertical radii and matching a predicate.
     *
     * @param clazz     The entity class.
     * @param xzRadius  The horizontal radius.
     * @param yRadius   The vertical radius.
     * @param predicate The filter predicate.
     * @param <T>       The entity type.
     * @return A list of matching nearby entities of the specified type.
     */
    public @NotNull <T extends Entity> List<T> getNearbyEntitiesByType(
            @Nullable Class<? extends T> clazz,
            double xzRadius,
            double yRadius,
            @Nullable Predicate<? super T> predicate
    ) {
        return this.getNearbyEntitiesByType(clazz, xzRadius, yRadius, xzRadius, predicate);
    }

    /**
     * Gets entities of a specific type within specified radii and matching a predicate.
     *
     * @param clazz     The entity class.
     * @param xRadius   The x-radius.
     * @param yRadius   The y-radius.
     * @param zRadius   The z-radius.
     * @param predicate The filter predicate.
     * @param <T>       The entity type.
     * @return A list of matching nearby entities of the specified type.
     * @throws IllegalArgumentException If the vector has no associated level.
     */
    public @NotNull <T extends Entity> List<T> getNearbyEntitiesByType(
            @Nullable Class<? extends T> clazz,
            double xRadius,
            double yRadius,
            double zRadius,
            @Nullable Predicate<? super T> predicate
    ) {
        final AsLevel level = this.getAsLevel();
        if (level == null) {
            throw new IllegalArgumentException("Location has no level");
        }
        return level.getNearbyEntitiesByType(clazz, this, xRadius, yRadius, zRadius, predicate);
    }

    /**
     * Adds another {@link Vec3d} to this vector.
     *
     * @param vec The vector to add.
     * @return This Vec3d instance for chaining.
     * @throws IllegalArgumentException If the levels of the vectors differ.
     */
    @NotNull
    public Vec3d add(@NotNull Vec3d vec) {
        if (vec.getLevel() != getLevel()) {
            throw new IllegalArgumentException("Cannot add Vec3ds of differing worlds");
        }

        x += vec.x;
        y += vec.y;
        z += vec.z;
        return this;
    }

    /**
     * Adds specified coordinates to this vector.
     *
     * @param x The x-offset.
     * @param y The y-offset.
     * @param z The z-offset.
     * @return This Vec3d instance for chaining.
     */
    @NotNull
    public Vec3d add(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;

        return this;
    }

    /**
     * Adds a {@link Vec3} to this vector.
     *
     * @param vec The vector to add.
     * @return This Vec3d instance for chaining.
     */
    @NotNull
    public Vec3d add(@NotNull Vec3 vec) {
        x += vec.x;
        y += vec.y;
        z += vec.z;

        return this;
    }

    /**
     * Adds rotation to this vector.
     *
     * @param yaw   The yaw offset.
     * @param pitch The pitch offset.
     * @return This Vec3d instance for chaining.
     */
    @NotNull
    public Vec3d addRotation(float yaw, float pitch) {
        this.yaw += yaw;
        this.pitch += pitch;
        return this;
    }

    /**
     * Sets the coordinates of this vector.
     *
     * @param x The new x-coordinate.
     * @param y The new y-coordinate.
     * @param z The new z-coordinate.
     * @return This Vec3d instance for chaining.
     */
    @NotNull
    public Vec3d set(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    /**
     * Sets the coordinates of this vector from a {@link Vec3}.
     *
     * @param vec The source vector.
     * @return This Vec3d instance for chaining.
     */
    @NotNull
    public Vec3d set(@NotNull Vec3 vec) {
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
        return this;
    }

    /**
     * Sets the coordinates of this vector from another {@link Vec3d}.
     *
     * @param vec The source vector.
     * @return This Vec3d instance for chaining.
     */
    @NotNull
    public Vec3d set(@NotNull Vec3d vec) {
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;

        return this;
    }

    /**
     * Sets the yaw and pitch orientation of this vector.
     *
     * @param yaw   The new yaw.
     * @param pitch The new pitch.
     * @return This Vec3d instance for chaining.
     */
    @NotNull
    public Vec3d setYawPitch(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
        return this;
    }

    /**
     * Subtracts another {@link Vec3d} from this vector.
     *
     * @param vec The vector to subtract.
     * @return This Vec3d instance for chaining.
     * @throws IllegalArgumentException If the levels of the vectors differ.
     */
    @NotNull
    public Vec3d subtract(@NotNull Vec3d vec) {
        if (vec.getLevel() != getLevel()) {
            throw new IllegalArgumentException("Cannot subtract Vec3ds of differing worlds");
        }

        x -= vec.x;
        y -= vec.y;
        z -= vec.z;
        return this;
    }

    /**
     * Subtracts specified coordinates from this vector.
     *
     * @param x The x-offset.
     * @param y The y-offset.
     * @param z The z-offset.
     * @return This Vec3d instance for chaining.
     */
    @NotNull
    public Vec3d subtract(double x, double y, double z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        return this;
    }

    /**
     * Subtracts a {@link Vec3} from this vector.
     *
     * @param vec The vector to subtract.
     * @return This Vec3d instance for chaining.
     */
    @NotNull
    public Vec3d subtract(Vec3 vec) {
        x -= vec.x;
        y -= vec.y;
        z -= vec.z;
        return this;
    }

    /**
     * Multiplies this vector's coordinates by a scalar.
     *
     * @param scalar The scalar to multiply by.
     * @return This Vec3d instance for chaining.
     */
    @NotNull
    public Vec3d multiply(double scalar) {
        x *= scalar;
        y *= scalar;
        z *= scalar;
        return this;
    }

    /**
     * Multiplies this vector's coordinates by another {@link Vec3}'s coordinates.
     *
     * @param vec The vector to multiply by.
     * @return This Vec3d instance for chaining.
     */
    @NotNull
    public Vec3d multiply(@NotNull Vec3 vec) {
        x *= vec.x;
        y *= vec.y;
        z *= vec.z;

        return this;
    }

    /**
     * Multiplies this vector's orientation by specified factors.
     *
     * @param yaw   The yaw factor.
     * @param pitch The pitch factor.
     * @return This Vec3d instance for chaining.
     */
    @NotNull
    public Vec3d multiply(float yaw, float pitch) {
        this.yaw *= yaw;
        this.pitch *= pitch;

        return this;
    }

    /**
     * Multiplies this vector's coordinates by another {@link Vec3d}'s coordinates.
     *
     * @param vec The vector to multiply by.
     * @return This Vec3d instance for chaining.
     */
    @NotNull
    public Vec3d multiply(@NotNull Vec3d vec) {
        x *= vec.x;
        y *= vec.y;
        z *= vec.z;

        return this;
    }

    /**
     * Rotates this vector and adds orientation.
     *
     * @param yaw   The yaw to add.
     * @param pitch The pitch to add.
     * @return This Vec3d instance for chaining.
     */
    @NotNull
    public Vec3d rotate(float yaw, float pitch) {
        return multiply(yaw, pitch).addRotation(yaw, pitch);
    }

    /**
     * Rotates this vector and adds orientation from another {@link Vec3d}.
     *
     * @param vec The source vector for rotation.
     * @return This Vec3d instance for chaining.
     */
    @NotNull
    public Vec3d rotate(@NotNull Vec3d vec) {
        return multiply(vec).addRotation(vec.yaw, vec.pitch);
    }

    /**
     * Calculates the length of this vector.
     *
     * @return The length of the vector.
     */
    public double length() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    /**
     * Calculates the squared length of this vector.
     *
     * @return The squared length of the vector.
     */
    public double lengthSquared() {
        return x * x + y * y + z * z;
    }

    /**
     * Calculates the distance to another {@link Vec3d}.
     *
     * @param vec The target vector.
     * @return The distance to the target vector.
     */
    public double distanceTo(@NotNull Vec3d vec) {
        return Math.sqrt(distanceToSquared(vec));
    }

    /**
     * Calculates the squared distance to another {@link Vec3d}.
     *
     * @param vec The target vector.
     * @return The squared distance to the target vector, or -1 if levels differ.
     */
    public double distanceToSquared(@NotNull Vec3d vec) {
        if (vec.getLevel() != getLevel()) {
            return -1;
        }

        double dx = x - vec.x;
        double dy = y - vec.y;
        double dz = z - vec.z;

        return dx * dx + dy * dy + dz * dz;
    }

    /**
     * Normalizes this vector.
     *
     * @return This normalized Vec3d instance.
     */
    @NotNull
    public Vec3d normalize() {
        double length = length();
        if (length == 0) {
            return this;
        }
        return multiply(1 / length);
    }

    /**
     * Converts this vector to a {@link Vec3}.
     *
     * @return A new Vec3 instance with the same coordinates.
     */
    @NotNull
    public Vec3 toVec3() {
        return new Vec3(x, y, z);
    }

    @Override
    public boolean equals(@NotNull Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final Vec3d obj1 = (Vec3d) obj;

        Level level1 = (this.level == null) ? null : this.level.get();
        Level level2 = (obj1.level == null) ? null : obj1.level.get();
        if (level1 != level2 && (level1 == null || !level1.equals(level2))) {
            return false;
        }
        if (Double.doubleToLongBits(this.x) != Double.doubleToLongBits(obj1.x)) {
            return false;
        }
        if (Double.doubleToLongBits(this.y) != Double.doubleToLongBits(obj1.y)) {
            return false;
        }
        if (Double.doubleToLongBits(this.z) != Double.doubleToLongBits(obj1.z)) {
            return false;
        }
        if (Float.floatToIntBits(this.pitch) != Float.floatToIntBits(obj1.pitch)) {
            return false;
        }
        if (Float.floatToIntBits(this.yaw) != Float.floatToIntBits(obj1.yaw)) {
            return false;
        }
        return true;
    }

    @Override
    @Nullable
    public Vec3d clone() {
        try {
            return (Vec3d) super.clone();
        } catch (CloneNotSupportedException ignored) {
            return null;
        }
    }
}
