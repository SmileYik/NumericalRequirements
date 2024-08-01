package org.eu.smileyik.numericalrequirements.core.network;

import org.jetbrains.annotations.NotNull;

public interface NetworkService {

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
}
