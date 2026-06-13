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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.ChorusFlowerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import one.pkg.libsl.api.event.block.BlockSpreadEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChorusFlowerBlock.class)
public class ChorusFlowerBlockMixin {
    @Shadow
    @Final
    public static IntegerProperty AGE;

    @Inject(
            method = "randomTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z",
                    ordinal = 0,
                    shift = At.Shift.BEFORE
            ),
            cancellable = true
    )
    private void springLotus$randomTick(
            BlockState state,
            ServerLevel level,
            BlockPos pos,
            RandomSource random,
            CallbackInfo ci,
            @Local(name = "currentAge") int currentAge,
            @Local(name = "above") BlockPos above
    ) {
        if (!BlockSpreadEvent.EVENT.canSkip()) {
            BlockState newState = ((ChorusFlowerBlock) (Object) this)
                    .defaultBlockState().setValue(AGE, currentAge);
            if (!BlockSpreadEvent.EVENT.invoker().onBlockSpread(level, newState, pos, above))
                ci.cancel();
        }
    }

    @Inject(
            method = "randomTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/ChorusFlowerBlock;placeGrownFlower(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;I)V",
                    ordinal = 1,
                    shift = At.Shift.BEFORE
            ),
            cancellable = true
    )
    private void springLotus$randomTick$placeGrownFlower(
            BlockState state,
            ServerLevel level,
            BlockPos pos,
            RandomSource random,
            CallbackInfo ci,
            @Local(name = "currentAge") int currentAge,
            @Local(name = "target") BlockPos target
    ) {
        if (!BlockSpreadEvent.EVENT.canSkip()) {
            BlockState newState = ((ChorusFlowerBlock) (Object) this)
                    .defaultBlockState().setValue(AGE, currentAge + 1);

            if (!BlockSpreadEvent.EVENT.invoker().onBlockSpread(level, newState, pos, target))
                ci.cancel();
        }
    }
}
