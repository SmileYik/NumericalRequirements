package org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.eu.smileyik.numericalrequirements.core.api.item.tag.lore.LoreValue;
import org.eu.smileyik.numericalrequirements.multiblockcraft.MultiBlockCraftExtension;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.event.RecipeFormatItemEvent;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.event.RecipeTakeItemEvent;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.tag.DurabilityLore;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.tag.DurabilityNBT;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.tag.NotConsumableInputLore;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.tag.NotConsumableInputNBT;
import org.eu.smileyik.numericalrequirements.nms.nbtitem.NBTItem;
import org.eu.smileyik.numericalrequirements.nms.nbtitem.NBTItemHelper;

import java.util.*;

public class RecipeToolListener implements Listener {
    private final DurabilityLore durabilityLore;
    private final NotConsumableInputLore notConsumableInputLore;
    private final DurabilityNBT durabilityNBT;
    private final NotConsumableInputNBT notConsumableInputNBT;
    private final Map<Long, Set<ItemStack>> checkingRecipeTakeItem = new HashMap<>();

    public RecipeToolListener(MultiBlockCraftExtension extension) {
        this.durabilityLore = new DurabilityLore();
        this.notConsumableInputLore = new NotConsumableInputLore();
        this.durabilityNBT = new DurabilityNBT();
        this.notConsumableInputNBT = new NotConsumableInputNBT();

        extension.getApi().getItemService().registerItemTag(this.durabilityLore);
        extension.getApi().getItemService().registerItemTag(this.notConsumableInputLore);
        extension.getApi().getItemService().registerItemTag(this.durabilityNBT);
        extension.getApi().getItemService().registerItemTag(this.notConsumableInputNBT);

        extension.getPlugin().getServer().getPluginManager().registerEvents(this, extension.getPlugin());
    }


    @EventHandler(ignoreCancelled = true)
    public synchronized void onRecipeTakeItem(RecipeTakeItemEvent event) {
        boolean latest = event.getIndex() == event.getMax();

        ItemStack item = event.getItem();
        if (item == null && latest) {
            checkingRecipeTakeItem.remove(event.getId());
            return;
        }

        if (!checkingRecipeTakeItem.containsKey(event.getId())) {
            checkingRecipeTakeItem.put(event.getId(), new HashSet<>());
        }

        // nbt first
        Boolean notConsumable = notConsumableInputNBT.getValue(item);
        if (notConsumable != null && notConsumable) {
            event.setCancelled(true);
            if (latest) checkingRecipeTakeItem.remove(event.getId());
            return;
        }
        {
            Integer value = durabilityNBT.getValue(item);
            if (value != null) {
                ItemStack clone = item.clone();
                durabilityNBT.clearDisplayLore(clone);
                durabilityNBT.setValue(clone, 0);
                if (!checkingRecipeTakeItem.get(event.getId()).contains(clone)) {
                    value--;
                    if (value <= 0) {
                        item.setAmount(0);
                    } else {
                        durabilityNBT.setValue(item, value);
                        durabilityNBT.refreshDisplayLore(item);
                        event.setItem(item);
                    }
                    checkingRecipeTakeItem.get(event.getId()).add(clone);
                }
                event.setCancelled(true);
                if (latest) checkingRecipeTakeItem.remove(event.getId());
                return;
            }
        }

        // lore
        if (!item.hasItemMeta()) return;
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasLore()) return;
        List<String> lore = meta.getLore();
        for (int i = lore.size() - 1; i >= 0; i--) {
            String line = lore.get(i);
            if (notConsumableInputLore.matches(line)) {
                event.setCancelled(true);
                break;
            } else if (durabilityLore.matches(line)) {
                ItemStack clone = item.clone();
                ItemMeta cloneMeta = clone.getItemMeta();
                ArrayList<String> cloneLore = new ArrayList<>(lore);
                cloneLore.remove(i);
                cloneMeta.setLore(cloneLore);
                clone.setItemMeta(cloneMeta);
                if (!checkingRecipeTakeItem.get(event.getId()).contains(clone)) {
                    checkingRecipeTakeItem.get(event.getId()).add(clone);
                    LoreValue value = durabilityLore.getValue(line);
                    int durability = value.get(0);
                    durability -= 1;
                    if (durability == 0) {
                        item.setAmount(0);
                    } else {
                        value.set(0, durability);
                        line = durabilityLore.buildLore(value);
                        lore.set(i, line);
                        meta.setLore(lore);
                        item.setItemMeta(meta);
                    }
                    event.setItem(item);
                }
                event.setCancelled(true);
                break;
            }
        }
        if (latest) checkingRecipeTakeItem.remove(event.getId());
    }

    @EventHandler
    public synchronized void onRecipeFormatItem(RecipeFormatItemEvent event) {
        // 若该物品含有耐久度标签，则移除此标签
        // 与此同时如果该物品堆叠数量不为1,则跳过格式化，
        // 使得堆叠含有耐久度标签的物品作为材料进行合成时，不会通过配方比对校验
        ItemStack itemStack = formatItem(event.getItem());
        if (itemStack != null) {
            event.setItem(itemStack);
        }
    }

    private ItemStack formatItem(ItemStack item) {
        if (item == null || item.getAmount() > 1) return null;

        // check nbt first
        if (durabilityNBT.getValue(item) != null) {
            durabilityNBT.clearDisplayLore(item);
            NBTItem nbtItem = NBTItemHelper.cast(item);
            nbtItem.getTag().remove(durabilityNBT.getId());
            return nbtItem.getItemStack();
        }

        // check lore after
        if (!item.hasItemMeta()) return null;
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasLore()) return null;
        List<String> lore = meta.getLore();
        for (int i = lore.size() - 1; i >= 0; i--) {
            if (durabilityLore.matches(lore.get(i))) {
                lore.remove(i);
                break;
            }
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
}
