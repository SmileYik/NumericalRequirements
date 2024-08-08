package org.eu.smileyik.numericalrequirements.multiblockcraft.machine.event;

import org.bukkit.configuration.ConfigurationSection;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.Machine;

/**
 * 机器加载事件，当机器被加载时触发.
 */
public class MachineLoadEvent extends MachineEvent {
    private final ConfigurationSection configuration;

    public MachineLoadEvent(Machine machine, ConfigurationSection configuration) {
        super(machine);
        this.configuration = configuration;
    }

    public MachineLoadEvent(boolean isAsync, Machine machine, ConfigurationSection configuration) {
        super(isAsync, machine);
        this.configuration = configuration;
    }

    /**
     * 获取机器配置片段。
     * @return
     */
    public ConfigurationSection getConfiguration() {
        return configuration;
    }
}
