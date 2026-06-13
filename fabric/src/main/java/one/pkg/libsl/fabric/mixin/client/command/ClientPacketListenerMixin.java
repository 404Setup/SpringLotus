package one.pkg.libsl.fabric.mixin.client.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.permissions.PermissionSet;
import one.pkg.libsl.api.event.client.command.ClientCommandRegistrationEvent;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {

    @Unique
    private static CommandDispatcher<CommandSourceStack> libsl$clientDispatcher;

    @Unique
    private static CommandDispatcher<CommandSourceStack> libsl$getDispatcher() {
        if (libsl$clientDispatcher == null) {
            libsl$clientDispatcher = new CommandDispatcher<>();
            ClientCommandRegistrationEvent.EVENT.invoker().register(libsl$clientDispatcher, null);
        }
        return libsl$clientDispatcher;
    }

    @Inject(method = "sendCommand", at = @At("HEAD"), cancellable = true)
    private void libsl$onSendCommand(String command, CallbackInfo ci) {
        if (libsl$executeCommand(command)) {
            ci.cancel();
        }
    }

    @Inject(method = "sendUnattendedCommand", at = @At("HEAD"), cancellable = true)
    private void libsl$onSendUnattendedCommand(String command, Screen screenAfterCommand, CallbackInfo ci) {
        if (libsl$executeCommand(command)) {
            ci.cancel();
        }
    }

    @Unique
    private boolean libsl$executeCommand(String command) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return false;

        CommandDispatcher<CommandSourceStack> dispatcher = libsl$getDispatcher();

        CommandSource customSource = new CommandSource() {
            @Override
            public void sendSystemMessage(@NotNull Component message) {
                player.sendSystemMessage(message);
            }

            @Override
            public boolean acceptsSuccess() {
                return true;
            }

            @Override
            public boolean acceptsFailure() {
                return true;
            }

            @Override
            public boolean shouldInformAdmins() {
                return false;
            }
        };

        CommandSourceStack source = new CommandSourceStack(
                customSource,
                player.position(),
                player.getRotationVector(),
                null,
                player.permissions(),
                player.getName().getString(),
                player.getDisplayName(),
                null,
                player
        );

        ParseResults<CommandSourceStack> parse = dispatcher.parse(command, source);
        if (!parse.getContext().getNodes().isEmpty()) {
            try {
                dispatcher.execute(parse);
            } catch (CommandSyntaxException e) {
                player.sendSystemMessage(Component.literal(e.getMessage()));
            } catch (Exception e) {
                // Ignore unknown errors to let the command gracefully fail
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }
}
