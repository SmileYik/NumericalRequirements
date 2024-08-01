package org.eu.smileyik.numericalrequirements.core.network;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.eu.smileyik.numericalrequirements.core.NumericalRequirements;
import org.eu.smileyik.numericalrequirements.debug.DebugLogger;
import org.eu.smileyik.numericalrequirements.nms.NMSNetwork;
import org.jetbrains.annotations.NotNull;

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
        if (playerChannelHandlers.containsKey(player.getName())) return;
        PlayerChannelHandler playerChannelHandler = new PlayerChannelHandler();
        if (NMSNetwork.registerChannelListener(player, "nreq-core-listener", playerChannelHandler)) {
            DebugLogger.debug("Registered channel listener for player: %s", player.getName());
            playerChannelHandlers.putIfAbsent(player.getName(), playerChannelHandler);
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
