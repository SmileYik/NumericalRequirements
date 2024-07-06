package org.eu.smileyik.numericalrequirements.core.item;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.eu.smileyik.numericalrequirements.core.NumericalRequirements;
import org.eu.smileyik.numericalrequirements.core.item.serialization.ItemSerialization;
import org.eu.smileyik.numericalrequirements.core.item.serialization.YamlItemSerialization;
import org.eu.smileyik.numericalrequirements.core.item.tag.service.*;
import org.eu.smileyik.numericalrequirements.core.player.NumericalPlayer;
import org.eu.smileyik.numericalrequirements.core.util.Pair;
import org.eu.smileyik.numericalrequirements.core.util.YamlUtil;
import org.eu.smileyik.numericalrequirements.debug.DebugLogger;
import org.eu.smileyik.numericalrequirements.nms.nbtitem.NBTItem;
import org.eu.smileyik.numericalrequirements.nms.nbtitem.NBTItemHelper;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ItemServiceImpl implements Listener, ItemService {
    private static final String NBT_ITEM_KEY = "NREQ_ITEM";

    private final NumericalRequirements plugin;
    private final Map<String, ItemTag> idTagMap = new HashMap<>();
    private final Collection<ItemTag> normalItemTags = new ArrayList<>();
    private final Collection<ItemTag> consumeItemTags = new ArrayList<>();
    private final Map<ItemTag, LoreTagPattern> noColorTagMap = new HashMap<>();
    private final LoreTagService loreTagService;

    private final File itemFile;
    private final YamlConfiguration itemConfig;
    private final ItemSerialization itemSerialization = new YamlItemSerialization();
    private final ConcurrentMap<String, ItemStack> itemStackCache = new ConcurrentHashMap<>();

    public ItemServiceImpl(NumericalRequirements plugin) {
        this.plugin = plugin;
        loreTagService = new SimpleLoreTagService();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        itemFile = new File(plugin.getDataFolder(), "items.yml");
        if (!itemFile.exists()) {
            try {
                itemFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        itemConfig = YamlConfiguration.loadConfiguration(itemFile);
    }

    @Override
    public LoreTagService getLoreTagService() {
        return loreTagService;
    }

    @Override
    public synchronized void registerItemTag(ItemTag tag) {
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
    public synchronized void unregisterItemTag(ItemTag tag) {
        String id = tag.getTagId().toLowerCase();
        idTagMap.remove(id);
        idTagMap.remove(id);
        consumeItemTags.remove(tag);
        normalItemTags.remove(tag);
        noColorTagMap.remove(tag);
    }

    @Override
    public synchronized ItemTag getItemTagById(String id) {
        return idTagMap.get(id.toLowerCase());
    }

    @Override
    public synchronized List<String> getTagIds() {
        return new ArrayList<>(idTagMap.keySet());
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

    @Override
    public ItemStack loadItem(String id, int amount) {
        ItemStack cache = getCachedItem(id);
        if (cache == null) {
            if (itemConfig.isConfigurationSection(id)) {
                ConfigurationSection section = itemConfig.getConfigurationSection(id);
                cache = itemSerialization.deserialize(YamlUtil.saveToString(section));
                cache = updateCachedItem(id, cache, false);
            }
        } else {
            cache = cache.clone();
        }

        if (cache == null) return null;
        cache.setAmount(amount);
        return cache;
    }

    @Override
    public void storeItem(String id, ItemStack stack) {
        try {
            String serialize = itemSerialization.serialize(stack);
            System.out.println(serialize);
            ConfigurationSection section = YamlUtil.loadFromString(serialize);
            itemConfig.set(id, section);
            saveItemFile();
            updateCachedItem(id, stack, true);
        } catch (InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    private ItemStack getCachedItem(String id) {
        return itemStackCache.get(id);
    }

    private ItemStack updateCachedItem(String id, ItemStack itemStack, boolean isStore) {
        if (isStore && !idTagMap.containsKey(id)) {
            return null;
        }

        itemStack = Objects.requireNonNull(NBTItemHelper.cast(itemStack.clone()))
                .append(NBT_ITEM_KEY, id)
                .getItemStack();
        itemStackCache.put(id, itemStack);
        return itemStack;
    }

    @Override
    public Collection<String> getItemIds() {
        return Collections.unmodifiableCollection(itemConfig.getKeys(false));
    }

    private void saveItemFile() {
        try {
            itemConfig.save(itemFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        saveItemFile();

        loreTagService.shutdown();
        idTagMap.clear();
        normalItemTags.clear();
        consumeItemTags.clear();
        noColorTagMap.clear();
    }

    private boolean updateItem(ItemStack item) {
        NBTItem nbtItem = NBTItemHelper.cast(item);
        if (nbtItem == null) return false;
        if (!nbtItem.containsKey(NBT_ITEM_KEY)) return false;
        String id = nbtItem.getString(NBT_ITEM_KEY);
        if (id == null) return false;
        ItemStack itemStack = loadItem(id, item.getAmount());
        if (itemStack == null || itemStack.isSimilar(item)) return false;
        item.setItemMeta(itemStack.getItemMeta());
        DebugLogger.debug(e -> DebugLogger.debug(e, "更新物品：%s", id));
        return true;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void updateOnInteractItem(PlayerInteractEvent event) {
        if (!event.hasItem()) return;
        ItemStack item = event.getItem();
        boolean ret = updateItem(item);
        DebugLogger.debug(e -> {
            if (ret) DebugLogger.debug(e, "尝试为为玩家 %s 更新物品", event.getPlayer().getName());
        });
    }

//    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
//    public void updateOnConsumeItem(PlayerItemConsumeEvent event) {
//        ItemStack item = event.getItem();
//        if (item == null) return;
//        boolean ret = updateItem(item);
//        if (ret) {
//            // event.setCancelled(true);
//            event.setItem(item);
//            DebugLogger.debug(e -> {
//                DebugLogger.debug(e, "尝试为为玩家 %s 更新物品", event.getPlayer().getName());
//            });
//        }
//    }

    @EventHandler(ignoreCancelled = true)
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

    @EventHandler(ignoreCancelled = true)
    public void onPlayerConsumeItem(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        NumericalPlayer numericalPlayer = plugin.getPlayerService().getNumericalPlayer(player);
        if (numericalPlayer == null) return;

        ItemStack item = event.getItem();
        if (updateItem(item)) {
            event.setItem(item);
        }

        boolean potion = item.getType() == Material.POTION;
        if (useItem(numericalPlayer, item)) {
            // event.setCancelled(true);

            PlayerInventory inventory = player.getInventory();
//            if (potion) {
//                inventory.addItem(new ItemStack(Material.GLASS_BOTTLE, 1)).forEach((key, value) -> {
//                    player.getWorld().dropItem(player.getLocation(), value);
//                });
//            }
        }
    }
}
