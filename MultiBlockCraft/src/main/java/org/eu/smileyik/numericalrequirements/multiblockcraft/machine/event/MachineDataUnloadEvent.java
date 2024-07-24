package org.eu.smileyik.numericalrequirements.multiblockcraft.machine.event;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Cancellable;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.Machine;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.data.MachineData;

/**
 * 机器数据卸载事件，当机器数据卸载时触发。
 */
public class MachineDataUnloadEvent extends MachineDataEvent implements Cancellable {
    private final ConfigurationSection section;
    private final String chunkId;
    private boolean cancelled = false;

    public MachineDataUnloadEvent(Machine machine, String identifier, MachineData machineData, ConfigurationSection section, String chunkId) {
        super(machine, identifier, machineData);
        this.section = section;
        this.chunkId = chunkId;
    }

    public MachineDataUnloadEvent(boolean isAsync, Machine machine, String identifier, MachineData machineData, ConfigurationSection section, String chunkId) {
        super(isAsync, machine, identifier, machineData);
        this.section = section;
        this.chunkId = chunkId;
    }

    /**
     * 获取该机器所在区块的ID
     * @return
     */
    public String getChunkId() {
        return chunkId;
    }

    /**
     * 获取该机器将要保存到的配置片段。
     * @return
     */
    public ConfigurationSection getConfiguration() {
        return section;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
