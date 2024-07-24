package org.eu.smileyik.numericalrequirements.multiblockcraft.recipe;

import org.bukkit.inventory.ItemStack;
import org.eu.smileyik.numericalrequirements.core.api.util.Pair;
import org.eu.smileyik.numericalrequirements.multiblockcraft.MultiBlockCraftExtension;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.event.RecipeFormatItemEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface OrderedRecipe {
    String getShapeString();
    boolean isMatch(Pair<Byte[], ItemStack[]> pair);

    static Pair<Byte[], ItemStack[]> spawnShape(ItemStack[] inputs) {
        return spawnShape(null, inputs);
    }

    static Pair<Byte[], ItemStack[]> spawnShape(Recipe recipe, ItemStack[] inputs) {
        final long eventId = RecipeFormatItemEvent.nextId();

        byte idx = 0;
        List<Byte> shape = new ArrayList<>();
        List<ItemStack> idItemList = new ArrayList<>();
        Map<ItemStack, Byte> itemIdMap = new HashMap<>();
        int size = inputs.length - 1;
        for (int i = 0; i <= size; i++) {
            ItemStack item = inputs[i];
            if (item != null) {
                item = item.clone();

                // call recipe format item event
                RecipeFormatItemEvent event = new RecipeFormatItemEvent(recipe, eventId, i, i, size, item);
                MultiBlockCraftExtension.getInstance().getPlugin().getServer().getPluginManager().callEvent(event);
                item = event.getItem();

                if (item != null && item.getAmount() != 1) item.setAmount(1);
            }

            byte id = itemIdMap.getOrDefault(item, idx);
            if (idx == id) {
                itemIdMap.put(item, id);
                idItemList.add(item);
                ++idx;
            }
            shape.add(id);
        }
        return Pair.newUnchangablePair(
                shape.toArray(new Byte[0]),
                idItemList.toArray(new ItemStack[0])
        );
    }
}
