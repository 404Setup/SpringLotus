/*
 * Copyright (C) 2026  404Setup.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package one.pkg.libsl.mixin.event.goal;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.EatBlockGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import one.pkg.libsl.api.event.block.BlockBreakEvents;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EatBlockGoal.class)
public abstract class EatBlockGoalMixin extends Goal {
    @Shadow
    @Final
    private Level level;

    @Shadow
    @Final
    private Mob mob;

    @Inject(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;destroyBlock(Lnet/minecraft/core/BlockPos;Z)Z",
                    shift = At.Shift.AFTER
            ),
            cancellable = true
    )
    private void springlotus$tickInvokeDestroyBlock(CallbackInfo ci, @Local BlockPos blockpos) {
        if (BlockBreakEvents.ENTITY_UPDATE.canSkip()) return;

        // TODO: Needs improvement
        BlockState state = this.level.getBlockState(blockpos);

        if (!BlockBreakEvents.ENTITY_UPDATE.invoker().onEntityUpdate(this.mob, this.level, blockpos, state)) {
            ci.cancel();
            this.mob.ate();
        }
    }

    @Inject(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;levelEvent(ILnet/minecraft/core/BlockPos;I)V",
                    shift = At.Shift.AFTER
            ),
            cancellable = true
    )
    private void springlotus$invokeLevelEvent(CallbackInfo ci, @Local(name = "blockpos1") BlockPos blockpos1) {
        if (BlockBreakEvents.ENTITY_UPDATE.canSkip()) return;

        // TODO: Needs improvement
        BlockState state = this.level.getBlockState(blockpos1);

        if (!BlockBreakEvents.ENTITY_UPDATE.invoker().onEntityUpdate(this.mob, this.level, blockpos1, state)) {
            ci.cancel();
            this.mob.ate();
        }
    }
}
