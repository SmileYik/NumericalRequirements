package org.eu.smileyik.numericalrequirements.multiblockcraft.data;

public interface MachineDataService {
    MachineData loadMachineData(String identifier);

    void storeMachineData(MachineData machineData);

    void removeMachineData(String identifier);

    void save();
}
