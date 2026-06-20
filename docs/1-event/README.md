# SpringLotus Event System

To reduce cross-platform development effort, I borrowed some foundational code from the Fabric API to build the event
system.

If you're already familiar with the Fabric API event system, using this will be very straightforward, as shown below:

```java
package one.pkg.libsl.fabric;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.minecraft.commands.Commands;
import one.pkg.libsl.api.event.client.command.ClientCommandRegistrationEvent;
import one.pkg.libsl.api.event.command.CommandRegistrationEvent;
import one.pkg.libsl.api.loader.JavaLoader;
import one.pkg.libsl.internal.command.SLCommand;


public class SpringLotusMod implements ModInitializer {
    @Override
    public void onInitialize() {
        CommandRegistrationEvent.EVENT.register(
                (dispatcher, _, _) -> SLCommand.register(dispatcher, Commands.CommandSelection.INTEGRATED)
        );

        if (JavaLoader.INSTANCE.isClient())
            onClientInitialize();
    }

    @Environment(EnvType.CLIENT)
    private void onClientInitialize() {
        ClientCommandRegistrationEvent.EVENT.register(
                (dispatcher, _) -> SLCommand.register(dispatcher, Commands.CommandSelection.INTEGRATED)
        );
    }
}
```

SpringLotus doesn't wrap too many APIs, as they may not be very commonly used. However, beyond that, SpringLotus also
supports some unique APIs — it's not just a simple wrapper.

## Cancelling Events

Since the Fabric-style event API cannot set priorities, your event may be cancelled by an event that executes earlier.

The method for cancelling an event is as follows:

```java
package one.pkg.libsl.fabric;

import net.fabricmc.api.ModInitializer;
import one.pkg.libsl.api.event.block.BlockBreakEvent;

public class SpringLotusMod implements ModInitializer {
    @Override
    public void onInitialize() {
        BlockBreakEvent.EVENT.register(((player, _, _, _) -> !player.isSleeping()));
    }
}
```

In this code block, if the player is in a sleeping state, the player's block-breaking event is cancelled.

Yes, if the method has a return value — such as a boolean — you can return `false`. When SpringLotus processes the
event, it will cancel the subsequent execution of that event. The resulting behavior is that the player is unable to
break the block.

## API List

### Block

- BlockBreakEvent.PLAYER_BREAK — Triggered after a block is broken by a player. Can be cancelled by returning a boolean
  value.
- BlockBreakEvent.ENTITY_UPDATE - Triggered when an entity updates or breaks a block. Can be cancelled by returning a
  boolean value.
- BlockExplosionEvent — Triggered when an explosion event occurs. The passed-in list can be modified to determine
  whether the blocks in the list should be destroyed.
- BlockPlaceEvent — Triggered when a block is placed by an entity. Can be cancelled by returning a boolean value.
- BlockSpreadEvent — Triggered when a block spreads to another block. Can be cancelled by returning a boolean value.

### Command

- ClientCommandRegistrationEvent — Client-side only. Triggered when the client registers commands.
- CommandRegistrationEvent — Triggered when the server registers commands.

### Lifecycle

- ClientLifecycleEvents.CLIENT_STARTED — Client-side only. Triggered when the Minecraft client has finished initializing
  and is ready.
- ClientLifecycleEvents.CLIENT_STOPPED — Client-side only. Triggered when the Minecraft client receives a stop command
  and has completed shutting down.
- ClientLifecycleEvents.CLIENT_STOPPING — Client-side only. Triggered when the Minecraft client receives a stop command
  and begins executing shutdown tasks.
- ServerLevelEvents.LOAD — Triggered when the server loads a level.
- ServerLevelEvents.UNLOAD — Triggered when the server unloads a level.
- ServerLifecycleEvents.ABOUT_STARTING — Triggered when the server is about to start.
- ServerLifecycleEvents.STARTING — Triggered when the server begins starting.
- ServerLifecycleEvents.STARTED — Triggered when the server has finished starting.
- ServerLifecycleEvents.STOPPING — Triggered when the server begins shutting down.
- ServerLifecycleEvents.STOPPED — Triggered when the server has finished shutting down.

### Entity

- ServerLivingEntityEvents.ALLOW_DAMAGE — Triggered when an entity is about to take damage. Can be cancelled by
  returning a boolean value.
- ServerLivingEntityEvents.AFTER_DAMAGE — Triggered after an entity takes damage.
- ServerLivingEntityEvents.ALLOW_DEATH — Triggered before an entity is about to die. Can be cancelled by returning a
  boolean value.
- ServerLivingEntityEvents.AFTER_DEATH — Triggered after an entity dies.
- ServerLivingEntityEvents.PRE_SPAWN — Triggered before an entity is about to spawn. Can be cancelled by returning a
  boolean value.
- ServerPlayerEvents.AFTER_RESPAWN — Triggered after a player respawns.
- ServerPlayerEvents.JOIN — Triggered after a player joins the server.
- ServerPlayerEvents.PRE_JOIN — Triggered before a player is about to join the server. A Component value can be returned
  to reject the player's joining; if a null value is returned, the event will not be interrupted.
- ServerPlayerEvents.LEAVE — Triggered when a player leaves the server.
- ServerPlayerEvents.AFK — Triggered after a player enters AFK mode. This event is not yet implemented; registering it
  will not result in it being called.
- ServerPlayerEvents.TELEPORT — Triggered when a player teleports. Can be cancelled by returning a boolean value.
- ServerPlayerEvents.END_GATEWAY — Triggered when a player passes through an end gateway. Can be cancelled by returning
  a boolean value.
- ServerPlayerEvents.MOVE — Triggered when a player moves. Can be cancelled by returning a boolean value.
- ServerPlayerEvents.LANG_CHANGED — Triggered when a player switches their client language.
- ServerPlayerEvents.CLIENT_OPTIONS_CHANGED — Triggered when a player modifies their client settings.
- ServerPlayerEvents.CHUNK_LOADED — Triggered after a player triggers a chunk load.
- ServerPlayerEvents.CHUNK_UNLOADED — Triggered after a player triggers a chunk unload.
- ServerPlayerEvents.DROP_ITEM — Triggered after a player drops an item. Can be cancelled by returning a boolean value.

### Item

- InventoryEvents.CLICKED — Triggered when a player clicks an item in a container menu. Can be cancelled by returning a
  boolean value.
- ItemBrokeEvent — Triggered when an item used by a player breaks.