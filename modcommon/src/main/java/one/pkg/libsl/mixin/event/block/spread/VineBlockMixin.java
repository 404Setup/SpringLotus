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
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import one.pkg.libsl.api.event.block.BlockSpreadEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(VineBlock.class)
public class VineBlockMixin {
    @Redirect(
            method = "randomTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z",
                    ordinal = 0
            )
    )
    private boolean springLotus$randomTick$0(
            ServerLevel instance,
            BlockPos blockPos,
            BlockState state,
            int i,
            @Local(argsOnly = true) BlockPos pos
    ) {
        return springLotus$tryPlaceBlock(instance, pos, blockPos, state, i);
    }

    @Redirect(
            method = "randomTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z",
                    ordinal = 1
            )
    )
    private boolean springLotus$randomTick$1(
            ServerLevel instance,
            BlockPos blockPos,
            BlockState state,
            int i,
            @Local(argsOnly = true) BlockPos pos
    ) {
        return springLotus$tryPlaceBlock(instance, pos, blockPos, state, i);
    }

    @Redirect(
            method = "randomTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z",
                    ordinal = 2
            )
    )
    private boolean springLotus$randomTick$2(
            ServerLevel instance,
            BlockPos blockPos,
            BlockState state,
            int i,
            @Local(argsOnly = true) BlockPos pos
    ) {
        return springLotus$tryPlaceBlock(instance, pos, blockPos, state, i);
    }

    @Redirect(
            method = "randomTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z",
                    ordinal = 3
            )
    )
    private boolean springLotus$randomTick$3(
            ServerLevel instance,
            BlockPos blockPos,
            BlockState state,
            int i,
            @Local(argsOnly = true) BlockPos pos
    ) {
        return springLotus$tryPlaceBlock(instance, pos, blockPos, state, i);
    }

    @Redirect(
            method = "randomTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z",
                    ordinal = 4
            )
    )
    private boolean springLotus$randomTick$4(
            ServerLevel instance,
            BlockPos blockPos,
            BlockState state,
            int i,
            @Local(argsOnly = true) BlockPos pos
    ) {
        return springLotus$tryPlaceBlock(instance, pos, blockPos, state, i);
    }

    @Redirect(
            method = "randomTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z",
                    ordinal = 7
            )
    )
    private boolean springLotus$randomTick$7(
            ServerLevel instance,
            BlockPos blockPos,
            BlockState state,
            int i,
            @Local(argsOnly = true) BlockPos pos
    ) {
        return springLotus$tryPlaceBlock(instance, pos, blockPos, state, i);
    }

    @Redirect(
            method = "randomTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z",
                    ordinal = 8
            )
    )
    private boolean springLotus$randomTick$8(
            ServerLevel instance,
            BlockPos blockPos,
            BlockState state,
            int i,
            @Local(argsOnly = true) BlockPos pos
    ) {
        return springLotus$tryPlaceBlock(instance, pos, blockPos, state, i);
    }

    @Unique
    private boolean springLotus$tryPlaceBlock(
            ServerLevel instance,
            BlockPos source,
            BlockPos target,
            BlockState state,
            int i
    ) {
        if (!BlockSpreadEvent.EVENT.invoker().onBlockSpread(instance, state, source, target))
            return false;

        return instance.setBlock(target, state, i);
    }
}
