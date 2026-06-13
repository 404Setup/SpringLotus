/*
 * Copyright (C) 2026  404Setup.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package one.pkg.libsl.mixin.as;

import com.google.common.base.Predicates;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.visitors.CollectFields;
import net.minecraft.nbt.visitors.FieldSelector;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.phys.AABB;
import one.pkg.libsl.api.Vec3d;
import one.pkg.libsl.api.instance.AsChunkMap;
import one.pkg.libsl.api.instance.AsLevel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

@Mixin(Level.class)
public abstract class LevelMixin implements AsLevel, LevelAccessor {
    @Unique
    private static final Map<ResourceKey<Level>, String> WORLD_NAME_CACHE = new WeakHashMap<>();

    private LevelMixin() {
    }

    @Shadow
    public abstract @NonNull List<Entity> getEntities(@Nullable Entity except, @NonNull AABB bb,
                                                      @NonNull Predicate<? super Entity> selector);

    @Shadow
    public abstract LevelChunk getChunkAt(BlockPos pos);

    @Shadow
    public abstract ResourceKey<Level> dimension();

    @Override
    public String getName() {
        ResourceKey<Level> key = dimension();
        return WORLD_NAME_CACHE.computeIfAbsent(key, k -> k.identifier().toString());
    }

    @Override
    public List<Entity> getNearbyEntities(Vec3d vec3d, double x, double y, double z, Predicate<? super Entity> filter) {
        if (!((Level) (Object) this).equals(vec3d.getLevel()))
            throw new IllegalArgumentException("Cannot get nearby entities from different levels");
        AABB aabb = new AABB(
                vec3d.getX() - x, vec3d.getY() - y,
                vec3d.getZ() - z, vec3d.getX() + x,
                vec3d.getY() + y, vec3d.getZ() + z
        );
        return getNearbyEntities(aabb, filter);
    }

    @Override
    public boolean updateChunkForced(ChunkPos pos, boolean forced) {
        return this.getChunkSource().updateChunkForced(pos, forced);
    }

    @Override
    public boolean updateChunkForced(long pos, boolean forced) {
        return this.getChunkSource().updateChunkForced(ChunkPos.unpack(pos), forced);
    }

    @Override
    public CompletableFuture<Boolean> isChunkLoaded(long pos) {
        ChunkPos chunkPos = ChunkPos.unpack(pos);
        ServerChunkCache serverChunkCache = (ServerChunkCache) this.getChunkSource();
        AsChunkMap map = (AsChunkMap) serverChunkCache.chunkMap;
        ChunkHolder holder = map.getVisibleChunk(chunkPos);
        if (holder != null && holder.getLatestStatus() == ChunkStatus.FULL) {
            return CompletableFuture.completedFuture(true);
        }
        FieldSelector selector = new FieldSelector(StringTag.TYPE, "Status");
        CollectFields collector = new CollectFields(selector);
        return serverChunkCache.chunkScanner().scanChunk(chunkPos, collector)
                .thenApply(ignored -> {
                    if (collector.getResult() instanceof CompoundTag nbt) {
                        String status = nbt.getStringOr("Status", "");
                        return "minecraft:full".equals(status) || "full".equals(status);
                    }
                    return false;
                });
    }

    @Override
    public CompletableFuture<Boolean> isChunkLoaded(ChunkPos pos) {
        return isChunkLoaded(pos.pack());
    }

    @Override
    public CompletableFuture<Boolean> isChunkLoaded(int x, int z) {
        return isChunkLoaded(new ChunkPos(x, z));
    }

    @Override
    public List<Entity> getNearbyEntities(@NotNull AABB aabb, @Nullable Predicate<? super Entity> filter) {
        return getEntities((Entity) null, aabb, filter != null ? filter : Predicates.alwaysTrue());
    }
}
