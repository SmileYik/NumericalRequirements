package org.eu.smileyik.numericalrequirements.multiblockcraft.machine.impl;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.eu.smileyik.numericalrequirements.core.api.NumericalRequirements;
import org.eu.smileyik.numericalrequirements.multiblockcraft.MultiBlockCraftExtension;
import org.eu.smileyik.numericalrequirements.multiblockcraft.data.impl.SimpleStorableMachineData;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.Machine;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.Recipe;

public class SimpleStorableCraftTable extends SimpleCraftTable {

    public SimpleStorableCraftTable() {
    }

    public SimpleStorableCraftTable(ConfigurationSection section) {
        super(section);
    }

    @Override
    public void open(Player player, String identifier) {
        Holder holder = new Holder();
        Inventory inv = NumericalRequirements.getPlugin().getServer().createInventory(holder, inventory.getSize(), title);
        inv.setContents(inventory.getContents());
        holder.setInventory(inv);
        holder.setMachine(this);
        holder.setIdentifier(identifier);
        Data machineData = (Data) MultiBlockCraftExtension.getInstance().getMachineService().getMachineDataService().loadMachineData(identifier);
        if (machineData == null) {
            machineData = new Data(this);
            machineData.setIdentifier(identifier);
        } else {
            machineData.forEach(inv::setItem);
            holder.setCrafted(machineData.isCrafted());

            if (!holder.isCrafted()) {
                ItemStack[] inputs = copyArray(inv, inputSlots);
                Recipe recipe = findRecipe(inputs);
                if (recipe != null) {
                    displayOutput(inv, recipe);
                }
            }
        }
        holder.setMachineData(machineData);
        player.openInventory(inv);
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        Inventory inv = event.getInventory();
        Holder holder = (Holder) inv.getHolder();
        Data data = (Data) holder.getMachineData();

        for (Integer slot : inputSlots) {
            data.setItem(slot, inv.getItem(slot));
        }

        for (Integer slot : emptySlots) {
            data.setItem(slot, inv.getItem(slot));
        }

        data.setCrafted(holder.isCrafted());
        if (holder.isCrafted()) {
            for (Integer slot : outputSlots) {
                data.setItem(slot, inv.getItem(slot));
            }
        } else {
            for (Integer slot : outputSlots) {
                data.setItem(slot, null);
            }
        }
        MultiBlockCraftExtension.getInstance().getMachineService().getMachineDataService().storeMachineData(data);
    }

    public static class Data extends SimpleStorableMachineData {
        private boolean crafted;


        public Data(Machine machine) {
            super(machine);
        }

        public boolean isCrafted() {
            return crafted;
        }

        public void setCrafted(boolean crafted) {
            this.crafted = crafted;
        }

        @Override
        public synchronized void load(ConfigurationSection section) {
            super.load(section);
            this.crafted = section.getBoolean("crafted");
        }

        @Override
        public synchronized void store(ConfigurationSection section) {
            super.store(section);
            section.set("crafted", this.crafted);
        }
    }
}
