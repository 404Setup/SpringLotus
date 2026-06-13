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
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BambooStalkBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BambooLeaves;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import one.pkg.libsl.api.event.block.BlockSpreadEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BambooStalkBlock.class)
public class BambooStalkBlockMixin {
    @Shadow
    @Final
    public static IntegerProperty AGE;

    @Shadow
    @Final
    public static EnumProperty<BambooLeaves> LEAVES;

    @Shadow
    @Final
    public static IntegerProperty STAGE;

    @Inject(
            method = "growBamboo",
            at = @At(
                    value = "INVOKE",
                    shift = At.Shift.BEFORE,
                    target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z",
                    ordinal = 0
            ),
            cancellable = true
    )
    private void springLotus$growBamboo(
            BlockState state,
            Level level,
            BlockPos pos,
            RandomSource random,
            int height,
            CallbackInfo ci,
            @Local(name = "twoBelowState") BlockState twoBelowState
    ) {
        if (!BlockSpreadEvent.EVENT.canSkip()) {
            int age = state.getValue(AGE) != 1 && !twoBelowState.is(Blocks.BAMBOO) ? 0 : 1;
            int stage = (height < 11 || !(random.nextFloat() < 0.25F)) && height != 15 ? 0 : 1;

            BlockState newState = ((BambooStalkBlock) (Object) this).defaultBlockState()
                    .setValue(AGE, age).setValue(LEAVES, BambooLeaves.NONE).setValue(STAGE, stage);

            if (!BlockSpreadEvent.EVENT.invoker().onBlockSpread(level, newState, pos, pos.above()))
                ci.cancel();
        }
    }

    @Inject(
            method = "growBamboo",
            at = @At(
                    value = "INVOKE",
                    shift = At.Shift.BEFORE,
                    target = "Lnet/minecraft/world/level/block/state/BlockState;getValue(Lnet/minecraft/world/level/block/state/properties/Property;)Ljava/lang/Comparable;",
                    ordinal = 2
            ),
            cancellable = true
    )
    private void springLotus$growBamboo(
            CallbackInfo ci) {
        ci.cancel();
    }
}
