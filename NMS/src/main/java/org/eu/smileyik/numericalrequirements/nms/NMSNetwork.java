package org.eu.smileyik.numericalrequirements.nms;

import io.netty.channel.Channel;
import org.bukkit.entity.Player;
import org.eu.smileyik.numericalrequirements.debug.DebugLogger;

public class NMSNetwork {

    /**
     * 注册玩家频道监听器。
     * @param player
     * @param channelName
     * @param handler
     * @return
     */
    public static boolean registerChannelListener(Player player, String channelName, NMSPlayerChannelHandler handler) {
        try {
            Channel channel = CraftPlayer.getHandle(player)
                                        .playerConnection()
                                        .getNetworkManager()
                                        .channel();
            synchronized (channel) {
                if (channel.isOpen()) {
                    handler.setChannel(channel);
                    handler.setPlayer(player);
                    channel.pipeline().addBefore("packet_handler", channelName, handler);
                }
            }
        } catch (Throwable t) {
            DebugLogger.debug(t);
            return false;
        }
        return true;
    }
}
