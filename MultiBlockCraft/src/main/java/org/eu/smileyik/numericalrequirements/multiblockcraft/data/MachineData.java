package org.eu.smileyik.numericalrequirements.multiblockcraft.data;

import org.bukkit.configuration.ConfigurationSection;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.Machine;

public interface MachineData {

    /**
     * 所属机器
     * @return
     */
    Machine getMachine();

    /**
     * 标识符
     * @return
     */
    String getIdentifier();

    /**
     * 保存
     * @param section
     */
    void store(ConfigurationSection section);

    /**
     * 加载
     * @param section
     */
    void load(ConfigurationSection section);
}
