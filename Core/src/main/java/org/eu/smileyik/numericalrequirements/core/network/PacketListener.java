package org.eu.smileyik.numericalrequirements.core.network;

public interface PacketListener {
    /**
     * 获取要监听的包名
     * @return
     */
    String getPacketName();

    /**
     * 处理传入包，如果要拦截该包则返回null或其他包实例, 否则返回super.handlePacketIn()
     *
     * @param handler
     * @param packet
     * @return
     */
    default Object handlePacketIn(PlayerChannelHandler handler, Object packet) {
        return packet;
    }

    /**
     * 处理传出包, 如果要拦截该包则返回null或其他包实例,否则返回super.handlePacketOut()
     * @param handler
     * @param packet
     * @return
     */
    default Object handlePacketOut(PlayerChannelHandler handler, Object packet) {
        return packet;
    };
}
