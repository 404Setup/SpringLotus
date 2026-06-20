/*
 * Copyright (C) 2026  404Setup.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package one.pkg.libsl.fabric;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLevelEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import one.pkg.libsl.Static;
import one.pkg.libsl.api.event.block.BlockBreakEvents;
import one.pkg.libsl.api.event.block.BlockPlaceEvent;
import one.pkg.libsl.api.event.client.command.ClientCommandRegistrationEvent;
import one.pkg.libsl.api.event.command.CommandRegistrationEvent;
import one.pkg.libsl.api.loader.JavaLoader;
import one.pkg.libsl.fabric.loader.FLoader;
import one.pkg.libsl.internal.command.SLCommand;
import one.pkg.libsl.internal.network.InternalNetworkInit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SpringLotusMod implements ModInitializer {
    private final Logger logger = LoggerFactory.getLogger(Static.MOD_NAME);

    private void initEvents() {
        ServerLevelEvents.LOAD.register(
                (server, level) ->
                        one.pkg.libsl.api.event.lifecycle.ServerLevelEvents.LOAD.invoker().onLevelLoad(server, level)
        );

        ServerLevelEvents.UNLOAD.register(
                (server, level) ->
                        one.pkg.libsl.api.event.lifecycle.ServerLevelEvents.UNLOAD.invoker().onLevelUnload(server, level)
        );

        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            ((FLoader) JavaLoader.INSTANCE).updateServer(server);
            one.pkg.libsl.api.event.lifecycle.ServerLifecycleEvents.STARTING.invoker().onServerStarting(server);
        });
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            one.pkg.libsl.api.event.lifecycle.ServerLifecycleEvents.STARTED.invoker().onServerStarted(server);
        });
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            ((FLoader) JavaLoader.INSTANCE).updateServer(null);
            one.pkg.libsl.api.event.lifecycle.ServerLifecycleEvents.STOPPING.invoker().onServerStopping(server);
        });
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            one.pkg.libsl.api.event.lifecycle.ServerLifecycleEvents.STOPPED.invoker().onServerStopped(server);
        });

        ServerPlayerEvents.AFTER_RESPAWN.register((_, newPlayer, _) ->
                one.pkg.libsl.api.event.entity.ServerPlayerEvents.AFTER_RESPAWN.invoker().afterRespawn(newPlayer)
        );
        ServerPlayerEvents.JOIN.register(
                player ->
                        one.pkg.libsl.api.event.entity.ServerPlayerEvents.JOIN.invoker().onJoin(player)
        );
        ServerPlayerEvents.LEAVE.register(
                player ->
                        one.pkg.libsl.api.event.entity.ServerPlayerEvents.LEAVE.invoker().onLeave(player)
        );
        PlayerBlockBreakEvents.BEFORE.register(
                (level, player, pos, state, _) ->
                        BlockBreakEvents.PLAYER_BREAK.invoker().onPlayerBreak(player, level, pos, state)
        );

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (world.isClientSide() || hand != InteractionHand.MAIN_HAND) {
                return InteractionResult.PASS;
            }

            BlockPos targetPos = world.getBlockState(hitResult.getBlockPos()).hasBlockEntity() ?
                    hitResult.getBlockPos() :
                    hitResult.getBlockPos().relative(hitResult.getDirection());

            ItemStack heldItem = player.getItemInHand(hand);
            boolean isPlacing = heldItem.getItem() instanceof BlockItem;

            if (!isPlacing) {
                return InteractionResult.PASS;
            }

            BlockState logState = world.getBlockState(hitResult.getBlockPos().relative(hitResult.getDirection()));

            if (!BlockPlaceEvent.EVENT.invoker().onBlockPlace(
                    player, targetPos, world, logState,
                    ((BlockItem) heldItem.getItem()).getBlock().defaultBlockState()
            )) {
                return InteractionResult.TRY_WITH_EMPTY_HAND;
            }
            return InteractionResult.PASS;
        });

        ServerLivingEntityEvents.ALLOW_DAMAGE.register((
                entity,
                source,
                amount
        ) -> one.pkg.libsl.api.event.entity.ServerLivingEntityEvents.ALLOW_DAMAGE
                .invoker().allowDamage(entity, source, amount));

        ServerLivingEntityEvents.AFTER_DAMAGE.register((
                entity,
                source,
                baseDamageTaken,
                damageTaken,
                blocked
        ) -> one.pkg.libsl.api.event.entity.ServerLivingEntityEvents.AFTER_DAMAGE
                .invoker().afterDamage(entity, source, baseDamageTaken, damageTaken, blocked));

        ServerLivingEntityEvents.ALLOW_DEATH.register((
                        entity,
                        damageSource,
                        damageAmount
                ) -> one.pkg.libsl.api.event.entity.ServerLivingEntityEvents.ALLOW_DEATH
                        .invoker().allowDeath(entity, damageSource, damageAmount)
        );

        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) ->
                one.pkg.libsl.api.event.entity.ServerLivingEntityEvents.AFTER_DEATH
                        .invoker().afterDeath(entity, damageSource));

        CommandRegistrationEvent.EVENT.register(
                (dispatcher, _, _) ->
                        SLCommand.register(dispatcher, Commands.CommandSelection.INTEGRATED)
        );

        if (JavaLoader.INSTANCE.isClient())
            onClientInitialize();
    }

    @Override
    public void onInitialize() {
        initEvents();

        InternalNetworkInit.init();
    }

    /**
     * Initializes the mod on the client side.
     */
    @Environment(EnvType.CLIENT)
    private void onClientInitialize() {
        ClientLifecycleEvents.CLIENT_STARTED.register(
                client ->
                        one.pkg.libsl.api.event.client.lifecycle.ClientLifecycleEvents.
                                CLIENT_STARTED.invoker().onClientStarted(client)
        );

        ClientLifecycleEvents.CLIENT_STOPPING.register(
                client ->
                        one.pkg.libsl.api.event.client.lifecycle.ClientLifecycleEvents.
                                CLIENT_STOPPING.invoker().onClientStopping(client)
        );


        ClientCommandRegistrationEvent.EVENT.register(
                (dispatcher, _) -> {
                    SLCommand.register(dispatcher, Commands.CommandSelection.INTEGRATED);
                }
        );
    }
}
