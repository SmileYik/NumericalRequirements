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
import org.eu.smileyik.numericalrequirements.core.item.tag.ConsumableTag;
import org.eu.smileyik.numericalrequirements.core.item.tag.FunctionalTag;
import org.eu.smileyik.numericalrequirements.core.item.tag.ItemTag;
import org.eu.smileyik.numericalrequirements.core.item.tag.lore.LoreTag;
import org.eu.smileyik.numericalrequirements.core.item.tag.lore.LoreValue;
import org.eu.smileyik.numericalrequirements.core.item.tag.lore.MergeableLore;
import org.eu.smileyik.numericalrequirements.core.item.tag.nbt.NBTTag;
import org.eu.smileyik.numericalrequirements.core.player.NumericalPlayer;
import org.eu.smileyik.numericalrequirements.core.util.Pair;
import org.eu.smileyik.numericalrequirements.core.util.YamlUtil;
import org.eu.smileyik.numericalrequirements.debug.DebugLogger;
import org.eu.smileyik.numericalrequirements.nms.nbt.NBTTagCompound;
import org.eu.smileyik.numericalrequirements.nms.nbtitem.NBTItem;
import org.eu.smileyik.numericalrequirements.nms.nbtitem.NBTItemHelper;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ItemServiceImpl implements Listener, ItemService {
    private final NumericalRequirements plugin;
    private final Map<String, ItemTag<?>> idTagMap = new HashMap<>();
    private final Map<Byte, Set<String>> typeTagMap = new HashMap<>();
    private final Set<String> nbtTagSet = new HashSet<>();
    private final Set<String> loreTagSet = new HashSet<>();

    private final File itemFile;
    private final YamlConfiguration itemConfig;
    private final ItemSerialization itemSerialization = new YamlItemSerialization();
    private final ConcurrentMap<String, ItemStack> itemStackCache = new ConcurrentHashMap<>();

    public ItemServiceImpl(NumericalRequirements plugin) {
        this.plugin = plugin;
        typeTagMap.put(TAG_TYPE_NORMAL, new HashSet<>());
        typeTagMap.put(TAG_TYPE_CONSUME, new HashSet<>());
        typeTagMap.put(TAG_TYPE_FUNCTIONAL, new HashSet<>());

        itemSerialization.configure(plugin.getConfig().getConfigurationSection("item.serialization"));
        itemFile = new File(plugin.getDataFolder(), "items.yml");
        if (!itemFile.exists()) {
            try {
                itemFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        itemConfig = YamlConfiguration.loadConfiguration(itemFile);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public synchronized void registerItemTag(ItemTag<?> tag) {
        String id = tag.getId().toLowerCase();
        if (idTagMap.containsKey(id)) {
            return;
        }
        idTagMap.put(id, tag);
        boolean flag = true;
        if (tag instanceof NBTTag<?>) {
            nbtTagSet.add(id);
        } else {
            loreTagSet.add(id);
        }
        if (tag instanceof ConsumableTag<?>) {
            typeTagMap.get(TAG_TYPE_CONSUME).add(id);
            flag = false;
        }
        if (tag instanceof FunctionalTag<?>) {
            typeTagMap.get(TAG_TYPE_FUNCTIONAL).add(id);
            flag = false;
        }
        if (flag) {
            typeTagMap.get(TAG_TYPE_NORMAL).add(id);
        }
    }

    @Override
    public synchronized void unregisterItemTag(ItemTag<?> tag) {
        String id = tag.getId().toLowerCase();
        idTagMap.remove(id);
        typeTagMap.get(TAG_TYPE_NORMAL).remove(id);
        typeTagMap.get(TAG_TYPE_CONSUME).remove(id);
        typeTagMap.get(TAG_TYPE_FUNCTIONAL).remove(id);
        nbtTagSet.remove(id);
        loreTagSet.remove(id);
    }

    @Override
    public synchronized ItemTag<?> getItemTagById(String id) {
        return idTagMap.get(id.toLowerCase());
    }

    @Override
    public synchronized List<String> getTagIds() {
        return new ArrayList<>(idTagMap.keySet());
    }

    @Override
    public synchronized List<String> getTagIds(byte ... types) {
        byte type = 0;
        for (byte b : types) type |= b;
        return getTagIds(type);
    }

    @Override
    public synchronized List<String> getTagIds(byte type) {
        return new ArrayList<>(getTagIdsByType(type));
    }

    @Override
    public synchronized Map<ItemTag<?>, List<Object>> analyzeItem(ItemStack itemStack, byte tagType) {
        if (itemStack == null) return new HashMap<>();
        Set<String> allIds = getTagIdsByType(tagType);
        Map<ItemTag<?>, List<Object>> result = new HashMap<>();
        if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasLore()) {
            Set<String> loreIds = new HashSet<>(allIds);
            loreIds.removeAll(nbtTagSet);
            Map<LoreTag, List<LoreValue>> loreTagListMap = analyzeLore(itemStack.getItemMeta().getLore(), loreIds);
            loreTagListMap.forEach((k, v) -> result.put(k, new ArrayList<>(v)));
        }
        NBTItem item = NBTItemHelper.cast(itemStack);
        if (item != null && item.hasTag()) {
            NBTTagCompound tagCompound = item.getTag();
            if (tagCompound != null) {
                allIds.removeAll(loreTagSet);
                result.putAll(analyzeNBTTag(tagCompound, allIds));
            }
        }
        return result;
    }

    private Map<ItemTag<?>, List<Object>> analyzeNBTTag(NBTTagCompound tagCompound, Set<String> ids) {
        Map<ItemTag<?>, List<Object>> map = new HashMap<>();
        for (String id : ids) {
            ItemTag<?> itemTag = idTagMap.get(id);
            if (itemTag instanceof NBTTag<?>) {
                NBTTag<?> nbtTag = (NBTTag<?>) itemTag;
                map.put(itemTag, List.of(nbtTag.getValue(tagCompound)));
            }
        }
        return map;
    }

    @Override
    public Map<LoreTag, List<LoreValue>> analyzeLore(List<String> loreList, byte ... tagType) {
        byte type = 0;
        for (byte b : tagType) type |= b;
        return analyzeLore(loreList, type);
    }

    @Override
    public Map<LoreTag, List<LoreValue>> analyzeLore(List<String> loreList, byte tagType) {
        return analyzeLore(loreList, getTagIdsByType(tagType));
    }

    private Map<LoreTag, List<LoreValue>> analyzeLore(List<String> loreList, Set<String> ids) {
        Map<LoreTag, List<LoreValue>> result = new LinkedHashMap<>();
        for (String lore : loreList) {
            lore = ChatColor.stripColor(lore);
            for (String id : ids) {
                LoreTag tag = (LoreTag) idTagMap.get(id);
                LoreValue value = tag.getValue(lore);
                if (value == null) {
                    continue;
                }

                if (!result.containsKey(tag)) {
                    result.put(tag, new ArrayList<>());
                }
                List<LoreValue> loreValues = result.get(tag);
                if (!loreValues.isEmpty() && tag instanceof MergeableLore) {
                    ((MergeableLore) tag).merge(loreValues.get(0), value);
                } else {
                    loreValues.add(value);
                }
            }
        }
        return result;
    }

    @Override
    public Pair<LoreTag, LoreValue> analyzeLore(String lore, byte ... tagType) {
        byte type = 0;
        for (byte b : tagType) type |= b;
        return analyzeLore(lore, type);
    }

    @Override
    public Pair<LoreTag, LoreValue> analyzeLore(String lore, byte tagType) {
        lore = ChatColor.stripColor(lore);
        Set<String> ids = getTagIdsByType(tagType);
        for (String id : ids) {
            LoreTag tag = (LoreTag) idTagMap.get(id);
            LoreValue v = tag.getValue(lore);
            if (v == null) continue;
            return Pair.newUnchangablePair(tag, v);
        }
        return null;
    }


    private Set<String> getTagIdsByType(byte tagType) {
        Set<String> set = new HashSet<>();
        if ((tagType & 0x40) == 0x40) {
            set.addAll(loreTagSet);
        }
        if ((tagType & 0x80) == 0x80) {
            set.addAll(nbtTagSet);
        }

        tagType = (byte) (tagType & TAG_TYPE_MASK);
        byte pos = 1;
        Set<String> need = new HashSet<>();
        Set<String> other = new HashSet<>();
        while (tagType != 0) {
            if ((tagType & 0x1) == 1) {
                need.addAll(typeTagMap.get(pos));
            } else {
                other.addAll(typeTagMap.get(pos));
            }
            pos <<= 1;
            tagType >>>= 1;
        }
        other.removeAll(need);
        set.removeAll(other);
        return set;
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
        if (itemStack == null || isStore && !idTagMap.containsKey(id)) {
            return null;
        }

        NBTItem cast = NBTItemHelper.cast(itemStack.clone());
        if (cast != null) {
            cast.getTag().setString(NBT_KEY_ID, id);
            itemStack = cast.getItemStack();
        }

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
        Map<ItemTag<?>, List<Object>> itemTagListMap =
                analyzeItem(item, (byte) (TAG_TYPE_NBT | TAG_TYPE_LORE | TAG_TYPE_CONSUME));
        itemTagListMap.forEach((k, v) -> {
            ((ConsumableTag<Object>) k).onConsume(player, v);
        });
        return true;
    }

    @Override
    public void shutdown() {
        saveItemFile();
        idTagMap.clear();
        nbtTagSet.clear();
        loreTagSet.clear();
        typeTagMap.clear();
        itemStackCache.clear();
    }

    @Override
    public boolean updateItem(ItemStack item) {
        NBTItem nbtItem = NBTItemHelper.cast(item);
        if (nbtItem == null) return false;
        NBTTagCompound tag = nbtItem.getTag();
        if (tag == null) return false;
        if (!tag.hasKey(NBT_KEY_ID) ||
                tag.hasKey(NBT_KEY_SYNC) && !tag.getBoolean(NBT_KEY_SYNC)) {
            return false;
        }
        String id = tag.getString(NBT_KEY_ID);
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
