package org.eu.smileyik.numericalrequirements.core.item;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.eu.smileyik.numericalrequirements.core.NumericalRequirements;
import org.eu.smileyik.numericalrequirements.core.item.tag.service.*;
import org.eu.smileyik.numericalrequirements.core.player.NumericalPlayer;
import org.eu.smileyik.numericalrequirements.core.util.Pair;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ItemServiceImpl implements Listener, ItemService {
    private final NumericalRequirements plugin;
    private final Map<String, ItemTag> idTagMap = new HashMap<>();
    private final Collection<ItemTag> normalItemTags = new ArrayList<>();
    private final Collection<ItemTag> consumeItemTags = new ArrayList<>();
    private final Map<ItemTag, LoreTagPattern> noColorTagMap = new HashMap<>();
    private final LoreTagService loreTagService;

    private final File itemFile;
    private final YamlConfiguration itemConfig;

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
    public ItemTag getItemTagById(String id) {
        return idTagMap.get(id.toLowerCase());
    }

    @Override
    public List<String> getTagIds() {
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
        if (!itemConfig.isConfigurationSection(id)) {
            return null;
        }
        ConfigurationSection section = itemConfig.getConfigurationSection(id);
        if (!section.contains("material")) {
            return null;
        }
        ItemStack stack = new ItemStack(Material.matchMaterial(section.getString("material")));
        ItemMeta meta = stack.getItemMeta();

        if (section.contains("name")) {
            meta.setDisplayName(section.getString("name"));
        }
        if (section.contains("lore")) {
            meta.setLore(section.getStringList("lore"));
        }
        if (section.contains("durability")) {
            stack.setDurability((short) section.getInt("durability"));
        }
        if (section.contains("enchantment")) {
            ConfigurationSection ench = section.getConfigurationSection("enchantment");
            for (String key : ench.getKeys(false)) {
                meta.addEnchant(Enchantment.getByName(key), ench.getInt(key), true);
            }
        }
        if (section.contains("flags")) {
            for (String flag : section.getStringList("flags")) {
                meta.addItemFlags(ItemFlag.valueOf(flag));
            }
        }
        stack.setItemMeta(meta);
        stack.setAmount(amount);
        return stack;
    }

    @Override
    public void storeItem(String id, ItemStack stack) {
        ConfigurationSection section = itemConfig.createSection(id);
        section.set("material", stack.getType().name());
        section.set("durability", stack.getDurability());
        if (stack.hasItemMeta()) {
            ItemMeta meta = stack.getItemMeta();
            if (meta.hasDisplayName()) {
                section.set("name", meta.getDisplayName());
            }
            if (meta.hasLore()) {
                section.set("lore", meta.getLore());
            }
            if (meta.hasEnchants()) {
                meta.getEnchants().forEach((k, v) -> {
                    section.set("enchantment." + k, v);
                });
            }
            Set<ItemFlag> itemFlags = meta.getItemFlags();
            if (!itemFlags.isEmpty()) {
                section.set("flags", new ArrayList<>(itemFlags));
            }
        }
        saveItemFile();
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
