package org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.ordered;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.eu.smileyik.numericalrequirements.core.util.Pair;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.AbstractRecipe;

import java.util.*;

public class OrderedRecipe extends AbstractRecipe {
    public static final String SHAPE_DELIMITER = ";";
    protected String shape;
    protected String[] shapeIdArray;
    protected Map<String, ItemStack> idMaterialMap;
    protected boolean checkMaterialAmount;
    protected Set<String> unconsumeMaterialId = new HashSet<>();

    public OrderedRecipe(String name) {
        super(name);
    }

    public void setMaterials(String shape, Map<String, ItemStack> idMaterialMap) {
        checkMaterialAmount = idMaterialMap.values().stream().anyMatch(it -> it.getAmount() > 1);
        Pair<String, Map<String, ItemStack>> normalize = normalize(Pair.newUnchangablePair(shape, idMaterialMap));
        this.shape = normalize.getFirst();
        this.idMaterialMap = normalize.getSecond();
        this.shapeIdArray = this.shape.split(SHAPE_DELIMITER);

        // find unconsume material
        idMaterialMap.forEach((id, material) -> {
            if (!material.hasItemMeta() || !material.getItemMeta().hasLore()) {
                return;
            }
            List<String> lore = material.getItemMeta().getLore();
            for (String str : lore) {
                if (UNCONSUME_TAG.matches(str)) {
                    unconsumeMaterialId.add(id);
                    return;
                }
            }
        });
    }

    public static Pair<String, Map<String, ItemStack>> spawnShape(ItemStack[] items, boolean countAmount) {
        int number = 0;
        Map<ItemStack, Integer> itemNumberMap = new HashMap<>();
        LinkedList<String> shape = new LinkedList<>();
        Map<String, ItemStack> idMaterialMap = new HashMap<>();
        for (ItemStack item : items) {
            if (item == null) {
                shape.add("-1");
                continue;
            }
            ItemStack clone = item.clone();
            if (!countAmount) clone.setAmount(1);
            if (!itemNumberMap.containsKey(clone)) {
                itemNumberMap.put(clone, number);
                idMaterialMap.put(String.valueOf(number), clone);
                number++;
            }
            int id = itemNumberMap.get(clone);
            shape.add(String.valueOf(id));
        }
        return Pair.newUnchangablePair(
            String.join(SHAPE_DELIMITER, shape), idMaterialMap
        );
    }

    public static Pair<String, Map<String, ItemStack>> normalize(Pair<String, Map<String, ItemStack>> shapeMaterialPair) {
        String[] shape = shapeMaterialPair.getFirst().split(SHAPE_DELIMITER);
        ItemStack[] materials = new ItemStack[shape.length];
        boolean checkAmount = false;
        for (int i = 0; i < shape.length; ++i) {
            if ("-1".equalsIgnoreCase(shape[i])) continue;
            materials[i] = shapeMaterialPair.getSecond().get(shape[i]);
            if (materials[i].getAmount() != 1) {
                checkAmount = true;
            }
        }
        return spawnShape(materials, checkAmount);
    }

    @Override
    public boolean isMatch(ItemStack[] items) {
        if (items == null) return false;
        Pair<String, Map<String, ItemStack>> stringMapPair = spawnShape(items, checkMaterialAmount);

        return this.shape.equals(stringMapPair.getFirst()) &&
                this.idMaterialMap.equals(stringMapPair.getSecond());
    }

    @Override
    public boolean takeMaterials(ItemStack[] items) {
        if (!isMatch(items)) return false;
        for (int i = 0; i < shapeIdArray.length; ++i) {
            if ("-1".equals(shapeIdArray[i])) continue;
            else if (unconsumeMaterialId.contains(shapeIdArray[i])) continue;
            items[i].setAmount(items[i].getAmount() - idMaterialMap.get(shapeIdArray[i]).getAmount());
        }
        return true;
    }

    @Override
    public Collection<ItemStack> getMaterials() {
        return idMaterialMap.values();
    }

    @Override
    public void load(ConfigurationSection section) {
        super.load(section);
        String shape = section.getString("shape");
        Map<String, ItemStack> materialMap = new HashMap<>();
        ConfigurationSection materialSection = section.getConfigurationSection("materials");
        for (String key : materialSection.getKeys(false)) {
            materialMap.put(key, materialSection.getItemStack(key));
        }
        setMaterials(shape, materialMap);
    }

    @Override
    public void store(ConfigurationSection section) {
        super.store(section);
        section.set("shape", shape);
        ConfigurationSection materialSection = section.createSection("materials");
        idMaterialMap.forEach(materialSection::set);
    }
}
