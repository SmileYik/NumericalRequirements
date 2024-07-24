package org.eu.smileyik.numericalrequirements.multiblockcraft.machine.event;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Cancellable;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.Machine;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.data.MachineData;

public class MachineDataLoadEvent extends MachineDataEvent implements Cancellable {
    private final ConfigurationSection section;
    private final String chunkId;
    private boolean cancelled = false;

    public MachineDataLoadEvent(Machine machine, String identifier, MachineData machineData, ConfigurationSection section, String chunkId) {
        super(machine, identifier, machineData);
        this.section = section;
        this.chunkId = chunkId;
    }

    public MachineDataLoadEvent(boolean isAsync, Machine machine, String identifier, MachineData machineData, ConfigurationSection section, String chunkId) {
        super(isAsync, machine, identifier, machineData);
        this.section = section;
        this.chunkId = chunkId;
    }

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

    public String getChunkId() {
        return chunkId;
    }
}
