/*
 * Copyright (C) 2026  404Setup.
 */
package one.pkg.libsl.neoforge.loader.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import one.pkg.libsl.api.network.*;
import org.jetbrains.annotations.NotNull;

public class NFNet implements INet {
    private final NFCNet client = new NFCNet(this);

    @Override
    public ICNet client() { return client; }

    @Override
    public boolean canSend(ServerPlayer player, ResourceLocation channelName) { return true; }

    @Override
    public boolean canSend(ServerGamePacketListenerImpl listener, ResourceLocation channelName) { return true; }

    @Override
    public <T> void serverHandler(@NotNull ResourceLocation type, @NotNull NetHandler handler) {}

    @Override
    public <T> void clientHandler(@NotNull ResourceLocation type, @NotNull CNetHandler handler) {}
}
