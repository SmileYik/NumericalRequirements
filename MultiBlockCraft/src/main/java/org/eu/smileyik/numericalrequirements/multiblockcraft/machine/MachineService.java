package org.eu.smileyik.numericalrequirements.multiblockcraft.machine;

import org.bukkit.block.Block;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.data.SimpleMachineDataService;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.Recipe;

import java.io.File;
import java.util.Collection;

public interface MachineService {
    void load();

    void setMachineMetadata(Block block, String key, String value);

    String delMachineMetadata(Block block, String key);

    String getMachineMetadata(Block block, String key);

    Machine getMachine(String id);

    Collection<String> getMachineIds();

    File createRecipe(String machineId, Recipe recipe);

    void save();

    void stop();

    SimpleMachineDataService getMachineDataService();
}
