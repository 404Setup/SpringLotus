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
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SpeleothemBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.SpeleothemThickness;
import net.minecraft.world.level.material.Fluids;
import one.pkg.libsl.api.event.block.BlockSpreadEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SpeleothemBlock.class)
public class SpeleothemBlockMixin {
    @Shadow @Final
    public static EnumProperty<Direction> TIP_DIRECTION;
    @Shadow @Final
    public static EnumProperty<SpeleothemThickness> THICKNESS;
    @Shadow @Final
    public static BooleanProperty WATERLOGGED;

    @Redirect(
            method = "grow",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/SpeleothemBlock;createSpeleothem(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;Lnet/minecraft/world/level/block/state/properties/SpeleothemThickness;)V"
            )
    )
    private static void springLotus$grow(SpeleothemBlock instance, LevelAccessor level, BlockPos pos,
            Direction direction, SpeleothemThickness thickness, @Local(argsOnly = true) BlockPos growFromPos
    ) {
        BlockState state = Blocks.POINTED_DRIPSTONE.
                defaultBlockState().setValue(TIP_DIRECTION, direction)
                .setValue(THICKNESS, thickness)
                .setValue(WATERLOGGED, level.getFluidState(pos).is(Fluids.WATER));

        if (BlockSpreadEvent.EVENT.canSkip() || BlockSpreadEvent.EVENT.invoker().onBlockSpread(level, state, growFromPos, pos))
            level.setBlock(pos, state, 3);
    }
}
