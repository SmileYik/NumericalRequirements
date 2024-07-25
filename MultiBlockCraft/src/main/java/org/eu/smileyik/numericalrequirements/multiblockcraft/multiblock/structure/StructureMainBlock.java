package org.eu.smileyik.numericalrequirements.multiblockcraft.multiblock.structure;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.eu.smileyik.numericalrequirements.core.api.util.Pair;
import org.eu.smileyik.numericalrequirements.multiblockcraft.multiblock.MultiBlockFace;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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

    /**
     * 获取节点所对应的方块。
     * @param node 相对于主方块的节点
     * @param block 主方块
     * @param ways 正确的机器朝向，上下左右前后方向所对应的BlockFace数组
     * @return
     */
    Block getBlock(MultiBlockStructureMainBlock.Node node, Block block, BlockFace[] ways);

    List<MultiBlockStructureMainBlock.Node> getInputPath();

    List<MultiBlockStructureMainBlock.Node> getOutputPath();

    static StructureMainBlock create(Block clickedBlock, BlockFace[] ways, StructureMainBlock structure, int maxBlocks) {
        LinkedList<Pair<Structure, Block>> queue = new LinkedList<>();
        structure.setBlock(clickedBlock);
        queue.add(Pair.newPair(structure, clickedBlock));
        Set<Location> checked = new HashSet<>();
        checked.add(clickedBlock.getLocation());
        int count = 1;
        while (!queue.isEmpty() && count < maxBlocks) {
            Pair<Structure, Block> pair = queue.removeFirst();

            for (int i = ways.length - 1; i >= 0; i--) {
                Block block = pair.getSecond().getRelative(ways[i]);
                if (block != null && !block.isEmpty() && checked.add(block.getLocation())) {
                    queue.add(Pair.newPair(pair.getFirst().set(i, block), block));
                    if (++count > maxBlocks) break;
                }
            }
        }
        return structure;
    }
}
