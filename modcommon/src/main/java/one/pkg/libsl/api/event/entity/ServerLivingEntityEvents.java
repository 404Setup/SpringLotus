/*
 * Copyright (C) 2026  404Setup.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package one.pkg.libsl.api.event.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.LivingEntity;
import one.pkg.libsl.api.event.Event;

/**
 * This interface contains event definitions for actions related to living entities on the server.
 * These events include hooks for intercepting or reacting to operations such as damage taken, death,
 * and related lifecycle moments for entities.
 */
public interface ServerLivingEntityEvents {
    /**
     * An event that is called when a living entity is going to take damage.
     * This is fired from {@link LivingEntity#hurt}, before armor or any other mitigation are applied.
     * Mods can cancel this to prevent the damage entirely.
     */
    Event<AllowDamage> ALLOW_DAMAGE = Event.create(AllowDamage.class,
            callbacks -> (entity, source, amount) -> {
                for (AllowDamage callback : callbacks) {
                    if (!callback.allowDamage(entity, source, amount)) return false;
                }
                return true;
            });

    /**
     * An event that is called after an entity is damaged. This is fired from {@link LivingEntity#hurt} after damage
     * is applied, or after that damage was blocked by a shield.
     *
     * <p>The base damage taken is the damage initially applied to the entity. Damage taken is the amount of damage the
     * entity actually took, after effects such as shields and extra freezing damage are applied. Damage taken does NOT
     * include damage reduction from armor and enchantments.
     *
     * <p>This event is not fired if the entity was killed by the damage.
     */
    Event<AfterDamage> AFTER_DAMAGE = Event.create(AfterDamage.class, callbacks -> (
            entity,
            source,
            baseDamageTaken,
            damageTaken,
            blocked
    ) -> {
        for (AfterDamage callback : callbacks) {
            callback.afterDamage(entity, source, baseDamageTaken, damageTaken, blocked);
        }
    });

    /**
     * An event that is called when an entity takes fatal damage.
     *
     * <p>Mods can cancel this to keep the entity alive.
     *
     * <p>Vanilla checks for entity health {@code <= 0} each tick (with {@link LivingEntity#isDeadOrDying()}), and kills if true -
     * so the entity will still die next tick if this event is cancelled.
     * It's assumed that the listener will do something to prevent this, for example, if the entity is a player:
     * <ul>
     *     <li>a minigame mod teleporting the player into a 'respawn room' and setting their health to 20.0</li>
     *     <li>a mod that changes death mechanics switching the player over to the mod's play-mode, where death doesn't apply</li>
     * </ul>
     */
    Event<AllowDeath> ALLOW_DEATH = Event.create(AllowDeath.class,
            callbacks -> (entity, damageSource, damageAmount) -> {
                for (AllowDeath callback : callbacks) {
                    if (!callback.allowDeath(entity, damageSource, damageAmount))
                        return false;
                }
                return true;
            });

    /**
     * An event triggered after a living entity has died. This event notifies all registered listeners
     * that the death of an entity has occurred and cannot be canceled at this point.
     * <p>
     * The event provides details about the entity that died and the damage source responsible for its demise.
     * Listeners registered to this event can perform additional actions or cleanup after an entity's death.
     * <p>
     * The invoker created for this event will iterate through all registered listeners and invoke their
     * {@link AfterDeath#afterDeath(LivingEntity, DamageSource)} method, passing the relevant details.
     */
    Event<AfterDeath> AFTER_DEATH = Event.create(AfterDeath.class,
            callbacks -> (entity, damageSource) -> {
                for (AfterDeath callback : callbacks) {
                    callback.afterDeath(entity, damageSource);
                }
            });

    /**
     * An event triggered before a living entity is spawned in the world.
     * Subscribers can implement custom logic to control whether the spawning process should proceed.
     * <p>
     * This event uses {@link PreSpawn} as a functional interface, which allows subscribers to define
     * conditions for allowing or denying entity spawn attempts. Multiple subscribers can be registered,
     * and each subscriber's logic will be executed in sequence.
     * <p>
     * If any subscriber returns {@code false} during its execution, the spawning process will be
     * halted, and the entity will not spawn. If all subscribers return {@code true}, the spawn
     * attempt will be allowed to proceed.
     *
     * @see PreSpawn
     */
    Event<PreSpawn> PRE_SPAWN = Event.create(PreSpawn.class, callbacks ->
            (entity, level, spawnPos, spawnReason) -> {
                for (PreSpawn callback : callbacks) {
                    if (!callback.onPreSpawn(entity, level, spawnPos, spawnReason)) {
                        return false;
                    }
                }

                return true;
            });


    @FunctionalInterface
    interface AllowDamage {
        /**
         * Called when a living entity is going to take damage. Can be used to cancel the damage entirely.
         *
         * <p>The amount corresponds to the "incoming" damage amount, before armor and other mitigations
         * have been applied.
         *
         * @param entity the entity
         * @param source the source of the damage
         * @param amount the amount of damage that the entity will take (before mitigations)
         * @return true if the damage should be allowed, false to cancel the damage
         */
        boolean allowDamage(LivingEntity entity, DamageSource source, float amount);
    }

    @FunctionalInterface
    interface AfterDamage {
        /**
         * Called after a living entity took damage, unless they were killed. The base damage taken is
         * given as damage taken before armor or enchantments are applied, but after other effects like
         * shields are applied.
         *
         * @param entity          the entity that was damaged
         * @param source          the source of the damage
         * @param baseDamageTaken the amount of damage initially dealt
         * @param damageTaken     the amount of damage actually taken by the entity, before armor and
         *                        enchantment effects
         * @param blocked         whether the damage was blocked by a shield
         */
        void afterDamage(LivingEntity entity, DamageSource source,
                         float baseDamageTaken, float damageTaken, boolean blocked);
    }

    @FunctionalInterface
    interface AllowDeath {
        /**
         * Called when a living entity takes fatal damage (before totems of undying can take effect).
         *
         * @param entity       the entity
         * @param damageSource the source of the fatal damage
         * @param damageAmount the amount of damage that has killed the entity
         * @return true if the death should be allowed, false to cancel
         */
        boolean allowDeath(LivingEntity entity, DamageSource damageSource, float damageAmount);
    }

    @FunctionalInterface
    interface AfterDeath {
        /**
         * Called when a living entity dies. The death cannot be canceled at this point.
         *
         * @param entity       the entity
         * @param damageSource the source of the fatal damage
         */
        void afterDeath(LivingEntity entity, DamageSource damageSource);
    }

    /**
     * Functional interface representing a callback that is executed before a living entity is spawned
     * in the world. It allows for custom logic to approve or deny the spawning process.
     * <p>
     * Implementations of this interface can define conditions under which a living entity's spawn
     * attempt should be allowed or prevented.
     */
    @FunctionalInterface
    interface PreSpawn {
        /**
         * Callback executed before a living entity is spawned in the world.
         * Allows for custom logic to determine whether the spawn attempt should proceed.
         *
         * @param entity      The living entity that is attempting to spawn.
         * @param level       The server level where the entity is attempting to spawn.
         * @param spawnPos    The block position where the entity is attempting to spawn.
         * @param spawnReason The reason or circumstances for the entity's spawn attempt.
         * @return {@code true} if the spawn attempt should be allowed, or {@code false} to prevent the entity from spawning.
         */
        boolean onPreSpawn(LivingEntity entity, ServerLevel level, BlockPos spawnPos, MobSpawnType spawnReason);
    }
}
