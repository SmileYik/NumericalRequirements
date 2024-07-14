package org.eu.smileyik.numericalrequirements.multiblockcraft.machine;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.eu.smileyik.numericalrequirements.core.api.NumericalRequirements;
import org.eu.smileyik.numericalrequirements.multiblockcraft.MultiBlockCraftExtension;
import org.eu.smileyik.numericalrequirements.multiblockcraft.SimpleItem;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.holder.SimpleCraftHolder;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.Recipe;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.impl.SimpleAbstractRecipe;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class SimpleCraftTable extends SimpleMachine {
    private final Creative creative;

    public SimpleCraftTable() {
        creative = new Creative(this);
    }

    public SimpleCraftTable(ConfigurationSection section) {
        super(section);
        creative = new Creative(this);
    }

    @Override
    public void open(Player player, String identifier) {
        SimpleCraftHolder holder = new SimpleCraftHolder();
        Inventory inv = NumericalRequirements.getPlugin().getServer().createInventory(holder, inventory.getSize(), title);
        inv.setContents(inventory.getContents());
        holder.setInventory(inv);
        holder.setMachine(this);
        holder.setIdentifier(identifier);
        player.openInventory(inv);
    }

    @Override
    public void createRecipe(Player player) {
        SimpleCraftHolder holder = new SimpleCraftHolder();
        Inventory inv = NumericalRequirements.getPlugin().getServer().createInventory(holder, inventory.getSize(), title);
        inv.setContents(inventory.getContents());
        holder.setInventory(inv);
        holder.setMachine(creative);
        player.openInventory(inv);
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        if (event.getClick() == ClickType.WINDOW_BORDER_LEFT || event.getClick() == ClickType.WINDOW_BORDER_RIGHT) return;
        int slot = event.getRawSlot();
        if (emptySlots.contains(slot)) return;
        boolean clickedInputs = inputSlots.contains(slot);
        boolean clickedOutputs = outputSlots.contains(slot);
        if (slot < inventory.getSize() && !clickedInputs && !emptySlots.contains(slot) && !clickedOutputs) {
            event.setCancelled(true);
            return;
        }

        if (clickedOutputs) {
            event.setCancelled(true);
            return;
        }

        if (clickedInputs) {
            MultiBlockCraftExtension.getInstance().getPlugin().getServer().getScheduler().runTask(
                    MultiBlockCraftExtension.getInstance().getPlugin(), () -> {
                        ItemStack[] inputs = new ItemStack[inputSlots.size()];
                        for (int i = inputSlots.size() - 1; i >= 0; i--) {
                            inputs[i] = event.getInventory().getItem(inputSlots.get(i));
                        }
                        Recipe recipe = findRecipe(inputs);
                        System.out.println(recipe);
                        if (recipe == null) {
                            event.getInventory().setItem(outputSlots.get(0), null);
                            return;
                        }
                        for (ItemStack output : recipe.getOutputs()) {
                            event.getInventory().setItem(outputSlots.get(0), output);
                            break;
                        }
                    }
            );
        }
    }

    @Override
    public void onDrag(InventoryDragEvent event) {
        event.setCancelled(true);
    }

    @Override
    public void onClose(InventoryCloseEvent event) {

    }

    private static class Creative extends SimpleMachine {
        private final SimpleCraftTable instance;

        private Creative(SimpleCraftTable instance) {
            this.instance = instance;
        }

        @Override
        public void onDrag(InventoryDragEvent event) {

        }

        @Override
        public void open(Player player, String identifier) {

        }

        @Override
        public void createRecipe(Player player) {

        }

        @Override
        public void onClick(InventoryClickEvent event) {

        }

        @Override
        public void onClose(InventoryCloseEvent event) {
            HumanEntity player = event.getPlayer();
            Inventory inv = event.getInventory();

            boolean inputsAllEmpty = true;
            SimpleItem[] inputs = new SimpleItem[instance.getInputSlots().size()];
            int idx = 0;
            for (Integer inputSlot : instance.getInputSlots()) {
                inputs[idx] = new SimpleItem(SimpleItem.TYPE_NREQ, inv.getItem(inputSlot));
                if (inputs[idx].getItemStack() != null) inputsAllEmpty = false;
                ++idx;
            }

            idx = 0;
            boolean outputsAllEmpty = true;
            SimpleItem[] outputs = new SimpleItem[instance.getOutputSlots().size()];
            for (Integer outputSlot : instance.getOutputSlots()) {
                outputs[idx] = new SimpleItem(SimpleItem.TYPE_NREQ, inv.getItem(outputSlot));
                if (outputs[idx].getItemStack() != null) outputsAllEmpty = false;
                ++idx;
            }

            if (inputsAllEmpty && outputsAllEmpty) return;

            Recipe recipe = new SimpleAbstractRecipe() {
                {
                    this.rawInputs = inputs;
                    this.rawOutputs = outputs;
                    this.id = UUID.randomUUID().toString();
                    this.name = this.id;
                }

                @Override
                public Collection<ItemStack> getInputs() {
                    return List.of();
                }

                @Override
                public Collection<ItemStack> getOutputs() {
                    return List.of();
                }

                @Override
                public boolean isMatch(ItemStack[] inputs) {
                    return false;
                }
            };
            File file = MultiBlockCraftExtension.getInstance().getMachineService().createRecipe(instance.getId(), recipe);
            player.sendMessage("Save to: " + file);
        }
    }
}
