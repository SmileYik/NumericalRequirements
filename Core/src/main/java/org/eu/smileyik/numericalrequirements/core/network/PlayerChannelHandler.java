package org.eu.smileyik.numericalrequirements.core.network;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.bukkit.entity.Player;
import org.eu.smileyik.numericalrequirements.debug.DebugLogger;
import org.eu.smileyik.numericalrequirements.nms.NMSPlayerChannelHandler;
import org.eu.smileyik.numericalrequirements.nms.network.packet.Packet;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class PlayerChannelHandler extends ChannelDuplexHandler implements NMSPlayerChannelHandler {
    private static final Map<String, Set<PacketListener>> packetListenerMap = new HashMap<>();

    private String playerName;
    private Player player;
    private Channel channel;
    private final NetworkService networkService;

    public PlayerChannelHandler(NetworkService networkService) {
        this.networkService = networkService;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            String simpleName = msg.getClass().getSimpleName();
            Set<PacketListener> packetListeners = packetListenerMap.get(simpleName);
            if (packetListeners != null) {
                for (PacketListener listener : packetListeners) {
                    msg = listener.handlePacketIn(this, msg);
                }
            }
        } catch (Throwable t) {
            DebugLogger.debug("Error handling packet in: %s", t);
            DebugLogger.debug(t);
        }
        if (msg != null) super.channelRead(ctx, msg);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        try {
            String simpleName = msg.getClass().getSimpleName();
            Set<PacketListener> packetListeners = packetListenerMap.get(simpleName);
            if (packetListeners != null) {
                for (PacketListener listener : packetListeners) {
                    msg = listener.handlePacketOut(this, msg);
                }
            }
        } catch (Throwable t) {
            DebugLogger.debug("Error handling packet out: %s", t);
            DebugLogger.debug(t);
        }
        if (msg != null) super.write(ctx, msg, promise);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        networkService.playerDisconnect(playerName);
        DebugLogger.debug("Player %s disconnecting: %s", playerName, ctx.channel().remoteAddress());
        player = null;
        channel = null;
        super.channelUnregistered(ctx);
    }

    /**
     * 注册一个包监听器
     * @param listener
     */
    protected static synchronized void registerPacketListener(@NotNull PacketListener listener) {
        if (!packetListenerMap.containsKey(listener.getPacketName())) {
            packetListenerMap.put(listener.getPacketName(), new LinkedHashSet<>());
        }
        DebugLogger.debug("registering packet listener for packet: %s", listener.getPacketName());
        packetListenerMap.get(listener.getPacketName()).add(listener);
    }

    /**
     * 取消注册已经注册了的包监听器
     * @param listener
     */
    protected static synchronized void removePacketListener(@NotNull PacketListener listener) {
        DebugLogger.debug("removing packet listener for packet: %s", listener.getPacketName());
        packetListenerMap.getOrDefault(listener.getPacketName(), Collections.emptySet()).remove(listener);
    }

    /**
     * 清空所有已经注册了的包监听器
     */
    protected static synchronized void clearPacketListeners() {
        packetListenerMap.clear();
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public Channel getChannel() {
        return channel;
    }

    @Override
    public void setPlayer(Player player) {
        this.playerName = player.getName();
        this.player = player;
    }

    @Override
    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public NetworkService getNetworkService() {
        return networkService;
    }

    public void sendPacket(Packet packet) {
        if (channel.isOpen()) channel.writeAndFlush(packet.getInstance());
    }

    public void sendPacket(Object packet) {
        if (channel.isOpen()) channel.writeAndFlush(packet);
    }
}
