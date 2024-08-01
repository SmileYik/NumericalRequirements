package org.eu.smileyik.numericalrequirements.core.network;

public interface PacketListener {
    /**
     * 获取要监听的包名
     * @return
     */
    String getPacketName();

    /**
     * 处理传入包，如果要阻止该包传入则返回true.
     * @param handler
     * @param packet
     * @return
     */
    boolean handlePacketIn(PlayerChannelHandler handler, Object packet);

    /**
     * 处理传出包, 如果要阻止包继续传出则返回true.
     * @param handler
     * @param packet
     * @return
     */
    boolean handlePacketOut(PlayerChannelHandler handler, Object packet);
}
