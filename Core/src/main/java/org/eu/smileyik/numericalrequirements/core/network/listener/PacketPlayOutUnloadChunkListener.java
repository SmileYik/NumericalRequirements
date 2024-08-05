package org.eu.smileyik.numericalrequirements.core.network.listener;

import org.bukkit.World;
import org.bukkit.plugin.PluginManager;
import org.eu.smileyik.numericalrequirements.core.api.NumericalRequirements;
import org.eu.smileyik.numericalrequirements.core.event.network.UnloadPlayerChunkEvent;
import org.eu.smileyik.numericalrequirements.core.network.PacketListener;
import org.eu.smileyik.numericalrequirements.core.network.PlayerChannelHandler;
import org.eu.smileyik.numericalrequirements.nms.network.packet.PacketPlayOutUnloadChunk;

public class PacketPlayOutUnloadChunkListener implements PacketListener {
    private final PluginManager pluginManager = NumericalRequirements.getPlugin().getServer().getPluginManager();

    @Override
    public String getPacketName() {
        return "PacketPlayOutUnloadChunk";
    }

    @Override
    public Object handlePacketOut(PlayerChannelHandler handler, Object packet) {
        PacketPlayOutUnloadChunk chunk = new PacketPlayOutUnloadChunk(packet);
        World world = handler.getPlayer().getWorld();
        UnloadPlayerChunkEvent event = new UnloadPlayerChunkEvent(handler.getPlayer(), world, chunk.getX(), chunk.getZ());
        pluginManager.callEvent(event);
        return PacketListener.super.handlePacketOut(handler, packet);
    }
}
