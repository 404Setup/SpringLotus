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

@Mixin(Explosion.class)
public abstract class ServerExplosionMixin implements Explosion {
    @Unique
    private static final Logger clickFinder$log = LoggerFactory.getLogger(ServerExplosionMixin.class);
    @Shadow
    @Final
    private net.minecraft.world.level.Level level;
    @Shadow
    @Final
    private Entity source;

    @Inject(method = "getToBlow", at = @At("RETURN"), remap = true)
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

        BlockExplosionEvent.EVENT.invoker().onBlockExplosion(actualSource, (ServerLevel)level, toBlow);
    }
}
