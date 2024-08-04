package org.eu.smileyik.numericalrequirements.core.item;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.eu.smileyik.numericalrequirements.core.NumericalRequirements;
import org.eu.smileyik.numericalrequirements.core.api.item.ItemService;
import org.eu.smileyik.numericalrequirements.core.api.item.tag.ConsumableTag;
import org.eu.smileyik.numericalrequirements.core.api.item.tag.FunctionalTag;
import org.eu.smileyik.numericalrequirements.core.api.item.tag.ItemTag;
import org.eu.smileyik.numericalrequirements.core.api.item.tag.lore.LoreTag;
import org.eu.smileyik.numericalrequirements.core.api.item.tag.lore.LoreValue;
import org.eu.smileyik.numericalrequirements.core.api.item.tag.lore.MergeableLore;
import org.eu.smileyik.numericalrequirements.core.api.item.tag.nbt.NBTTag;
import org.eu.smileyik.numericalrequirements.core.api.player.NumericalPlayer;
import org.eu.smileyik.numericalrequirements.core.api.util.ConfigurationHashMap;
import org.eu.smileyik.numericalrequirements.core.api.util.Pair;
import org.eu.smileyik.numericalrequirements.core.api.util.YamlUtil;
import org.eu.smileyik.numericalrequirements.debug.DebugLogger;
import org.eu.smileyik.numericalrequirements.nms.nbt.NBTTagCompound;
import org.eu.smileyik.numericalrequirements.nms.nbt.NBTTagTypeId;
import org.eu.smileyik.numericalrequirements.nms.nbtitem.NBTItem;
import org.eu.smileyik.numericalrequirements.nms.nbtitem.NBTItemHelper;

import java.util.*;

public class ItemServiceImpl implements Listener, ItemService {
    private final NumericalRequirements plugin;
    private final Map<String, ItemTag<?>> idTagMap = new HashMap<>();
    private final Map<Byte, Set<String>> typeTagMap = new HashMap<>();
    private final Set<String> nbtTagSet = new HashSet<>();
    private final Set<String> loreTagSet = new HashSet<>();

    private final ItemKeeper itemKeeper;
    public ItemServiceImpl(NumericalRequirements plugin) {
        this.plugin = plugin;
        typeTagMap.put(TAG_TYPE_NORMAL, new HashSet<>());
        typeTagMap.put(TAG_TYPE_CONSUME, new HashSet<>());
        typeTagMap.put(TAG_TYPE_FUNCTIONAL, new HashSet<>());

        ConfigurationSection itemConfig = plugin.getConfig().getConfigurationSection("item");
        String sync = itemConfig.getString("sync", "enable").toLowerCase();
        itemKeeper = new FileItemKeeper(
                plugin,
                itemConfig.getConfigurationSection("serialization"),
                sync.equals("enable") || sync.equals("true")
        );

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

    private synchronized Map<ItemTag<?>, List<Object>> analyzeNBTTag(NBTTagCompound tagCompound, Set<String> ids) {
        Map<ItemTag<?>, List<Object>> map = new HashMap<>();
        for (String id : ids) {
            ItemTag<?> itemTag = idTagMap.get(id);
            if (itemTag instanceof NBTTag<?>) {
                NBTTag<?> nbtTag = (NBTTag<?>) itemTag;
                Object value = nbtTag.getValue(tagCompound);
                if (value != null) map.put(itemTag, Arrays.asList(value));
            }
        }
        return map;
    }

    @Override
    public synchronized Map<LoreTag, List<LoreValue>> analyzeLore(List<String> loreList, byte ... tagType) {
        byte type = 0;
        for (byte b : tagType) type |= b;
        return analyzeLore(loreList, type);
    }

    @Override
    public synchronized Map<LoreTag, List<LoreValue>> analyzeLore(List<String> loreList, byte tagType) {
        return analyzeLore(loreList, getTagIdsByType(tagType));
    }

    private synchronized Map<LoreTag, List<LoreValue>> analyzeLore(List<String> loreList, Set<String> ids) {
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
    public synchronized Pair<LoreTag, LoreValue> analyzeLore(String lore, byte ... tagType) {
        byte type = 0;
        for (byte b : tagType) type |= b;
        return analyzeLore(lore, type);
    }

    @Override
    public synchronized Pair<LoreTag, LoreValue> analyzeLore(String lore, byte tagType) {
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


    private synchronized Set<String> getTagIdsByType(byte tagType) {
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
        ItemStack item = itemKeeper.loadItem(id);
        if (item == null) return null;
        item.setAmount(amount);
        return item;
    }

    @Override
    public ItemStack loadItem(ConfigurationSection section, int amount) {
        return itemKeeper.loadItemFromYaml(section, amount);
    }

    @Override
    public void storeItem(String id, ItemStack stack) {
        itemKeeper.storeItem(id, stack);
    }

    @Override
    public ConfigurationSection storeItem(ItemStack stack) {
        ConfigurationHashMap configurationHashMap = itemKeeper.storeItem(stack);
        return YamlUtil.fromMap(configurationHashMap);
    }

    @Override
    public Collection<String> getItemIds() {
        return Collections.unmodifiableCollection(itemKeeper.getItemIds());
    }

    @Override
    public String getItemId(ItemStack itemStack) {
        if (itemStack == null) return null;
        NBTItem nbtItem = NBTItemHelper.cast(itemStack);
        if (nbtItem == null) return null;
        NBTTagCompound tag = nbtItem.getTag();
        if (tag == null) return null;
        if (!tag.hasKeyOfType(NBT_KEY_ID, NBTTagTypeId.STRING)) return null;
        return tag.getString(NBT_KEY_ID);
    }

    private synchronized boolean useItem(NumericalPlayer player, ItemStack item) {
        Map<ItemTag<?>, List<Object>> map =
                analyzeItem(item, (byte) (TAG_TYPE_NBT | TAG_TYPE_LORE | TAG_TYPE_CONSUME));
        if (map.isEmpty()) return false;
        map.forEach((k, v) -> ((ConsumableTag<Object>) k).onConsume(player, v));
        return true;
    }

    @Override
    public synchronized void reloadItems() {
        itemKeeper.reloadItems();
    }

    @Override
    public void shutdown() {
        itemKeeper.saveItems();
        itemKeeper.clear();
        idTagMap.clear();
        nbtTagSet.clear();
        loreTagSet.clear();
        typeTagMap.clear();
    }

    @Override
    public synchronized boolean updateItem(ItemStack item) {
        if (!itemKeeper.isSyncItem(item)) return false;
        String id = getItemId(item);
        ItemStack itemStack = loadItem(id, item.getAmount());
        if (itemStack == null || itemStack.isSimilar(item)) return false;
        item.setItemMeta(itemStack.getItemMeta());
        DebugLogger.debug(e -> DebugLogger.debug(e, "更新物品：%s", id));
        return true;
    }

    @EventHandler(priority = EventPriority.MONITOR)
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

    @EventHandler(priority = EventPriority.MONITOR)
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

        if (useItem(numericalPlayer, item)) {
            // TODO
        }
    }
}
