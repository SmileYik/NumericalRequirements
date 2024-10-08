package org.eu.smileyik.numericalrequirements.multiblockcraft.machine.impl;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.eu.smileyik.numericalrequirements.core.api.NumericalRequirements;
import org.eu.smileyik.numericalrequirements.debug.DebugLogger;
import org.eu.smileyik.numericalrequirements.multiblockcraft.MultiBlockCraftExtension;
import org.eu.smileyik.numericalrequirements.multiblockcraft.SimpleItem;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.event.FinishedCraftEvent;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.event.OpenMachineGuiEvent;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.holder.SimpleCraftHolder;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.Recipe;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.impl.SimpleAbstractRecipe;
import org.eu.smileyik.numericalrequirements.nms.NMS;

import java.io.File;
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
        OpenMachineGuiEvent event = new OpenMachineGuiEvent(this, player, identifier);
        MultiBlockCraftExtension.getInstance().getPlugin().getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }

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
        Inventory inv = event.getInventory();
        Holder holder = (Holder) inv.getHolder();

        if (emptySlots.contains(slot)) return;
        boolean clickedInputs = inputSlots.contains(slot);
        boolean clickedOutputs = outputSlots.contains(slot);
        if (slot < inventory.getSize() && !clickedInputs && !emptySlots.contains(slot) && !clickedOutputs) {
            isClickedButton(slot, inv, holder.getMachineData());
            event.setCancelled(true);
            return;
        }

        if (slot >= inventory.getSize() &&
                (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT)) {
            findRecipeAndDisplay(inv);
            return;
        }

        if (clickedOutputs) {
            ItemStack item = inv.getItem(slot);
            ItemStack itemOnCursor = event.getWhoClicked().getItemOnCursor();

            if (item == null || itemOnCursor != null && itemOnCursor.getType() != Material.AIR) {
                event.setCancelled(true);
                return;
            }

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

            // 计算合成数量，右击合成最大数量，其余合成1次。
            int times = 0;
            if (event.getClick() == ClickType.RIGHT || event.getClick() == ClickType.SHIFT_RIGHT) {
                int max = 1;
                for (ItemStack output : recipe.getOutputs()) {
                    max = Math.max(max, output.getAmount());
                }

                do {
                    recipe.takeInputs(inputs);
                    if (NMS.MIDDLE_VERSION == 7) {
                        int idx = 0;
                        for (int islot : inputSlots) {
                            inv.setItem(islot, inputs[idx++]);
                        }
                    }
                    times++;
                } while (max * times < 64 && recipe.isMatch(inputs));
            } else {
                recipe.takeInputs(inputs);
                if (NMS.MIDDLE_VERSION == 7) {
                    int idx = 0;
                    for (int islot : inputSlots) {
                        inv.setItem(islot, inputs[idx++]);
                    }
                }
                times = 1;
            }

            int idx = 0, size = outputSlots.size();

            // call finished craft event
            FinishedCraftEvent finishedCraftEvent = new FinishedCraftEvent(this, holder.getIdentifier(), holder.getMachineData(), recipe, recipe.getOutputs());
            MultiBlockCraftExtension.getInstance().getPlugin().getServer().getPluginManager().callEvent(finishedCraftEvent);

            for (ItemStack output : finishedCraftEvent.getOutputs()) {
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
            } else {
                runTask(() -> {
                    boolean flag = true;
                    for (int i : outputSlots) {
                        ItemStack stack = inv.getItem(i);
                        DebugLogger.debug("check craft status: %s", stack);
                        if (stack != null && stack.getType() != Material.AIR) {
                            flag = false;
                            break;
                        }
                    }
                    if (flag) {
                        holder.setCrafted(false);
                        if (recipe.isMatch(inputs)) displayOutput(inv, recipe);
                    }
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

    public static class Holder extends SimpleCraftHolder {
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
            };
            File file = MultiBlockCraftExtension.getInstance().getMachineService().createRecipe(instance.getId(), recipe);
            player.sendMessage("Save to: " + file);
        }
    }
}
