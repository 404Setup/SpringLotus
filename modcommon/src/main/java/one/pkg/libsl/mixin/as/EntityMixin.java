/*
 * Copyright (C) 2026  404Setup.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package one.pkg.libsl.mixin.as;

import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.RelativeMovement;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.phys.Vec3;

import one.pkg.libsl.api.Vec3d;
import one.pkg.libsl.api.instance.AsEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Set;

@Mixin(Entity.class)
public abstract class EntityMixin implements AsEntity,
        EntityAccess {
    @Shadow
    public double xo;

    @Shadow
    public double yo;

    @Shadow
    public double zo;
    @Shadow
    private float yRot;
    @Shadow
    private float xRot;
    @Shadow
    private Level level;

    @Shadow
    public abstract void load(CompoundTag input);


    @Shadow
    public abstract void teleportTo(double x, double y, double z);

    @Override
    public Vec3d getPos() {
        return new Vec3d(this.xo, this.yo, this.zo, this.yRot, this.xRot, this.level);
    }


    @Override
    public void teleportTo(Vec3d pos) {
        teleportTo(pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    public void teleportTo(Vec3 pos) {
        teleportTo(pos.x, pos.y, pos.z);
    }
}
