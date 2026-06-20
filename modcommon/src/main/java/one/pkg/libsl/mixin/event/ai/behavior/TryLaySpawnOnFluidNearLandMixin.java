/*
 * Copyright (C) 2026  404Setup.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package one.pkg.libsl.mixin.event.ai.behavior;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.kinds.IdF.Mu;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.TryLaySpawnOnFluidNearLand;
import net.minecraft.world.entity.ai.behavior.declarative.MemoryAccessor;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import one.pkg.libsl.api.event.block.BlockBreakEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TryLaySpawnOnFluidNearLand.class)
public class TryLaySpawnOnFluidNearLandMixin {
    @Inject(
            method = "lambda$create$2",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z",
                    shift = At.Shift.BEFORE
            ),
            cancellable = true
    )
    private static void springlotus$injectCreate(Block spawnBlock, MemoryAccessor<Mu, WalkTarget> pregnant,
                                                 ServerLevel level, LivingEntity body, long timestamp,
                                                 CallbackInfoReturnable<Boolean> cir,
                                                 @Local BlockState newState,
                                                 @Local(name = "spawnPos") BlockPos spawnPos) {
        if (!BlockBreakEvents.ENTITY_UPDATE.invoker().onEntityUpdate(body, level, spawnPos, newState)) {
            cir.setReturnValue(true);
        }
    }
}
