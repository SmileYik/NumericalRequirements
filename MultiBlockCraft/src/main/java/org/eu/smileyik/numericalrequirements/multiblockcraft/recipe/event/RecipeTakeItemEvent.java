package org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.event;

import org.bukkit.event.Cancellable;
import org.bukkit.inventory.ItemStack;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.Recipe;

/**
 * 当调用 Recipe.takeInputs 时触发。若取消该事件则将要被拿的原材料物品不会被扣减。
 */
public class RecipeTakeItemEvent extends RecipeEvent implements Cancellable {
    private boolean cancelled;
    private final ItemStack item;

    public RecipeTakeItemEvent(Recipe recipe, ItemStack item) {
        super(recipe);
        this.item = item;
    }

    public RecipeTakeItemEvent(boolean isAsync, Recipe recipe, ItemStack item) {
        super(isAsync, recipe);
        this.item = item;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    /**
     * 获取将要被扣减的物品。
     * @return
     */
    public ItemStack getItem() {
        return item;
    }
}
