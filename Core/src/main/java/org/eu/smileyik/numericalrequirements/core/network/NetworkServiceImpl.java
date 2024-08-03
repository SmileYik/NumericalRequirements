package org.eu.smileyik.numericalrequirements.core.network;

import io.netty.channel.Channel;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.eu.smileyik.numericalrequirements.core.NumericalRequirements;
import org.eu.smileyik.numericalrequirements.debug.DebugLogger;
import org.eu.smileyik.numericalrequirements.nms.NMSNetwork;
import org.eu.smileyik.numericalrequirements.nms.network.packet.Packet;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class NetworkServiceImpl implements Listener, NetworkService {
    private final ConcurrentMap<String, PlayerChannelHandler> playerChannelHandlers = new ConcurrentHashMap<>();
    private final NumericalRequirements plugin;

    public NetworkServiceImpl(NumericalRequirements plugin) {
        this.plugin = plugin;

        DebugLogger.debug("network service created");
        listenAllPlayer();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    private synchronized void listenAllPlayer() {
        plugin.getServer().getOnlinePlayers().forEach(this::listen);
    }

    private synchronized void listen(Player player) {
        if (playerChannelHandlers.containsKey(player.getName())) {
            Channel channel = playerChannelHandlers.get(player.getName()).getChannel();
            if (channel.isOpen()) return;
            playerChannelHandlers.remove(player.getName());
        }
        PlayerChannelHandler playerChannelHandler = new PlayerChannelHandler();
        if (NMSNetwork.registerChannelListener(player, "nreq-core-listener", playerChannelHandler)) {
            DebugLogger.debug("Registered channel listener for player: %s", player.getName());
            playerChannelHandlers.putIfAbsent(player.getName(), playerChannelHandler);
        }
    }

    private synchronized void unlisten(Player player) {
        playerChannelHandlers.remove(player.getName());
    }

    @Override
    public synchronized void sendPacket(Player player, Packet packet) {
        this.sendPacket(player, packet.getInstance());
    }

    @Override
    public synchronized void sendPacket(Player player, Object packet) {
        if (playerChannelHandlers.containsKey(player.getName())) {
            playerChannelHandlers.get(player.getName()).sendPacket(packet);
        }
    }

    @Override
    public synchronized void broadcastPacket(Packet packet) {
        this.broadcastPacket(packet.getInstance());
    }

    @Override
    public synchronized void broadcastPacket(Object packet) {
        playerChannelHandlers.forEach((name, channelHandler) -> channelHandler.sendPacket(packet));
    }

    @Override
    public synchronized void broadcastPacket(Collection<Player> playerList, Packet packet) {
        this.broadcastPacket(playerList, packet.getInstance());
    }

    @Override
    public synchronized void broadcastPacket(Collection<Player> playerList, Object packet) {
        for (Player player : playerList) {
            this.sendPacket(player, packet);
        }
    }

    @Override
    public void addPacketListener(@NotNull PacketListener packetListener) {
        PlayerChannelHandler.registerPacketListener(packetListener);
    }

    @Override
    public void removePacketListener(@NotNull PacketListener packetListener) {
        PlayerChannelHandler.removePacketListener(packetListener);
    }

    @Override
    public void shutdown() {
        PlayerChannelHandler.clearPacketListeners();
        playerChannelHandlers.clear();
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        listen(event.getPlayer());
    }
}
