package org.eu.smileyik.numericalrequirements.nms;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import org.bukkit.entity.Player;

public interface NMSPlayerChannelHandler extends ChannelHandler {
    /**
     * 获取玩家
     * @return
     */
    Player getPlayer();

    /**
     * 设置玩家
     * @param player
     */
    void setPlayer(Player player);

    /**
     * 获取信道
     * @return
     */
    Channel getChannel();

    /**
     * 设置信道
     * @param channel
     */
    void setChannel(Channel channel);
}
