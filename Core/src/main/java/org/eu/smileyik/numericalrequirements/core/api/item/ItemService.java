package org.eu.smileyik.numericalrequirements.core.api.item;

import org.bukkit.inventory.ItemStack;
import org.eu.smileyik.numericalrequirements.core.api.item.tag.ItemTag;
import org.eu.smileyik.numericalrequirements.core.api.item.tag.lore.LoreTag;
import org.eu.smileyik.numericalrequirements.core.api.item.tag.lore.LoreValue;
import org.eu.smileyik.numericalrequirements.core.api.util.Pair;
import org.eu.smileyik.numericalrequirements.nms.nbt.NBTTagCompound;
import org.eu.smileyik.numericalrequirements.nms.nbt.NBTTagTypeId;
import org.eu.smileyik.numericalrequirements.nms.nbtitem.NBTItem;
import org.eu.smileyik.numericalrequirements.nms.nbtitem.NBTItemHelper;

import java.util.List;
import java.util.Map;

public interface ItemService {
    String NBT_KEY_ID = "nreq-item";

    /**
     * Tag种类：不可消耗、无功能性
     */
    byte TAG_TYPE_NORMAL = 1;
    /**
     * Tag种类：可消耗。
     */
    byte TAG_TYPE_CONSUME = 1 << 1;
    /**
     * Tag种类：功能性
     */
    byte TAG_TYPE_FUNCTIONAL = 1 << 2;
    byte TAG_TYPE_LORE = (byte) (1 << 6);
    byte TAG_TYPE_NBT = (byte) (1 << 7);
    byte TAG_TYPE_MASK = 0x7;

    byte TAG_TYPE_ALL_LORE = TAG_TYPE_LORE | TAG_TYPE_NORMAL | TAG_TYPE_CONSUME | TAG_TYPE_FUNCTIONAL;

    /**
     * 注册一个 ItemTag
     * @param tag
     */
    void registerItemTag(ItemTag<?> tag);

    /**
     * 取消注册指定 ItemTag
     * @param tag
     */
    void unregisterItemTag(ItemTag<?> tag);

    /**
     * 根据ID获取一个ItemTag
     * @param id
     * @return
     */
    ItemTag<?> getItemTagById(String id);

    /**
     * 获取全部ItemTag的ID
     * @return
     */
    List<String> getTagIds();

    List<String> getTagIds(byte tagType);

    List<String> getTagIds(byte ... tagType);

    Map<ItemTag<?>, List<Object>> analyzeItem(ItemStack itemStack, byte tagType);

    /**
     * 分析Lore列表。
     * @param loreList 需要分析的lore
     * @param tagTypes 需要分析的ItemTag种类
     * @return 每个ItemTag及其对应的值，未找到符合要求的Tag则返回空Map
     */
    Map<LoreTag, List<LoreValue>> analyzeLore(List<String> loreList, byte... tagTypes);

    Map<LoreTag, List<LoreValue>> analyzeLore(List<String> loreList, byte tagType);

    /**
     * 分析单条Lore.
     * @param lore 需要分析的lore
     * @param tagType 需要分析的ItemTag种类
     * @return 返回符合的ItemTag以及其对应的值, 未找到符合的Tag则返回null
     */
    Pair<LoreTag, LoreValue> analyzeLore(String lore, byte tagType);

    Pair<LoreTag, LoreValue> analyzeLore(String lore, byte ... tagTypes);

    ItemKeeper getItemKeeper();

    /**
     * 获取物品ID，如果该物品不存在ID则返回null
     * @param itemStack
     * @return
     */
    static String getItemId(ItemStack itemStack) {
        if (itemStack == null) return null;
        NBTItem nbtItem = NBTItemHelper.cast(itemStack);
        if (nbtItem == null) return null;
        NBTTagCompound tag = nbtItem.getTag();
        if (tag == null) return null;
        if (!tag.hasKeyOfType(NBT_KEY_ID, NBTTagTypeId.STRING)) return null;
        return tag.getString(NBT_KEY_ID);
    }

    /**
     * 更新物品。
     * @param itemStack 要更新的物品
     * @return 如果更新成功返回true, 无需更新或无法更新等其他情况返回false.
     */
    boolean updateItem(ItemStack itemStack);

    void shutdown();
}
