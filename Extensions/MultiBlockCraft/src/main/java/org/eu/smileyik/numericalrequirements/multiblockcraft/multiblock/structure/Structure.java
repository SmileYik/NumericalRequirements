package org.eu.smileyik.numericalrequirements.multiblockcraft.multiblock.structure;

import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.eu.smileyik.numericalrequirements.core.api.util.Pair;
import org.eu.smileyik.numericalrequirements.debug.DebugLogger;

import java.util.function.BiConsumer;

public interface Structure extends Iterable<Pair<StructureFace, Structure>>{
    /**
     * 存储多方块结构
     * @param section
     */
    void store(ConfigurationSection section);

    /**
     * 从存储片段中读取数据
     * @param section
     */
    void load(ConfigurationSection section);

    /**
     * 是否为同一个方块。
     * @param block
     * @return
     */
    boolean isSameBlock(Block block);

    /**
     * 设定方块
     * @param block
     */
    void setBlock(Block block);

    /**
     * 设定本结构周围6个面的某个面的方块
     * @param way 0～5, 代表上下左右前后共六个面
     * @param block
     * @return
     */
    Structure set(int way, Block block);

    /**
     * 设定本结构周围6个面的某个面的方块
     * @param structureFace
     * @param block
     * @return
     */
    Structure set(StructureFace structureFace, Block block);

    /**
     * 获取本方块周围6个面的某一面
     * @param way 0～5, 代表上下左右前后共六个面
     * @return
     */
    Structure getNear(int way);

    /**
     * 获取周围6个面对应的方块
     * @param structureFace
     * @return
     */
    Structure getNear(StructureFace structureFace);

    /**
     * 获取周围6个面对应方块
     * @return
     */
    Structure[] getNears();

    /**
     * 从配置文件片段中读取多方块结构
     * @param section
     * @return
     */
    static Structure newMultiBlockStructure(ConfigurationSection section) {
        if (section == null) return null;
        String type = section.getString("type", null);
        MultiBlockStructure structure = null;
        if (type != null) {
            try {
                structure = (MultiBlockStructure) Class.forName(type).getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                DebugLogger.debug(e);
            }
        }
        if (structure == null) {
            structure = new MultiBlockStructure();
        }
        structure.load(section);
        return structure;
    }

    /**
     * 遍历本方块周围其他有方块的面。
     * @param biConsumer
     */
    void forEach(BiConsumer<StructureFace, Structure> biConsumer);
}
