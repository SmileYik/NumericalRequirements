package org.eu.smileyik.numericalrequirements.core.command;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.eu.smileyik.numericalrequirements.core.I18N;
import org.eu.smileyik.numericalrequirements.core.api.NumericalRequirements;
import org.eu.smileyik.numericalrequirements.core.command.annotation.Command;
import org.eu.smileyik.numericalrequirements.core.command.annotation.CommandI18N;
import org.eu.smileyik.numericalrequirements.core.item.ItemService;
import org.eu.smileyik.numericalrequirements.core.item.ItemTag;
import org.eu.smileyik.numericalrequirements.core.item.tagold.service.LoreTagValue;
import org.eu.smileyik.numericalrequirements.core.util.Pair;

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
        ItemTag tag = itemService.getItemTagById(args[0]);
        if (tag == null) {
            player.sendMessage(I18N.trp("command", "command.item.error.not-valid-tag", args[0]));
            return;
        }
        List<String> list = args.length == 1 ? Collections.emptyList() : Arrays.asList(Arrays.copyOfRange(args, 1, args.length));
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
        if (tag.canMerge()) {
            for (int i = 0; i < size; ++i) {
                String line = lore.get(i);
                Pair<ItemTag, LoreTagValue> pair1 = itemService.analyzeLore(line, ItemService.TAG_ALL);
                if (pair1 == null || pair1.getFirst() != tag) {
                    continue;
                }
                Pair<ItemTag, LoreTagValue> pair2 =
                        itemService.analyzeLore(tag.getPattern().buildLoreByStringList(list), ItemService.TAG_ALL);
                if (pair2 == null) {
                    continue;
                }
                added = true;
                String replaceLore = tag.getPattern().buildLore(
                        pair1.getSecond().merge(pair2.getSecond(), tag)
                );
                lore.set(i, replaceLore);
                break;
            }
        }

        if (!added) {
            lore.add(tag.getPattern().buildLoreByStringList(list));
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
        ItemTag tag = itemService.getItemTagById(args[0]);
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
            if (!itemService.matches(tag, lore.get(i))) continue;
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
        Map<ItemTag, List<LoreTagValue>> result = itemService.analyzeLoreList(lore, ItemService.TAG_ALL);
        lore.clear();
        result.forEach((tag, value) -> {
            value.forEach(it -> {
                lore.add(tag.getPattern().buildLore(it));
            });
        });
        meta.setLore(lore);
        item.setItemMeta(meta);
        player.getInventory().setItemInMainHand(item);
    }
}
