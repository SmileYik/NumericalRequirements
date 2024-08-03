package org.eu.smileyik.numericalrequirements.core.network;

import org.bukkit.entity.Player;
import org.eu.smileyik.numericalrequirements.nms.network.packet.Packet;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface NetworkService {
    /**
     * 给指定玩家发送数据包.
     * @param player
     * @param packet
     */
    void sendPacket(Player player, Packet packet);

    /**
     * 给指定玩家发送数据包.
     * @param player
     * @param packet
     */
    void sendPacket(Player player, Object packet);

    /**
     * 广播数据包.
     * @param packet
     */
    void broadcastPacket(Packet packet);

    /**
     * 广播数据包.
     * @param packet
     */
    void broadcastPacket(Object packet);

    /**
     * 广播数据包给指定玩家.
     * @param playerList
     * @param packet
     */
    void broadcastPacket(Collection<Player> playerList, Packet packet);

    /**
     * 广播数据包给指定玩家.
     * @param playerList
     * @param packet
     */
    void broadcastPacket(Collection<Player> playerList, Object packet);

    /**
     * 添加包监听器
     * @param packetListener
     */
    void addPacketListener(@NotNull PacketListener packetListener);

    /**
     * 移除包监听器
     * @param packetListener
     */
    void removePacketListener(@NotNull PacketListener packetListener);

    /**
     * 关闭服务
     */
    void shutdown();
}
