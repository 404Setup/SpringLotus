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
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.behavior.HarvestFarmland;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import one.pkg.libsl.api.event.block.BlockBreakEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(HarvestFarmland.class)
public class HarvestFarmlandMixin {
    @Shadow
    private BlockPos aboveFarmlandPos;

    @Redirect(
            method = "tick(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/npc/Villager;J)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;destroyBlock(Lnet/minecraft/core/BlockPos;ZLnet/minecraft/world/entity/Entity;)Z"
            )
    )
    private boolean springlotus$redirectDestroyBlock(ServerLevel instance, BlockPos pos, boolean b, Entity entity,
                                                     @Local BlockState blockState) {
        if (BlockBreakEvents.ENTITY_UPDATE.invoker().onEntityUpdate(entity, instance, pos, blockState.getFluidState().createLegacyBlock())) {
            return instance.destroyBlock(pos, b, entity);
        }
        return false;
    }

    @Redirect(
            method = "tick(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/npc/Villager;J)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z")
    )
    private boolean springlotus$redirectSetBlockAndUpdate(ServerLevel instance, BlockPos pos, BlockState state,
                                                          @Local(argsOnly = true) Villager body,
                                                          @Share("springlotus$placed") LocalBooleanRef placed) {
        if (BlockBreakEvents.ENTITY_UPDATE.invoker().onEntityUpdate(body, instance, pos, state)) {
            boolean result = instance.setBlockAndUpdate(pos, state);
            if (result) {
                instance.gameEvent(GameEvent.BLOCK_PLACE, this.aboveFarmlandPos, GameEvent.Context.of(body, state));
                placed.set(true);
            } else {
                placed.set(false);
            }
            return result;
        }
        placed.set(false);
        return false;
    }

    @Redirect(
            method = "tick(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/npc/Villager;J)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;gameEvent(Lnet/minecraft/world/level/gameevent/GameEvent;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/gameevent/GameEvent$Context;)V")
    )
    private void springlotus$disableGameEvent(ServerLevel instance, GameEvent holder, BlockPos pos,
                                              GameEvent.Context context) {
    }

    @ModifyVariable(
            method = "tick(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/npc/Villager;J)V",
            at = @At(value = "STORE"),
            name = "ok"
    )
    private boolean springlotus$modifyOk(boolean ok,
                                         @Share("springlotus$placed") LocalBooleanRef placed) {
        return placed.get();
    }
}
