package org.eu.smileyik.numericalrequirements.core.customblock;

import org.bukkit.Chunk;
import org.eu.smileyik.numericalrequirements.debug.DebugLogger;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractCustomBlockService implements CustomBlockService {

    /**
     * 根据方块ID获取方块.
     * @param blockId
     * @return
     */
    protected abstract CustomBlock getCustomBlock(@NotNull String blockId);

    /**
     * 根据物品ID获取使用该物品是所放置的方块.
     * @param itemId
     * @return
     */
    protected abstract CustomBlock getCustomBlockByItemId(@NotNull String itemId);

    /**
     * 根据位置来获取该位置上所存在的自定义方块类型.
     * @param pos
     * @return
     */
    protected abstract CustomBlock getCustomBlockByPos(@NotNull Pos pos);

    /**
     * 向服务注册自定义方块信息, 该信息应该被持久保存.
     * 该方法将在放置方块时触发.
     * @param customBlockData
     * @return 如果注册方块信息成功, 则需要返回 true.
     */
    protected abstract boolean registerCustomBlockData(@NotNull CustomBlockData customBlockData);

    /**
     * 卸载一个自定义方块信息.
     * 该方法将在破坏一个方块时触发.
     * @param customBlockData
     * @return 如果卸载方块信息成功, 则需要返回 true.
     */
    protected abstract boolean unregisterCustomBlockData(@NotNull CustomBlockData customBlockData);

    /**
     * 加载区块内的方块. 该方法是放置那些已经放置的, 但是服务器中未显示的方块.
     * 该方法并非放置新的方块.
     * @param chunk
     */
    protected abstract void loadChunkBlocks(Chunk chunk);

    /**
     * 取消加载区块内的方块, 即移除该方块的显示, 但是不应该移除该方块的注册信息.
     * @param chunk
     */
    protected abstract void unloadChunkBlocks(Chunk chunk);

    /**
     * 放置一个自定义方块
     * @param block
     * @param pos
     * @return 如果成功注册此方块的信息则返回true代表放置方块成功.
     */
    public synchronized boolean placeCustomBlock(@NotNull CustomBlock block, @NotNull Pos pos) {
        if (registerCustomBlockData(new CustomBlockData(pos, block.getId()))) {
            DebugLogger.debug("place custom block %s, %s", block.getId(), pos);
            block.place(pos);
            return true;
        }
        return false;
    }

    /**
     * 破坏一个自定义方块, 并且并不会产生掉落物.
     * @param block
     * @param pos
     * @return 如果成功卸载该方块的信息则返回true代表破坏成功.
     */
    public synchronized boolean breakCustomBlock(@NotNull CustomBlock block, @NotNull Pos pos) {
        if (unregisterCustomBlockData(new CustomBlockData(pos, block.getId()))) {
            DebugLogger.debug("break custom block %s, %s", block.getId(), pos);
            block.remove(pos);
            return true;
        }
        return false;
    }
}
