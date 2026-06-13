package one.pkg.libsl.fabric.mixin.client.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import one.pkg.libsl.api.event.client.command.ClientCommandRegistrationEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {

    @Unique
    private static CommandDispatcher<CommandSourceStack> libsl$clientDispatcher;
    @Shadow
    private CommandDispatcher<SharedSuggestionProvider> commands;

    @Unique
    private static CommandDispatcher<CommandSourceStack> libsl$getDispatcher() {
        if (libsl$clientDispatcher == null) {
            libsl$clientDispatcher = new CommandDispatcher<>();
            ClientCommandRegistrationEvent.EVENT.invoker().register(libsl$clientDispatcher, null);
        }
        return libsl$clientDispatcher;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Inject(method = "handleCommands", at = @At("RETURN"))
    private void libsl$onHandleCommands(net.minecraft.network.protocol.game.ClientboundCommandsPacket packet, CallbackInfo ci) {
        CommandDispatcher<CommandSourceStack> dispatcher = libsl$getDispatcher();
        for (CommandNode<CommandSourceStack> node : dispatcher.getRoot().getChildren()) {
            this.commands.getRoot().addChild((CommandNode) node);
        }
    }
}