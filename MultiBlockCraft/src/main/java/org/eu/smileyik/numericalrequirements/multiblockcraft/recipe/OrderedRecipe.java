package org.eu.smileyik.numericalrequirements.multiblockcraft.recipe;

import org.bukkit.inventory.ItemStack;
import org.eu.smileyik.numericalrequirements.core.api.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface OrderedRecipe {
    String getShapeString();
    boolean isMatch(Pair<Byte[], ItemStack[]> pair);

    static Pair<Byte[], ItemStack[]> spawnShape(ItemStack[] inputs) {
        byte idx = 0;
        List<Byte> shape = new ArrayList<>();
        List<ItemStack> idItemList = new ArrayList<>();
        Map<ItemStack, Byte> itemIdMap = new HashMap<>();
        for (ItemStack item : inputs) {
            if (item != null && item.getAmount() != 1) {
                item = item.clone();
                item.setAmount(1);
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
