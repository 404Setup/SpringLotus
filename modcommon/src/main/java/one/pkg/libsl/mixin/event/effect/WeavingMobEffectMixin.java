/*
 * Copyright (C) 2026  404Setup.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package one.pkg.libsl.mixin.event.effect;

import com.google.common.collect.Sets;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.WeavingMobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import one.pkg.libsl.api.event.block.BlockBreakEvents;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Set;
import java.util.function.ToIntFunction;

@Mixin(WeavingMobEffect.class)
public class WeavingMobEffectMixin {
    @Shadow
    @Final
    private ToIntFunction<RandomSource> maxCobwebs;

    @Redirect(
            method = "onMobRemoved",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/effect/WeavingMobEffect;spawnCobwebsRandomlyAround(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/util/RandomSource;Lnet/minecraft/core/BlockPos;)V"
            )
    )
    private void springlotus$redirectSCRA(WeavingMobEffect instance, ServerLevel level, RandomSource random,
                                          BlockPos pos, @Local(argsOnly = true) LivingEntity mob) {
        springlotus$spawnCobwebsRandomlyAround(level, random, pos, mob);
    }

    @Unique
    private void springlotus$spawnCobwebsRandomlyAround(
            ServerLevel level, RandomSource random, BlockPos pos, LivingEntity entity) {
        Set<BlockPos> positionsToTransform = Sets.newHashSet();
        int cobwebCount = this.maxCobwebs.applyAsInt(random);

        for (BlockPos blockPos : BlockPos.randomInCube(random, 15, pos, 1)) {
            BlockPos below = blockPos.below();
            if (!positionsToTransform.contains(blockPos)
                    && level.getBlockState(blockPos).canBeReplaced()
                    && level.getBlockState(below).isFaceSturdy(level, below, Direction.UP)) {
                positionsToTransform.add(blockPos.immutable());
                if (positionsToTransform.size() >= cobwebCount) {
                    break;
                }
            }
        }

        if (BlockBreakEvents.ENTITY_UPDATE.canSkip()) return;
        for (BlockPos blockPos : positionsToTransform) {
            if (BlockBreakEvents.ENTITY_UPDATE.invoker().onEntityUpdate(entity, level, pos,
                    Blocks.COBWEB.defaultBlockState())) {
                level.setBlock(blockPos, Blocks.COBWEB.defaultBlockState(), 3);
                level.levelEvent(3018, blockPos, 0);
            }
        }
    }
}
