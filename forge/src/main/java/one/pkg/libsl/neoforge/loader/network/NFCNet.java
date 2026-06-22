/*
 * Copyright (C) 2026  404Setup.
 */
package one.pkg.libsl.neoforge.loader.network;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import one.pkg.libsl.api.network.CNetHandler;
import one.pkg.libsl.api.network.ICNet;
import one.pkg.libsl.api.network.NetSrc;
import org.jetbrains.annotations.NotNull;

public class NFCNet implements ICNet {
    private final NFNet parent;
    public NFCNet(NFNet parent) { this.parent = parent; }

    @Override
    public void send(@NotNull Object payload, Object... payloads) { }

    @Override
    public boolean canSend(ResourceLocation channelName) { return true; }

    @Override
    public <T> void registerGlobalReceiver(ResourceLocation id, CNetHandler handler, NetSrc.Direction direction) {}

    @Override
    public <T> boolean isRegistered(ResourceLocation id) { return true; }
}
