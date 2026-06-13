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
import net.minecraft.core.TypedInstance;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SyncedDataHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.debug.DebugValueSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.ScoreHolder;
import one.pkg.libsl.api.Vec3d;
import one.pkg.libsl.api.instance.AsEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Set;

@Mixin(Entity.class)
public abstract class EntityMixin implements AsEntity,
        EntityAccess,
        ScoreHolder,
        SyncedDataHolder,
        DataComponentGetter,
        ItemOwner,
        SlotProvider,
        DebugValueSource,
        TypedInstance<EntityType<?>> {
    @Shadow
    public double xo;

    @Shadow
    public double yo;

    @Shadow
    public double zo;
    @Shadow
    public PortalProcessor portalProcess;
    @Shadow
    private float yRot;
    @Shadow
    private float xRot;
    @Shadow
    private Level level;

    @Shadow
    public abstract void load(ValueInput input);

    @Shadow
    public abstract boolean teleportTo(ServerLevel level, double x, double y, double z, Set<Relative> relatives, float newYRot, float newXRot, boolean resetCamera);

    @Shadow
    public abstract void teleportTo(double x, double y, double z);

    @Override
    public PortalProcessor getPortalProcessor() {
        return portalProcess;
    }

    @Override
    public Vec3d getPos() {
        return new Vec3d(this.xo, this.yo, this.zo, this.yRot, this.xRot, this.level);
    }

    @Override
    public void load(ProblemReporter problemReporter, RegistryAccess registryAccess, CompoundTag tag) {
        load(TagValueInput.create(problemReporter, registryAccess, tag));
    }

    @Override
    public void teleportTo(Vec3d pos) {
        if (pos.getLevel() instanceof ServerLevel)
            teleportTo((ServerLevel) pos.getLevel(),
                    pos.getX(),
                    pos.getY(),
                    pos.getZ(),
                    Set.of(), pos.getYaw(), pos.getPitch(),
                    false);
        else teleportTo(pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    public void teleportTo(Vec3 pos) {
        teleportTo(pos.x, pos.y, pos.z);
    }
}
