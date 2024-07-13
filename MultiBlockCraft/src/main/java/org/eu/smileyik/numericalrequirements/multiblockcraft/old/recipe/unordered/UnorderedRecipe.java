package org.eu.smileyik.numericalrequirements.multiblockcraft.old.recipe.unordered;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.eu.smileyik.numericalrequirements.multiblockcraft.old.recipe.AbstractRecipe;

import java.util.*;

public class UnorderedRecipe extends AbstractRecipe {
    protected Map<ItemStack, Integer> materialAmountMap;
    protected final Set<ItemStack> unconsumeMaterials = new HashSet<>();
    protected ItemStack[] materials;

    public UnorderedRecipe(String name) {
        super(name);
    }

    public void setMaterials(ItemStack[] materials) {
        List<ItemStack> list = new ArrayList<>();
        for (ItemStack itemStack : materials) {
            if (itemStack != null) list.add(itemStack);
        }
        this.materials = list.toArray(new ItemStack[0]);
        materialAmountMap = collect(this.materials);
        unconsumeMaterials.clear();

        // find unconsume material
        for (ItemStack itemStack : materialAmountMap.keySet()) {
            if (!itemStack.hasItemMeta() || !itemStack.getItemMeta().hasLore()) {
                continue;
            }
            List<String> lore = itemStack.getItemMeta().getLore();
            for (String str : lore) {
                if (UNCONSUME_TAG.matches(str)) {
                    unconsumeMaterials.add(itemStack);
                    break;
                }
            }
        }
    }

    @Override
    public boolean isMatch(ItemStack[] items) {
        if (items == null || items.length == 0) {
            return false;
        }
        Map<ItemStack, Integer> collect = collect(items);

        for (Map.Entry<ItemStack, Integer> entry : materialAmountMap.entrySet()) {
            int left = collect.getOrDefault(entry.getKey(), 0) - entry.getValue();
            if (left < 0) return false;
            collect.put(entry.getKey(), left);
        }
        return true;
    }

    @Override
    public boolean takeMaterials(ItemStack[] items) {
        if (!isMatch(items)) {
            return false;
        }
        HashMap<ItemStack, Integer> map = new HashMap<>(materialAmountMap);

        for (ItemStack item : items) {
            if (item == null) continue;
            ItemStack clone = item.clone();
            if (unconsumeMaterials.contains(clone)) continue;
            clone.setAmount(1);
            Integer amount = map.getOrDefault(clone, 0);
            if (amount == 0) continue;
            if (item.getAmount() >= amount) {
                item.setAmount(item.getAmount() - amount);
                map.remove(clone);
            } else {
                amount -= item.getAmount();
                map.put(clone, amount);
                item.setAmount(0);
            }
        }
        return true;
    }

    protected Map<ItemStack, Integer> collect(ItemStack[] items) {
        Map<ItemStack, Integer> materialAmountMap = new HashMap<>();
        for (ItemStack item : items) {
            if (item == null) continue;
            ItemStack clone = item.clone();
            clone.setAmount(1);
            materialAmountMap.put(clone, materialAmountMap.getOrDefault(clone, 0) + item.getAmount());
        }
        return materialAmountMap;
    }

    @Override
    public Collection<ItemStack> getMaterials() {
        return Arrays.asList(materials);
    }

    @Override
    public void load(ConfigurationSection section) {
        super.load(section);
        ConfigurationSection materialSection = section.getConfigurationSection("materials");
        List<ItemStack> itemStacks = new ArrayList<>();
        for (String key : materialSection.getKeys(false)) {
            itemStacks.add(materialSection.getItemStack(key));
        }
        setMaterials(itemStacks.toArray(new ItemStack[0]));
    }

    @Override
    public void store(ConfigurationSection section) {
        super.store(section);
        ConfigurationSection materialSection = section.createSection("materials");
        for (int i = 0; i < materials.length; ++i) {
            materialSection.set(String.valueOf(i), materials[i]);
        }
    }
}
