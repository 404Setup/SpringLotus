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

import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.world.level.ChunkPos;
import one.pkg.libsl.api.instance.AsChunkMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ChunkMap.class)
public class ChunkMapMixin implements AsChunkMap {

    @Shadow
    protected ChunkHolder getVisibleChunkIfPresent(long key) {
        return null;
    }

    @Override
    public ChunkHolder getVisibleChunk(long key) {
        return getVisibleChunkIfPresent(key);
    }

    @Override
    public ChunkHolder getVisibleChunk(ChunkPos pos) {
        return getVisibleChunkIfPresent(pos.toLong());
    }
}
