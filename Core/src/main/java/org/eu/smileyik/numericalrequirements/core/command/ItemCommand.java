package org.eu.smileyik.numericalrequirements.core.command;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.eu.smileyik.numericalrequirements.core.I18N;
import org.eu.smileyik.numericalrequirements.core.api.NumericalRequirements;
import org.eu.smileyik.numericalrequirements.core.api.item.ItemService;
import org.eu.smileyik.numericalrequirements.core.api.item.tag.ItemTag;
import org.eu.smileyik.numericalrequirements.core.api.item.tag.lore.LoreTag;
import org.eu.smileyik.numericalrequirements.core.api.item.tag.lore.LoreValue;
import org.eu.smileyik.numericalrequirements.core.api.item.tag.lore.MergeableLore;
import org.eu.smileyik.numericalrequirements.core.api.item.tag.nbt.DisplayableNBTTag;
import org.eu.smileyik.numericalrequirements.core.api.item.tag.nbt.NBTTag;
import org.eu.smileyik.numericalrequirements.core.api.util.Pair;
import org.eu.smileyik.numericalrequirements.core.command.annotation.Command;
import org.eu.smileyik.numericalrequirements.core.command.annotation.CommandI18N;

import java.util.*;

@CommandI18N("command")
@Command(
        value = "item",
        colorCode = "color",
        parentCommand = "NumericalRequirements",
        permission = "NumericalRequirements.Admin"
)
public class ItemCommand {
    private final NumericalRequirements api = NumericalRequirements.getInstance();
    private final ItemService itemService = api.getItemService();

    @CommandI18N("command.item")
    @Command(
            value = "add",
            colorCode = "color",
            args = {"tag", "values"},
            isUnlimitedArgs = true,
            needPlayer = true
    )
    public void addTagToItem(Player player, String[] args) {
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType() == Material.AIR) {
            player.sendMessage(I18N.trp("command", "command.item.error.no-item-in-hand"));
            return;
        }
        if (args.length == 0) {
            player.sendMessage(I18N.trp("command", "command.item.error.no-tag-id"));
            return;
        }
        ItemTag<?> itemTag = itemService.getItemTagById(args[0]);
        if (itemTag == null) {
            player.sendMessage(I18N.trp("command", "command.item.error.not-valid-tag", args[0]));
            return;
        }

        List<String> list = args.length == 1 ? Collections.emptyList() : Arrays.asList(Arrays.copyOfRange(args, 1, args.length));
        if (itemTag instanceof NBTTag) {
            NBTTag<?> nbtTag = (NBTTag<?>) itemTag;
            if (nbtTag.isValidValue(list)) {
                nbtTag.setValue(item, list);
                if (nbtTag instanceof DisplayableNBTTag) {
                    ((DisplayableNBTTag<?>) nbtTag).refreshDisplayLore(item);
                }
            } else {
                player.sendMessage(I18N.trp("command", "command.item.error.wrong-tag-value"));
            }
            return;
        }
        LoreTag tag = (LoreTag) itemTag;
        if (!tag.isValidValues(list)) {
            player.sendMessage(I18N.trp("command", "command.item.error.wrong-tag-value"));
            return;
        }

        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();
        if (lore == null) {
            lore = new ArrayList<>();
        }
        int size = lore.size();
        boolean added = false;
        if (tag instanceof MergeableLore) {
            for (int i = 0; i < size; ++i) {
                String line = lore.get(i);
                Pair<LoreTag, LoreValue> pair1 = itemService.analyzeLore(line, ItemService.TAG_TYPE_ALL_LORE);
                if (pair1 == null || pair1.getFirst() != tag) {
                    continue;
                }
                Pair<LoreTag, LoreValue> pair2 =
                        itemService.analyzeLore(tag.buildLore(list), ItemService.TAG_TYPE_ALL_LORE);
                if (pair2 == null) {
                    continue;
                }
                added = true;
                ((MergeableLore) tag).merge(pair1.getSecond(), pair2.getSecond());
                String replaceLore = tag.buildLore(pair1.getSecond());
                lore.set(i, replaceLore);
                break;
            }
        }

        if (!added) {
            lore.add(tag.buildLore(list));
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        player.getInventory().setItemInMainHand(item);
    }

    @CommandI18N("command.item")
    @Command(
            value = "remove",
            colorCode = "color",
            args = {"tag"},
            isUnlimitedArgs = false,
            needPlayer = true
    )
    public void removeTagFromItem(Player player, String[] args) {
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType() == Material.AIR) {
            player.sendMessage(I18N.trp("command", "command.item.error.no-item-in-hand"));
            return;
        }
        LoreTag tag = (LoreTag) itemService.getItemTagById(args[0]);
        if (tag == null) {
            player.sendMessage(I18N.trp("command", "command.item.error.not-valid-tag", args[0]));
            return;
        }
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();
        if (lore == null) {
            return;
        }
        int size = lore.size();
        for (int i = 0; i < size; ++i) {
            if (!tag.matches(lore.get(i))) continue;
            lore.remove(i);
            break;
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        player.getInventory().setItemInMainHand(item);
    }

    @CommandI18N("command.item")
    @Command(
            value = "merge",
            colorCode = "color",
            isUnlimitedArgs = false,
            needPlayer = true
    )
    public void mergeLore(Player player, String[] args) {
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType() == Material.AIR) {
            player.sendMessage(I18N.trp("command", "command.item.error.no-item-in-hand"));
            return;
        }
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();
        if (lore == null) {
            return;
        }
        Map<LoreTag, List<LoreValue>> result = itemService.analyzeLore(lore, ItemService.TAG_TYPE_ALL_LORE);
        List<String> newLore = new ArrayList<>();
        for (String line : lore) {
            Pair<LoreTag, LoreValue> pair = itemService.analyzeLore(line, ItemService.TAG_TYPE_ALL_LORE);
            if (pair == null) {
                newLore.add(line);
                continue;
            }
            LoreTag tag = pair.getFirst();
            if (result.containsKey(pair.getFirst())) {
                List<LoreValue> loreValues = result.get(tag);
                newLore.add(tag.buildLore(loreValues.remove(0)));
                if (loreValues.isEmpty()) {
                    result.remove(tag);
                }
            }
        }
        meta.setLore(newLore);
        item.setItemMeta(meta);
        player.getInventory().setItemInMainHand(item);
    }

    @CommandI18N("command.item")
    @Command(
            value = "reload",
            colorCode = "color",
            isUnlimitedArgs = false,
            needPlayer = false
    )
    public void reload(CommandSender sender, String[] args) {
        NumericalRequirements.getInstance().getItemService().reloadItems();
        sender.sendMessage(I18N.trp("command", "command.item.reload.reloaded"));
    }

    @CommandI18N("command.item")
    @Command(
            value = "get",
            colorCode = "color",
            args = {"item-id", "item-amount"},
            isUnlimitedArgs = false,
            needPlayer = true
    )
    public void getItem(Player sender, String[] args) {
        int amount;
        try {
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(I18N.trp("command", "command.item.error.not-number", args[1]));
            return;
        }
        ItemStack itemStack = NumericalRequirements.getInstance().getItemService().loadItem(args[0], amount);
        if (itemStack == null) {
            sender.sendMessage(I18N.trp("command", "command.item.error.item-not-found", args[0]));
            return;
        }
        String itemName = itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName() ? itemStack.getItemMeta().getDisplayName() : itemStack.getType().name();
        HashMap<Integer, ItemStack> map = sender.getInventory().addItem(itemStack);
        if (map != null && !map.isEmpty()) {
            sender.sendMessage(I18N.tr("command.item.info.full-inv", itemName, amount));
            map.forEach((k, v) -> sender.getWorld().dropItem(sender.getLocation(), v));
        }
        sender.sendMessage(I18N.tr("command.item.info.inform-sender", sender.getName(), itemName, amount));
    }

    @CommandI18N("command.item")
    @Command(
            value = "give",
            colorCode = "color",
            args = {"player", "item-id", "item-amount"},
            isUnlimitedArgs = false,
            needPlayer = false
    )
    public void giveItem(CommandSender sender, String[] args) {
        Player player = NumericalRequirements.getPlugin().getServer().getPlayer(args[0]);
        if (player == null) {
            sender.sendMessage(I18N.trp("command", "command.item.error.player-not-online", args[0]));
            return;
        }
        int amount;
        try {
            amount = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(I18N.trp("command", "command.item.error.not-number", args[2]));
            return;
        }
        ItemStack itemStack = NumericalRequirements.getInstance().getItemService().loadItem(args[1], amount);
        if (itemStack == null) {
            sender.sendMessage(I18N.trp("command", "command.item.error.item-not-found", args[1]));
            return;
        }
        String itemName = itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName() ? itemStack.getItemMeta().getDisplayName() : itemStack.getType().name();
        player.sendMessage(I18N.tr("command.item.info.inform-target", sender.getName(), itemName, amount));
        HashMap<Integer, ItemStack> map = player.getInventory().addItem(itemStack);
        if (map != null && !map.isEmpty()) {
            player.sendMessage(I18N.tr("command.item.info.full-inv", itemName, amount));
            map.forEach((k, v) -> player.getWorld().dropItem(player.getLocation(), v));
        }
        player.sendMessage(I18N.tr("command.item.info.inform-sender", player.getName(), itemName, amount));
    }

    @CommandI18N("command.item")
    @Command(
            value = "store",
            colorCode = "color",
            args = {"item-id"},
            isUnlimitedArgs = false,
            needPlayer = true
    )
    public void storeItem(Player sender, String[] args) {
        ItemStack itemInMainHand = sender.getInventory().getItemInMainHand();
        if (itemInMainHand == null || itemInMainHand.getType() == Material.AIR) {
            sender.sendMessage(I18N.trp("command", "command.item.error.no-item-in-hand"));
            return;
        }
        NumericalRequirements.getInstance().getItemService().storeItem(args[0], itemInMainHand);
        sender.sendMessage(I18N.trp("command", "command.item.store.success", args[0]));
    }
}
