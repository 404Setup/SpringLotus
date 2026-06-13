/*
 * Copyright (C) 2026  404Setup.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package one.pkg.libsl.mixin.event;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.projectile.hurtingprojectile.Fireball;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ServerExplosion;
import one.pkg.libsl.api.event.block.BlockExplosionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(ServerExplosion.class)
public abstract class ServerExplosionMixin implements Explosion {
    @Unique
    private static final Logger clickFinder$log = LoggerFactory.getLogger(ServerExplosionMixin.class);
    @Shadow
    @Final
    private ServerLevel level;
    @Shadow
    @Final
    private Entity source;

    @Inject(method = "calculateExplodedPositions", at = @At("RETURN"))
    private void onCalculateExplodedPositions(CallbackInfoReturnable<ObjectArrayList<BlockPos>> cir) {
        if (this.level.isClientSide() || BlockExplosionEvent.EVENT.canSkip()) return;
        ObjectArrayList<BlockPos> toBlow = cir.getReturnValue();
        if (toBlow == null || toBlow.isEmpty()) return;

        Entity actualSource = this.source;
        if (actualSource instanceof PrimedTnt tnt) {
            LivingEntity owner = tnt.getOwner();
            if (owner != null) {
                actualSource = owner;
            }
        } else if (actualSource instanceof Creeper creeper) {
            LivingEntity target = creeper.getTarget();
            if (target != null) {
                actualSource = target;
            }
        } else if (actualSource instanceof Fireball fireball) {
            Entity owner = fireball.getOwner();
            if (owner instanceof Ghast ghast) {
                LivingEntity target = ghast.getTarget();
                actualSource = Objects.requireNonNullElse(target, ghast);
            } else if (owner != null) {
                actualSource = owner;
            }
        }

        BlockExplosionEvent.EVENT.invoker().onBlockExplosion(actualSource, level, toBlow);
    }
}
