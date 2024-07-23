package org.eu.smileyik.numericalrequirements.multiblockcraft.machine;

import org.eu.smileyik.numericalrequirements.multiblockcraft.multiblock.structure.StructureMainBlock;

public interface MultiBlockMachine extends Machine {

    /**
     * 获取多方块结构主方块。
     * @return
     */
    StructureMainBlock getStructure();

    /**
     * 获取检查多方块结构成型的间隔。
     * @return
     */
    long getCheckPeriod();
}
