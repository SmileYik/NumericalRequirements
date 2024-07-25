package org.eu.smileyik.numericalrequirements.multiblockcraft.machine.impl;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.eu.smileyik.numericalrequirements.core.api.NumericalRequirements;
import org.eu.smileyik.numericalrequirements.debug.DebugLogger;
import org.eu.smileyik.numericalrequirements.multiblockcraft.MultiBlockCraftExtension;
import org.eu.smileyik.numericalrequirements.multiblockcraft.SimpleItem;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.Machine;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.item.InvItem;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.Recipe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class YamlMachine implements Machine {
    protected String id;
    protected String name;
    protected String title;

    protected SimpleItem machineItem;
    protected List<Integer> inputSlots;
    protected List<Integer> outputSlots;
    protected List<Integer> emptySlots;

    protected Inventory inventory;
    protected final Map<Integer, InvItem> funcItems = new HashMap<>();

    public YamlMachine() {

    }

    public YamlMachine(ConfigurationSection section) {
        id = section.getString("id");
        name = section.getString("name");
        title = section.getString("title");

        machineItem = SimpleItem.load(section.getConfigurationSection("machine-item"));
        inputSlots = section.getIntegerList("input-slots");
        outputSlots = section.getIntegerList("output-slots");
        emptySlots = section.getIntegerList("empty-slots");
        inventory = NumericalRequirements.getPlugin().getServer().createInventory(null, section.getInt("inv-size"));

        if (section.isConfigurationSection("other-slots")) {
            SimpleItem item = SimpleItem.load(section.getConfigurationSection("other-slots"));
            ItemStack itemStack = item.getItemStack();
            for (int i = inventory.getSize() - 1; i >= 0; i--) {
                if (outputSlots.contains(i) || emptySlots.contains(i) || inputSlots.contains(i)) {
                    continue;
                }
                inventory.setItem(i, itemStack);
            }
        }

        ConfigurationSection inv = section.getConfigurationSection("inv-items");
        for (String key : inv.getKeys(false)) {
            int i = Integer.parseInt(key);
            SimpleItem item = SimpleItem.load(inv.getConfigurationSection(key));
            ItemStack itemStack = item.getItemStack();
//            if (itemStack != null) {
//                ItemMeta itemMeta = itemStack.getItemMeta();
//                List<String> lore = itemMeta.hasLore() ? itemMeta.getLore() : new ArrayList<>();
//                lore.add("§7 ");
//                itemMeta.setLore(lore);
//                itemStack.setItemMeta(itemMeta);
//            }
            inventory.setItem(i, itemStack);
        }

        ConfigurationSection funcInv = section.getConfigurationSection("func-items");
        if (funcInv == null) funcInv = new YamlConfiguration();
        for (String key : funcInv.getKeys(false)) {
            int i = Integer.parseInt(key);
            ConfigurationSection c = funcInv.getConfigurationSection(key);
            String type = c.getString("type");
            try {
                InvItem item = (InvItem) Class.forName(type).getDeclaredConstructor().newInstance();
                item.load(c);
                if (inventory.getItem(i) != null && inventory.getItem(i).getType() != Material.AIR) {
                    funcItems.put(i, item);
                }
            } catch (Exception e) {
                DebugLogger.debug(e);
            }
        }
    }

    @Override
    public Inventory createGui() {
        return inventory;
    }

    @Override
    public List<Integer> getInputSlots() {
        return inputSlots;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<Integer> getEmptySlots() {
        return emptySlots;
    }

    @Override
    public List<Integer> getOutputSlots() {
        return outputSlots;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public ItemStack getMachineItem() {
        return machineItem.getItemStack().clone();
    }

    @Override
    public Map<Integer, InvItem> getFuncItems() {
        return funcItems;
    }

    protected ItemStack[] copyArray(Inventory inventory, List<Integer> slots) {
        ItemStack[] items = new ItemStack[slots.size()];
        for (int i = slots.size() - 1; i >= 0; i--) {
            items[i] = inventory.getItem(slots.get(i));
        }
        return items;
    }

    protected void runTask(Runnable runnable) {
        MultiBlockCraftExtension.getInstance().getPlugin().getServer().getScheduler().runTask(
                MultiBlockCraftExtension.getInstance().getPlugin(), runnable
        );
    }

    protected void displayOutput(Inventory inventory, Recipe recipe) {
        int idx = 0, size = outputSlots.size();
        for (ItemStack itemStack : recipe.getDisplayedOutput()) {
            if (idx == size) break;
            inventory.setItem(outputSlots.get(idx++), itemStack);
        }
    }

    protected void clearOutput(Inventory inventory) {
        for (int i : outputSlots) {
            inventory.setItem(i, null);
        }
    }
}
