package one.pkg.libsl.mixin.event;

// import net.minecraft.server.level.ServerLevel;
// import net.minecraft.server.level.ServerPlayer;
// import net.minecraft.server.network.ServerGamePacketListenerImpl;
// import net.minecraft.world.level.ChunkPos;
// import net.minecraft.world.level.chunk.LevelChunk;
// import one.pkg.libsl.api.event.entity.ServerPlayerEvents;
// import org.spongepowered.asm.mixin.Mixin;
// import org.spongepowered.asm.mixin.injection.At;
// import org.spongepowered.asm.mixin.injection.Inject;
// import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// @Mixin(Object.class)
public class PlayerChunkSenderMixin {
    /*
    @Inject(
            method = "sendChunk",
            at =
            @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V",
                    shift = At.Shift.AFTER
            )
    )
    private static void onChunkSend(ServerGamePacketListenerImpl connection, ServerLevel level, LevelChunk chunk, CallbackInfo ci) {
        if (!ServerPlayerEvents.CHUNK_LOADED.canSkip())
            ServerPlayerEvents.CHUNK_LOADED.invoker().onChunkLoaded(connection.player, chunk);
    }

    @Inject(
            method = "dropChunk",
            at =
            @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V",
                    shift = At.Shift.AFTER
            )
    )
    private static void onChunkDrop(ServerPlayer player, ChunkPos pos, CallbackInfo ci) {
        if (!ServerPlayerEvents.CHUNK_UNLOADED.canSkip())
            ServerPlayerEvents.CHUNK_UNLOADED.invoker().onChunkUnloaded(player, pos);
    }
    */
}
