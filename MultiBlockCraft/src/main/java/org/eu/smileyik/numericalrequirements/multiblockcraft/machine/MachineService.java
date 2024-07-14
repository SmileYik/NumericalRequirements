package org.eu.smileyik.numericalrequirements.multiblockcraft.machine;

import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.Recipe;

import java.io.File;

public interface MachineService {
    Machine getMachine(String id);

    File createRecipe(String machineId, Recipe recipe);
}
