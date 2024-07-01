package org.eu.smileyik.numericalrequirements.core.item;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.eu.smileyik.numericalrequirements.core.NumericalRequirements;
import org.eu.smileyik.numericalrequirements.core.item.tag.service.*;
import org.eu.smileyik.numericalrequirements.core.player.NumericalPlayer;
import org.eu.smileyik.numericalrequirements.core.util.Pair;

import java.util.*;

public class ItemServiceImpl implements Listener, ItemService {
    private final NumericalRequirements plugin;
    private final Map<String, ItemTag> idTagMap = new HashMap<>();
    private final Collection<ItemTag> normalItemTags = new ArrayList<>();
    private final Collection<ItemTag> consumeItemTags = new ArrayList<>();
    private final Map<ItemTag, LoreTagPattern> noColorTagMap = new HashMap<>();
    private final LoreTagService loreTagService;

    public ItemServiceImpl(NumericalRequirements plugin) {
        this.plugin = plugin;
        loreTagService = new SimpleLoreTagService();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public LoreTagService getLoreTagService() {
        return loreTagService;
    }

    @Override
    public void registerItemTag(ItemTag tag) {
        String id = tag.getTagId().toLowerCase();
        if (idTagMap.containsKey(id)) {
            // throw error
            return;
        }
        idTagMap.put(id, tag);
        if (tag instanceof ConsumeItemTag) {
            consumeItemTags.add(tag);
        } else {
            normalItemTags.add(tag);
        }
        noColorTagMap.put(tag, loreTagService.compile(ChatColor.stripColor(tag.getPattern().getModeString())));
    }

    @Override
    public ItemTag getItemTagById(String id) {
        return idTagMap.get(id.toLowerCase());
    }

    @Override
    public Map<ItemTag, List<LoreTagValue>> analyzeLoreList(List<String> loreList, byte tagType) {
        Collection<ItemTag> tags = getTagsByTagType(tagType);
        Map<ItemTag, List<LoreTagValue>> result = new LinkedHashMap<>();
        for (String lore : loreList) {
            for (ItemTag tag : tags) {
                LoreTagValue value = noColorTagMap.get(tag).getValue(ChatColor.stripColor(lore));
                if (value == null) {
                    continue;
                }
                if (result.containsKey(tag)) {
                    List<LoreTagValue> values = result.get(tag);
                    if (tag.canMerge()) {
                        values.add(values.remove(0).merge(value, tag));
                    } else {
                        values.add(value);
                    }
                } else {
                    List<LoreTagValue> values = new ArrayList<>();
                    values.add(value);
                    result.put(tag, values);
                }
            }
        }
        return result;
    }

    private Collection<ItemTag> getTagsByTagType(byte tagType) {
        Collection<ItemTag> tags = null;
        if (tagType == TAG_ALL) {
            tags = idTagMap.values();
        } else if (tagType == TAG_CONSUME) {
            tags = consumeItemTags;
        } else if (tagType == TAG_UNCONSUME) {
            tags = normalItemTags;
        } else {
            tags = Collections.emptyList();
        }
        return tags;
    }

    @Override
    public Pair<ItemTag, LoreTagValue> analyzeLore(String lore, byte tagType) {
        Collection<ItemTag> tags = getTagsByTagType(tagType);
        for (ItemTag tag : tags) {
            LoreTagValue valueList = noColorTagMap.get(tag).getValue(ChatColor.stripColor(lore));
            if (valueList == null) {
                continue;
            }
            return Pair.newUnchangablePair(tag, valueList);
        }
        return null;
    }

    @Override
    public boolean matches(ItemTag tag, String lore) {
        return tag != null && noColorTagMap.containsKey(tag) && noColorTagMap.get(tag).matches(ChatColor.stripColor(lore));
    }

    private boolean useItem(NumericalPlayer player, ItemStack item) {
        if (!item.hasItemMeta() || !item.getItemMeta().hasLore()) {
            return false;
        }
        List<String> lore = item.getItemMeta().getLore();
        Map<ItemTag, List<LoreTagValue>> consumeItemTagListMap = analyzeLoreList(lore, TAG_CONSUME);
        if (consumeItemTagListMap.isEmpty()) {
            return false;
        }
        consumeItemTagListMap.forEach((key, value) -> {
            ((ConsumeItemTag) key).handlePlayer(player, value);
        });
        return true;
    }

    @Override
    public void shutdown() {
        loreTagService.shutdown();
        idTagMap.clear();
        normalItemTags.clear();
        consumeItemTags.clear();
        noColorTagMap.clear();
    }

    @EventHandler
    public void onPlayerInteractItem(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        NumericalPlayer numericalPlayer = plugin.getPlayerService().getNumericalPlayer(player);
        if (numericalPlayer == null) return;

        if (!event.hasItem() ||
                event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        ItemStack item = event.getItem();
        if (item.getType().isEdible() || item.getType() == Material.POTION) return;
        if (useItem(numericalPlayer, item)) {
            event.setCancelled(true);
            item.setAmount(item.getAmount() - 1);
        }
    }

    @EventHandler
    public void onPlayerConsumeItem(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        NumericalPlayer numericalPlayer = plugin.getPlayerService().getNumericalPlayer(player);
        if (numericalPlayer == null) return;

        ItemStack item = event.getItem();
        boolean potion = item.getType() == Material.POTION;
        if (useItem(numericalPlayer, item)) {
            event.setCancelled(true);
            PlayerInventory inventory = player.getInventory();
            boolean mainHand = inventory.getItemInMainHand().isSimilar(item);
            if (item.getAmount() == 1) {
                item = null;
            } else {
                item.setAmount(item.getAmount() - 1);
            }
            if (mainHand) {
                inventory.setItemInMainHand(item);
            } else {
                inventory.setItemInOffHand(item);
            }
            if (potion) {
                inventory.addItem(new ItemStack(Material.GLASS_BOTTLE, 1)).forEach((key, value) -> {
                    player.getWorld().dropItem(player.getLocation(), value);
                });
            }
        }
    }
}
