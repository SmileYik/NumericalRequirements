package org.eu.smileyik.numericalrequirements.core.network.listener;

import org.bukkit.World;
import org.bukkit.plugin.PluginManager;
import org.eu.smileyik.numericalrequirements.core.api.NumericalRequirements;
import org.eu.smileyik.numericalrequirements.core.event.network.LoadPlayerChunkEvent;
import org.eu.smileyik.numericalrequirements.core.network.PacketListener;
import org.eu.smileyik.numericalrequirements.core.network.PlayerChannelHandler;
import org.eu.smileyik.numericalrequirements.nms.network.packet.PacketPlayOutMapChunk;

public class PacketPlayOutMapChunkListener implements PacketListener {
    private final PluginManager pluginManager = NumericalRequirements.getPlugin().getServer().getPluginManager();

    @Override
    public String getPacketName() {
        return "PacketPlayOutMapChunk";
    }

    @Override
    public Object handlePacketOut(PlayerChannelHandler handler, Object packet) {
        PacketPlayOutMapChunk chunk = new PacketPlayOutMapChunk(packet);
        World world = handler.getPlayer().getWorld();
        LoadPlayerChunkEvent event = new LoadPlayerChunkEvent(handler.getPlayer(), world, chunk.getX(), chunk.getZ());
        pluginManager.callEvent(event);
        return PacketListener.super.handlePacketOut(handler, packet);
    }
}
