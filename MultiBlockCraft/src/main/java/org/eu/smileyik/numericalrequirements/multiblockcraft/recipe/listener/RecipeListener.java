package org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.eu.smileyik.numericalrequirements.core.api.item.tag.lore.LoreValue;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.Recipe;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.event.RecipeFormatItemEvent;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.event.RecipeTakeItemEvent;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.tag.DurabilityLore;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.tag.NotConsumableInputLore;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RecipeListener implements Listener {
    private final DurabilityLore durabilityLore;
    private final NotConsumableInputLore notConsumableInputLore;
    private final Set<Long> checkingRecipeTakeItem = new HashSet<>();
    private final Set<Long> checkingRecipeFormatItem = new HashSet<>();

    public RecipeListener(DurabilityLore durabilityLore, NotConsumableInputLore notConsumableInputLore) {
        this.durabilityLore = durabilityLore;
        this.notConsumableInputLore = notConsumableInputLore;
    }


    @EventHandler(ignoreCancelled = true)
    public synchronized void onRecipeTakeItem(RecipeTakeItemEvent event) {
        boolean checked = checkingRecipeTakeItem.contains(event.getId());
        if (!checked) {
            ItemStack item = event.getItem();
            if (handleLoreItem(event.getRecipe(), item, event)) {
                event.setCancelled(true);
                checkingRecipeTakeItem.add(event.getId());
            }
        }
        if (event.getIndex() == event.getMax()) checkingRecipeTakeItem.remove(event.getId());
    }

    @EventHandler
    public synchronized void onRecipeFormatItem(RecipeFormatItemEvent event) {
        // 若该物品含有耐久度标签，则移除此标签
        // 与此同时如果该物品堆叠数量不为1,则跳过格式化，
        // 使得堆叠含有耐久度标签的物品作为材料进行合成时，不会通过配方比对校验
        ItemStack item = event.getItem();
        if (item == null || item.getAmount() > 1) return;
        if (!item.hasItemMeta()) return;
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasLore()) return;
        List<String> lore = meta.getLore();
        for (int i = lore.size() - 1; i >= 0; i--) {
            if (durabilityLore.matches(lore.get(i))) {
                lore.remove(i);
                break;
            }
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        event.setItem(item);
    }

    private boolean handleLoreItem(Recipe recipe, ItemStack item, RecipeTakeItemEvent event) {
        if (!item.hasItemMeta()) return false;
        ItemMeta itemMeta = item.getItemMeta();
        if (!itemMeta.hasLore()) return false;
        List<String> lore = itemMeta.getLore();
        int size = lore.size();
        for (int i = 0; i < size; i++) {
            String line = lore.get(i);
            if (notConsumableInputLore.matches(line)) {
                return true;
            } else if (durabilityLore.matches(line)) {
                LoreValue value = durabilityLore.getValue(line);
                int durability = value.get(0);
                durability -= 1;
                if (durability == 0) {
                    item.setAmount(0);
                    return true;
                }
                value.set(0, durability);
                line = durabilityLore.buildLore(value);
                lore.set(i, line);
                itemMeta.setLore(lore);
                item.setItemMeta(itemMeta);
                return true;
            }
        }
        return false;
    }
}
