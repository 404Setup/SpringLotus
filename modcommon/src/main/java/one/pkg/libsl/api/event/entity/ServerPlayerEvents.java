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

import com.mojang.authlib.GameProfile;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import one.pkg.libsl.api.Vec3d;
import one.pkg.libsl.api.event.Event;

import java.net.InetAddress;

/**
 * Interface for events related to server-side player activity.
 * Provides a set of events that can be utilized to hook into specific
 * moments of a player's lifecycle or actions in a multiplayer server environment.
 */
@SuppressWarnings("all")
public interface ServerPlayerEvents {
    /**
     * An event that is triggered after a server player has respawned. This allows for custom logic
     * to be executed post-respawn, such as resetting properties, re-equipping items, or other
     * gameplay adjustments.
     * <p>
     * Event listeners for this event are implementations of the {@link ServerPlayerEvents.AfterRespawn}
     * functional interface. All registered callbacks will be invoked in order when the event occurs.
     * <p>
     * The event uses a factory to combine multiple callbacks into a single invoker, which is called
     * when the respawn event occurs.
     */
    Event<ServerPlayerEvents.AfterRespawn> AFTER_RESPAWN = Event.create(ServerPlayerEvents.AfterRespawn.class,
            callbacks -> (player) -> {
                for (AfterRespawn callback : callbacks) {
                    callback.afterRespawn(player);
                }
            });

    /**
     * An event triggered when a player joins the server.
     * <p>
     * This event allows registering callbacks that are invoked whenever a player joins.
     * The registered callbacks receive the joining player as a parameter and perform
     * operations accordingly.
     * <p>
     * The invoker for this event aggregates all registered {@link Join} callbacks
     * and executes their {@code onJoin(ServerPlayer)} method when the event is triggered.
     */
    Event<Join> JOIN = Event.create(Join.class, callbacks -> player -> {
        for (Join callback : callbacks) {
            callback.onJoin(player);
        }
    });

    /**
     * An event triggered before a player joins the game session. This allows implementing custom logic
     * such as validation, modifications, or pre-join behaviors before the player is fully connected.
     * <p>
     * The event utilizes the {@code PreJoin} functional interface, enabling the registration of multiple
     * listeners. Each listener is invoked sequentially. If any listener cancels the event through the
     * {@code CancelableResults} object, subsequent listeners are not executed.
     * <p>
     * Listeners will be passed the following parameters:
     * - The player's resolved IP address.
     * - The player's raw (unvalidated) IP address.
     * - The player's {@code GameProfile}.
     * <p>
     * Cancellation of this event results in the associated join process being stopped, and the returned
     * message will be sent to the player.
     */
    Event<PreJoin> PRE_JOIN = Event.create(PreJoin.class, callbacks -> (
            ipAddress,
            rawAddress,
            profile) -> {
        for (PreJoin callback : callbacks) {
            Component result = callback.onPreJoin(ipAddress, rawAddress, profile);
            if (result != null) return result;
        }
        return null;
    });

    /**
     * Event triggered when a server player leaves the game or a specific area.
     * <p>
     * This event allows registering callbacks that are executed when a {@link ServerPlayer}
     * triggers a leave action. All registered {@code Leave} callbacks are invoked with the
     * player information as input.
     * <p>
     * The event is an implementation of {@link Event}, constructed with a functional interface
     * {@link Leave} that defines the {@code onLeave} method. When a player leaves, the event
     * iterates through all registered callbacks and invokes their {@code onLeave} methods.
     * <p>
     * This event is particularly useful for server-side plugins to monitor and react
     * to players leaving specific contexts, enabling customized handling or logging.
     */
    Event<Leave> LEAVE = Event.create(Leave.class, callbacks -> player -> {
        for (Leave callback : callbacks) {
            callback.onLeave(player);
        }
    });

    /**
     * An event that is triggered when a player is considered AFK (Away From Keyboard).
     * This event allows listeners to handle actions or behaviors for players who become inactive.
     * <p>
     * The event utilizes a functional interface {@link ServerPlayerEvents.AFK} which defines the
     * `onAFK(Player player)` method. When a player becomes AFK, all registered callbacks for this
     * event are invoked sequentially.
     * <p>
     * The AFK status is determined by external logic, and this event only signals the occurrence
     * of that state change.
     * <p>
     * Listeners can register to this event to implement custom behaviors, such as notifying other
     * players, logging activity, or enforcing rules related to inactivity.
     */
    Event<AFK> AFK = Event.create(AFK.class, callbacks -> player -> {
        for (AFK callback : callbacks) {
            callback.onAFK(player);
        }
    });

    /**
     * An event triggered when a player is teleported from one location to another.
     * This event allows for custom handling of player teleportation actions by invoking
     * all registered {@link Teleport} callbacks. Each callback can determine if the
     * teleportation should proceed by returning a boolean value. If any callback
     * returns false, the teleportation is canceled.
     * <p>
     * The event provides the player instance, the current position before teleportation,
     * and the new position after teleportation to all registered callbacks for processing.
     */
    Event<Teleport> TELEPORT = Event.create(Teleport.class, callbacks ->
            (player, currentPos, newPos) -> {
                for (Teleport callback : callbacks) {
                    if (!callback.onTeleport(player, currentPos, newPos))
                        return false;
                }
                return true;
            });

    /**
     * Represents an event triggered when a player teleports via an End Gateway in the game world.
     * This event allows for the registration of custom behaviors or checks that are performed during
     * the End Gateway teleportation process.
     * <p>
     * Multiple listeners can register callbacks which will be invoked in sequence whenever the event occurs.
     * If any callback returns {@code false}, the teleportation is canceled.
     * <p>
     * The registered callbacks conform to the {@link Teleport.EndGateway} functional interface, which provides
     * access to details about the player, their current position, their intended position, and the End Gateway
     * block facilitating the teleportation.
     */
    Event<Teleport.EndGateway> END_GATEWAY = Event.create(Teleport.EndGateway.class, callbacks ->
            (player, currentPos, newPos, blockEntity) -> {
                for (Teleport.EndGateway callback : callbacks) {
                    if (!callback.onEndGatewayTeleport(player, currentPos, newPos, blockEntity))
                        return false;
                }
                return true;
            });

    /**
     * An event triggered when a player moves from one position to another in the game world.
     * <p>
     * The event allows for multiple listeners to handle movement behavior. If any listener
     * cancels the movement by returning {@code false} from the {@code onMove} method, the
     * movement will be denied for the player. All registered callbacks will be invoked in order
     * until either all complete successfully or one cancels the movement.
     * <p>
     * Listeners for this event must implement the {@link Move} functional interface, which
     * processes the player, their starting position, and their destination position.
     */
    Event<Move> MOVE = Event.create(Move.class, callbacks ->
            (player, from, to) -> {
                for (Move callback : callbacks) {
                    if (!callback.onMove(player, from, to))
                        return false;
                }
                return true;
            });

    /**
     * An event that triggers when a player's language setting changes.
     * <p>
     * This event is used to notify registered listeners when a player updates their preferred language,
     * typically through client options. Each registered listener will be invoked with the player instance
     * and the new language code chosen by the player.
     * <p>
     * The event listener must implement the {@link LangChanged} functional interface, which provides
     * a single method, {@code onLangChanged}, to handle the language change logic.
     */
    Event<LangChanged> LANG_CHANGED = Event.create(LangChanged.class, callbacks ->
            (player, lang) -> {
                for (LangChanged callback : callbacks) {
                    callback.onLangChanged(player, lang);
                }
            });

    /**
     * An event triggered whenever a server-side player's client updates its configuration or settings.
     * This includes changes such as control preferences, graphical settings, language, or other client-side
     * options that the server needs to be aware of.
     * <p>
     * Registered listeners for this event are invoked in order, each handling the changes for the specific player
     * and their updated client settings encapsulated in the provided information.
     * <p>
     * The event uses the {@link Event} mechanism to manage and invoke the {@link ClientOptionsChanged} callbacks.
     */
    Event<ClientOptionsChanged> CLIENT_OPTIONS_CHANGED = Event.create(ClientOptionsChanged.class,
            callbacks ->
                    (player, information) -> {
                        for (ClientOptionsChanged callback : callbacks) {
                            callback.onClientOptionsChanged(player, information);
                        }
                    });

    /**
     * An event triggered whenever a chunk is loaded for a player. This event allows
     * for handling specific actions or behaviors that should occur when a given chunk
     * is sent to a player during gameplay.
     * <p>
     * The event is represented as an {@link Event} containing {@link ChunkLoaded}
     * listeners, which are invoked sequentially for each chunk loaded.
     * <p>
     * Registered callbacks within this event receive the {@link ServerPlayer} and
     * the {@link LevelChunk} being loaded, enabling the implementation of custom behavior.
     */
    Event<ChunkLoaded> CHUNK_LOADED = Event.create(ChunkLoaded.class, callbacks ->
            (player, chunk) -> {
                for (ChunkLoaded callback : callbacks) {
                    callback.onChunkLoaded(player, chunk);
                }
            }
    );

    /**
     * An event triggered when a chunk is unloaded for a specific player on the server.
     * This event allows listeners to define custom behavior for handling the chunk unloading event.
     * <p>
     * CHUNK_UNLOADED is a registry for listeners implementing the {@link ChunkUnloaded} functional interface.
     * Registered listeners will be notified in order when the event is fired.
     * <p>
     * The event is invoked for each registered listener with the specified {@link ServerPlayer} and {@link ChunkPos}.
     */
    Event<ChunkUnloaded> CHUNK_UNLOADED = Event.create(ChunkUnloaded.class, callbacks ->
            (player, pos) -> {
                for (ChunkUnloaded callback : callbacks) {
                    callback.onChunkUnloaded(player, pos);
                }
            }
    );

    /**
     * An event triggered when a player attempts to drop an item. This event allows
     * custom behaviors or logic to be executed before the item drop occurs.
     * Listeners can decide whether the drop action should proceed or be canceled.
     * <p>
     * The event uses a chain of registered {@link DropItem} listeners. Each listener
     * is called sequentially. If any listener returns {@code false}, the drop
     * action is canceled, and further listeners are not invoked.
     * <p>
     * The event is powered by an {@link Event}, which manages the registration
     * of {@link DropItem} listeners and the invocation logic.
     * <p>
     * Type: {@code Event<DropItem>}
     * Listener Type: {@link DropItem}
     */
    Event<DropItem> DROP_ITEM = Event.create(DropItem.class, callbacks ->
            (player, item) -> {
                for (DropItem callback : callbacks) {
                    if (!callback.onDropItem(player, item))
                        return false;
                }
                return true;
            });

    /**
     * Functional interface representing an event hook that is triggered when a player attempts to drop an item.
     * Implementations can provide custom logic to determine whether the drop action should be permitted or canceled.
     */
    @FunctionalInterface
    interface DropItem {
        /**
         * This method is triggered when a player attempts to drop an item.
         * It allows for custom logic to determine the outcome of the drop action.
         *
         * @param player the {@link ServerPlayer} instance representing the player who is performing the drop action
         * @param item   the {@link ItemStack} instance representing the item being dropped
         * @return true if the item drop should proceed, false to cancel the drop
         */
        boolean onDropItem(ServerPlayer player, ItemStack item);
    }

    /**
     * This functional interface represents an event callback for when a chunk is loaded for a player.
     * Implementations of this interface can define actions to execute when a specific chunk is sent
     * to a player during gameplay.
     */
    @FunctionalInterface
    interface ChunkLoaded {
        /**
         * Called when a chunk is loaded for a player. This method can be used to handle any
         * logic that should occur when a specific chunk is sent to a player.
         *
         * @param player the {@link ServerPlayer} who loaded the chunk
         * @param chunk  the {@link LevelChunk} that was loaded
         */
        void onChunkLoaded(ServerPlayer player, LevelChunk chunk);
    }

    /**
     * Represents a functional interface that handles the event of a chunk being unloaded
     * for a specific player. This allows developers to implement custom behavior when
     * a chunk becomes unavailable to a player on the server.
     */
    @FunctionalInterface
    interface ChunkUnloaded {
        /**
         * Called when a chunk is unloaded for a specific player.
         *
         * @param player the {@link ServerPlayer} for whom the chunk is unloaded
         * @param pos    the {@link ChunkPos} representing the position of the unloaded chunk
         */
        void onChunkUnloaded(ServerPlayer player, ChunkPos pos);
    }

    /**
     * Functional interface representing an event handler for changes in client options.
     * This event is triggered whenever a server-side player's client updates its configuration
     * or settings, such as control preferences, graphical settings, language, or other options
     * that the server may need to be aware of.
     * <p>
     * Implementations of this interface should define how to handle the changes when they occur.
     */
    @FunctionalInterface
    interface ClientOptionsChanged {
        /**
         * Handles changes in client options for a specific player.
         * This method is typically invoked when a player's client updates
         * its configuration or settings.
         *
         * @param player      the server-side player whose client options have changed
         * @param information the updated client information containing the new settings or options
         */
        void onClientOptionsChanged(ServerPlayer player, ClientInformation information);
    }

    /**
     * Defines a functional interface for handling events related to changes in a player's language setting.
     * <p>
     * This event is triggered whenever a player updates their preferred language, often through the client settings.
     */
    @FunctionalInterface
    interface LangChanged {
        /**
         * Called when a player's language setting changes.
         *
         * @param player the player whose language has changed
         * @param lang   the new language code the player has selected
         */
        void onLangChanged(ServerPlayer player, String lang);
    }

    /**
     * Represents a functional interface that is triggered when a player is considered AFK (Away From Keyboard).
     * This can be used to handle events or actions that should occur when a player becomes inactive.
     */
    @FunctionalInterface
    interface AFK {
        /**
         * Triggered when a player is considered to be AFK (Away From Keyboard).
         *
         * @param player the player who is considered AFK
         */
        void onAFK(Player player);
    }

    /**
     * Represents a functional interface for handling teleportation events involving players.
     * Teleportation can occur between various locations and this interface allows for custom handling
     * of such events.
     */
    @FunctionalInterface
    interface Teleport {
        /**
         * Handles the event triggered when a player is teleported.
         *
         * @param player     The player who is being teleported.
         * @param currentPos The current position of the player before teleportation.
         * @param newPos     The new position of the player after teleportation.
         * @return True if the teleportation is allowed and proceeds, false otherwise.
         */
        boolean onTeleport(ServerPlayer player, Vec3d currentPos, Vec3d newPos);

        /**
         * Functional interface for handling teleportation events involving an End Gateway.
         * This interface provides a single method that is invoked when a player uses an End Gateway block
         * to teleport between locations.
         */
        @FunctionalInterface
        interface EndGateway {
            /**
             * Handles the event triggered when a player teleports through an End Gateway.
             *
             * @param player          The player who is teleporting.
             * @param currentPos      The current position of the player before teleportation.
             * @param newPos          The new position of the player after teleportation.
             * @param endGatewayBlock The End Gateway block entity facilitating the teleportation.
             * @return True if the teleportation is successful, false otherwise.
             */
            boolean onEndGatewayTeleport(ServerPlayer player, Vec3d currentPos, Vec3d newPos, TheEndGatewayBlockEntity endGatewayBlock);
        }
    }

    /**
     * Represents a functional interface used to handle behavior when a player moves
     * from one position to another within the game world.
     */
    @FunctionalInterface
    interface Move {
        /**
         * Handles the movement of a player from one position to another.
         *
         * @param player The player object performing the movement.
         * @param from   The starting position of the player.
         * @param to     The destination position of the player.
         * @return A boolean indicating whether the movement is allowed (true) or canceled (false).
         */
        boolean onMove(ServerPlayer player, Vec3d from, Vec3d to);
    }

    /**
     * Represents a functional interface that is triggered after a server player has respawned.
     * It allows for custom post-respawn logic to be executed, such as resetting player-specific
     * properties, re-equipping items, or applying buffs.
     */
    @FunctionalInterface
    interface AfterRespawn {
        /**
         * Called after a server player has respawned. This method allows handling
         * events or actions that need to occur after the player has been resurrected,
         * such as reinitializing custom data, granting items, or other post-respawn logic.
         *
         * @param player the server player who has respawned
         */
        void afterRespawn(ServerPlayer player);
    }

    /**
     * A functional interface representing an event that is triggered when a player joins the server.
     */
    @FunctionalInterface
    interface Join {
        /**
         * Called when a player joins the server.
         *
         * @param player the player who joined the server
         */
        void onJoin(ServerPlayer player);
    }

    /**
     * Represents a callback that is triggered prior to a player joining a game session.
     * This functional interface can be implemented to apply custom logic, such as validating
     * the player or modifying behaviors, before the join process is finalized.
     */
    @FunctionalInterface
    interface PreJoin {
        /**
         * Invoked before a player joins the game, allowing for validation or custom logic
         * before completing the join process.
         *
         * @param ipAddress         the resolved IP address of the connecting player
         * @param rawAddress        the raw IP address of the connecting player, provided without validation
         * @param profile           the game profile of the connecting player
         * @return a Component containing the disconnect reason if the event is cancelled, or null to allow
         */
        Component onPreJoin(
                InetAddress ipAddress,
                InetAddress rawAddress,
                GameProfile profile
        );
    }

    /**
     * Functional interface representing a leave event for server players.
     * This event is triggered when a server player leaves the game or a specific area.
     */
    @FunctionalInterface
    interface Leave {
        /**
         * Called when a server player leaves the game or a specific area.
         *
         * @param player the server player who triggered the leave event
         */
        void onLeave(ServerPlayer player);
    }
}
