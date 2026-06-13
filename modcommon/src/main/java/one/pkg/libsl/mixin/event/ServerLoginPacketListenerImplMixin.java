/*
 * Copyright (C) 2026  404Setup.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package one.pkg.libsl.mixin.event;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.yggdrasil.ProfileResult;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.login.ServerboundKeyPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import net.minecraft.server.notifications.ServerActivityMonitor;
import one.pkg.libsl.api.event.entity.ServerPlayerEvents;
import one.pkg.libsl.mixin.access.ConnectionAccess;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@Mixin(ServerLoginPacketListenerImpl.class)
public class ServerLoginPacketListenerImplMixin {
    @Final @Shadow
    private static Logger LOGGER;
    @Final @Shadow
    private static AtomicInteger UNIQUE_THREAD_ID;
    @Final @Shadow
    private Connection connection;
    @Final @Shadow
    private MinecraftServer server;
    @Shadow
    private String requestedUsername;
    @Shadow @Final
    private ServerActivityMonitor serverActivityMonitor;

    @Shadow
    private void startClientVerification(GameProfile profile) {
    }

    @Inject(
            method = "handleHello",
            at =
            @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerLoginPacketListenerImpl;startClientVerification(Lcom/mojang/authlib/GameProfile;)V",
                    ordinal = 1
            )
    )
    private void springLotus$handleHello(
            CallbackInfo ci,
            @Local(name = "singleplayerProfile") GameProfile singleplayerProfile
    ) {
        springLotus$runEvent(singleplayerProfile);
    }

    @Inject(
            method = "handleKey",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/Connection;setEncryptionKey(Ljavax/crypto/Cipher;Ljavax/crypto/Cipher;)V",
                    shift = At.Shift.AFTER
            ),
            cancellable = true
    )
    private void springLotus$handleKey(ServerboundKeyPacket packet,
                                       CallbackInfo ci, @Local(name = "digest") String digest) {
        Runnable runnable = () -> {
            String name = Objects.requireNonNull(requestedUsername, "Player name not initialized");

            try {
                ProfileResult result = server.services().sessionService().hasJoinedServer(
                        name, digest, springLotus$getAddress());
                if (result != null) {
                    GameProfile profile = result.profile();
                    if (!connection.isConnected()) {
                        return;
                    }
                    springLotus$runEvent(profile);

                    LOGGER.info("UUID of player {} is {}", profile.name(), profile.id());
                    serverActivityMonitor.reportLoginActivity();
                    startClientVerification(profile);
                } else if (server.isSingleplayer()) {
                    LOGGER.warn("Failed to verify username but will let them in anyway!");
                    startClientVerification(UUIDUtil.createOfflineProfile(name));
                } else {
                    disconnect(Component.translatable("multiplayer.disconnect.unverified_username"));
                    LOGGER.error("Username '{}' tried to join with an invalid session", name);
                }
            } catch (AuthenticationUnavailableException authenticationunavailableexception) {
                if (server.isSingleplayer()) {
                    LOGGER.warn("Authentication servers are down but will let them in anyway!");
                    startClientVerification(UUIDUtil.createOfflineProfile(digest));
                } else {
                    disconnect(Component.translatable("multiplayer.disconnect.authservers_down"));
                    LOGGER.error("Couldn't verify username because servers are unavailable");
                }
            }
        };

        Thread.ofVirtual().name("User Authenticator #" + UNIQUE_THREAD_ID.incrementAndGet())
                .uncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER)).start(runnable);
        ci.cancel();
    }

    @Unique
    private InetAddress springLotus$getAddress() {
        SocketAddress socketaddress = connection.getRemoteAddress();
        return server.getPreventProxyConnections() && socketaddress instanceof InetSocketAddress
                ? ((InetSocketAddress) socketaddress).getAddress()
                : null;
    }

    @Shadow
    public void disconnect(Component component) {
    }

    @Unique
    void springLotus$runEvent(GameProfile profile) {
        if (ServerPlayerEvents.PRE_JOIN.canSkip()) return;

        InetAddress address = ((InetSocketAddress) this.connection.getRemoteAddress()).getAddress();

        InetAddress rawAddress = ((InetSocketAddress)
                ((ConnectionAccess) this.connection).getChannel()
                        .remoteAddress()).getAddress();

        Component result = ServerPlayerEvents.PRE_JOIN.invoker().onPreJoin(
                address, rawAddress, profile
        );

        if (result != null) {
            this.disconnect(result);
        }
    }
}
