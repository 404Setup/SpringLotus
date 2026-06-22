/*
 * Copyright (C) 2026  404Setup.
 */
package one.pkg.libsl.neoforge.loader.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import one.pkg.libsl.api.network.*;

public class NFONet implements INet {
    private final NFNet parent;
    public NFONet(NFNet parent) { this.parent = parent; }
    public ICNet client() { return parent.client(); }
    public boolean canSend(ServerPlayer p, ResourceLocation c) { return parent.canSend(p, c); }
    public boolean canSend(ServerGamePacketListenerImpl p, ResourceLocation c) { return parent.canSend(p, c); }
    public <T> void serverHandler(ResourceLocation t, NetHandler h) { parent.serverHandler(t, h); }
    public <T> void clientHandler(ResourceLocation t, CNetHandler h) { parent.clientHandler(t, h); }
}
