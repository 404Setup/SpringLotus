/*
 * Copyright (C) 2026  404Setup.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package one.pkg.libsl.mixin.event.block.spread;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import one.pkg.libsl.api.event.block.BlockSpreadEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SculkVeinBlock.class)
public abstract class SculkVeinBlockMixin extends MultifaceBlock implements SculkBehaviour {
    @Shadow
    @Final
    private MultifaceSpreader veinSpreader;

    public SculkVeinBlockMixin(Properties properties) {
        super(properties);
    }

    @Redirect(
            method = "attemptUseCharge",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/SculkVeinBlock;attemptPlaceSculk(Lnet/minecraft/world/level/block/SculkSpreader;Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/util/RandomSource;)Z"
            )
    )
    private boolean springLotus$attemptUseCharge(
            SculkVeinBlock instance,
            SculkSpreader spreader,
            LevelAccessor level,
            BlockPos pos,
            RandomSource random,
            @Local(argsOnly = true) BlockPos originPos
    ) {
        return springLotus$attemptPlaceSculk(spreader, level, pos, originPos, random);
    }

    @Unique
    private boolean springLotus$attemptPlaceSculk(SculkSpreader spreader, LevelAccessor level, BlockPos newPos, BlockPos sourcePos, RandomSource random) {
        BlockState state = level.getBlockState(newPos);
        TagKey<Block> replaceTag = spreader.replaceableBlocks();

        for (Direction support : Direction.allShuffled(random)) {
            if (MultifaceBlock.hasFace(state, support)) {
                BlockPos supportPos = newPos.relative(support);
                BlockState supportState = level.getBlockState(supportPos);
                if (supportState.is(replaceTag)) {
                    BlockState defaultSculk = Blocks.SCULK.defaultBlockState();
                    if (!BlockSpreadEvent.EVENT.canSkip() ||
                            !BlockSpreadEvent.EVENT.invoker().onBlockSpread(level, defaultSculk, sourcePos, newPos)
                    ) {
                        return false;
                    }
                    level.setBlock(supportPos, defaultSculk, 3);
                    Block.pushEntitiesUp(supportState, defaultSculk, level, supportPos);
                    level.playSound((net.minecraft.world.entity.player.Player) null, supportPos, SoundEvents.SCULK_BLOCK_SPREAD, SoundSource.BLOCKS, 1.0F, 1.0F);
                    this.veinSpreader.spreadAll(defaultSculk, level, supportPos, spreader.isWorldGeneration());
                    Direction skip = support.getOpposite();

                    for (Direction veinBlocks : Direction.values()) {
                        if (veinBlocks != skip) {
                            BlockPos veinPos = supportPos.relative(veinBlocks);
                            BlockState possibleVeinBlock = level.getBlockState(veinPos);
                            if (possibleVeinBlock.is((Block)(Object)this)) {
                                this.onDischarged(level, possibleVeinBlock, veinPos, random);
                            }
                        }
                    }

                    return true;
                }
            }
        }

        return false;
    }
}
