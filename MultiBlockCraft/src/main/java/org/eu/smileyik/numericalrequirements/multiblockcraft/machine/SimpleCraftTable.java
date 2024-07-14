package org.eu.smileyik.numericalrequirements.multiblockcraft.machine;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
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
        SimpleCraftHolder holder = new Holder();
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
        if (event.getSlotType() == InventoryType.SlotType.OUTSIDE) return;
        if (event.getClick() == ClickType.WINDOW_BORDER_LEFT || event.getClick() == ClickType.WINDOW_BORDER_RIGHT) return;
        int slot = event.getRawSlot();
        if (emptySlots.contains(slot)) return;
        boolean clickedInputs = inputSlots.contains(slot);
        boolean clickedOutputs = outputSlots.contains(slot);
        if (slot < inventory.getSize() && !clickedInputs && !emptySlots.contains(slot) && !clickedOutputs) {
            event.setCancelled(true);
            return;
        }

        if (slot >= inventory.getSize() &&
                (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT)) {
            findRecipeAndDisplay(event.getInventory());
            return;
        }

        if (clickedOutputs) {
            Inventory inv = event.getInventory();
            ItemStack item = inv.getItem(slot);
            ItemStack itemOnCursor = event.getWhoClicked().getItemOnCursor();

            if (item == null || itemOnCursor != null && itemOnCursor.getType() != Material.AIR) {
                event.setCancelled(true);
                return;
            }

            Holder holder = (Holder) inv.getHolder();
            if (holder.isCrafted()) {
                runTask(() -> {
                    boolean flag = true;
                    for (int i : outputSlots) {
                        ItemStack stack = inv.getItem(i);
                        if (stack != null && stack.getType() != Material.AIR) {
                            flag = false;
                            break;
                        }
                    }
                    holder.setCrafted(!flag);

                    if (!holder.isCrafted()) {
                        ItemStack[] inputs = copyArray(inv, inputSlots);
                        Recipe recipe = findRecipe(inputs);
                        if (recipe == null) {
                            clearOutput(inv);
                            return;
                        }
                        displayOutput(inv, recipe);
                    }
                });
                return;
            }

            ItemStack[] inputs = copyArray(inv, inputSlots);
            Recipe recipe = findRecipe(inputs);
            if (recipe == null) {
                clearOutput(inv);
                event.setCancelled(true);
                return;
            }
            holder.setCrafted(true);

            int times = 0;
            if (event.getClick() == ClickType.RIGHT || event.getClick() == ClickType.SHIFT_RIGHT) {
                int max = 1;
                for (ItemStack output : recipe.getOutputs()) {
                    max = Math.max(max, output.getAmount());
                }

                do {
                    recipe.takeInputs(inputs);
                    times++;
                } while (max * times < 64 && recipe.isMatch(inputs));
            } else {
                recipe.takeInputs(inputs);
                times = 1;
            }

            int idx = 0, size = outputSlots.size();
            for (ItemStack output : recipe.getOutputs()) {
                if (idx == size) break;
                if (output != null) {
                    output = output.clone();
                    output.setAmount(output.getAmount() * times);
                }
                inv.setItem(outputSlots.get(idx++), output);
            }

            if (event.getClick() == ClickType.RIGHT) {
                event.setCancelled(true);
                event.getWhoClicked().setItemOnCursor(inv.getItem(slot));
                inv.setItem(slot, null);
            }

            if (size == 1) {
                holder.setCrafted(false);
                runTask(() -> {
                    if (recipe.isMatch(inputs)) displayOutput(inv, recipe);
                });
            }
            return;
        }

        if (clickedInputs) {
            findRecipeAndDisplay(event.getInventory());
            return;
        }
    }

    @Override
    public void onDrag(InventoryDragEvent event) {
        for (Integer rawSlot : event.getRawSlots()) {
            if (emptySlots.contains(rawSlot)) continue;
            if (inputSlots.contains(rawSlot)) continue;
            if (rawSlot >= inventory.getSize()) continue;
            event.setCancelled(true);
            return;
        }
        findRecipeAndDisplay(event.getInventory());
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        Inventory inv = event.getInventory();
        HumanEntity player = event.getPlayer();
        for (Integer slot : inputSlots) {
            ItemStack item = inv.getItem(slot);
            if (item == null) continue;
            player.getInventory().addItem(item).forEach((k, v) -> {
                player.getWorld().dropItem(player.getLocation(), v);
            });
        }

        if (((Holder)inv.getHolder()).isCrafted()) {
            for (Integer slot : outputSlots) {
                ItemStack item = inv.getItem(slot);
                if (item == null) continue;
                player.getInventory().addItem(item).forEach((k, v) -> {
                    player.getWorld().dropItem(player.getLocation(), v);
                });
            }
        }
    }

    private void findRecipeAndDisplay(Inventory inv) {
        if (((Holder)inv.getHolder()).isCrafted()) return;
        runTask(() -> {
            ItemStack[] inputs = copyArray(inv, inputSlots);
            Recipe recipe = findRecipe(inputs);
            if (recipe == null) {
                clearOutput(inv);
                return;
            }
            displayOutput(inv, recipe);
        });
    }

    private static class Holder extends SimpleCraftHolder {
        private boolean crafted = false;

        public boolean isCrafted() {
            return crafted;
        }

        public void setCrafted(boolean crafted) {
            this.crafted = crafted;
        }
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
