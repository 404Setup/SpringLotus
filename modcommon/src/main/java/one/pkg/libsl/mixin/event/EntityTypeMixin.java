/*
 * Copyright (C) 2026  404Setup.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package one.pkg.libsl.mixin.event;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import one.pkg.libsl.api.event.entity.ServerLivingEntityEvents;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

@Mixin(EntityType.class)
public abstract class EntityTypeMixin<T> {
    @Inject(
            method = "spawn(Lnet/minecraft/server/level/ServerLevel;Ljava/util/function/Consumer;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/EntitySpawnReason;ZZ)Lnet/minecraft/world/entity/Entity;",
            at = @At("RETURN"),
            cancellable = true
    )
    private void onSpawn(ServerLevel level, @Nullable Consumer<T> postSpawnConfig, BlockPos spawnPos,
                         EntitySpawnReason spawnReason, boolean tryMoveDown, boolean movedUp,
                         CallbackInfoReturnable<T> cir) {
        var r = cir.getReturnValue();
        if (r instanceof Player || !(r instanceof LivingEntity entity) ||
                ServerLivingEntityEvents.PRE_SPAWN.canSkip()) return;

        if (!ServerLivingEntityEvents.PRE_SPAWN.invoker().onPreSpawn(entity, level, spawnPos, spawnReason)) {
            cir.setReturnValue(null);
        }
    }
}
