package org.eu.smileyik.numericalrequirements.multiblockcraft.multiblock.structure;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.eu.smileyik.numericalrequirements.multiblockcraft.multiblock.MultiBlockFace;

public interface StructureMainBlock extends Structure {

    /**
     * 初始化多方块结构
     */
    void init();

    /**
     * 提供机器主方块及上下左右前后方向所对应的BlockFace来判断是否符合多方块结构。
     * @param block 主方块
     * @param ways 上下左右前后方向所对应的BlockFace数组
     * @return 如果匹配成功则返回true
     */
    boolean isMatch(Block block, BlockFace[] ways);

    /**
     * 判断某方块所在的结构是否符合此多方块结构
     * @param block 主方块
     * @param mainBlockFace 主方块的朝向
     * @return 如果匹配成功则返回true
     */
    default boolean isMatch(Block block, BlockFace mainBlockFace) {
        MultiBlockFace[] byFace = MultiBlockFace.getByFace(mainBlockFace);
        for (MultiBlockFace multiBlockFace : byFace) {
            if (isMatch(block, multiBlockFace.getFaces())) {
                return true;
            }
        }
        return false;
    }
}
