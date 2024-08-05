package org.eu.smileyik.numericalrequirements.core.event.network;

import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * 当服务器给玩家发送加载区块包时触发.
 */
public class LoadPlayerChunkEvent extends PlayerNetworkEvent {
    private final World world;
    private final int chunkX;
    private final int chunkZ;

    public LoadPlayerChunkEvent(Player player, World world, int chunkX, int chunkZ) {
        super(player);
        this.world = world;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
    }

    public World getWorld() {
        return world;
    }

    public int getChunkX() {
        return chunkX;
    }

    public int getChunkZ() {
        return chunkZ;
    }
}
