package org.eu.smileyik.numericalrequirements.multiblockcraft.machine.event;

import org.bukkit.event.Cancellable;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.Machine;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.Recipe;

/**
 * 寻找匹配配方事件。当机器寻找到匹配的配方时触发。
 */
public class FindRecipeEvent extends MachineEvent implements Cancellable {
    private final Recipe recipe;
    private boolean cancelled;

    public FindRecipeEvent(Machine machine, Recipe recipe) {
        super(machine);
        this.recipe = recipe;
    }

    public FindRecipeEvent(boolean isAsync, Machine machine, Recipe recipe) {
        super(isAsync, machine);
        this.recipe = recipe;
    }

    /**
     * 获取当前配方。
     * @return
     */
    public Recipe getRecipe() {
        return recipe;
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
