package org.eu.smileyik.numericalrequirements.core.item;

import org.bukkit.inventory.ItemStack;
import org.eu.smileyik.numericalrequirements.core.item.tag.service.LoreTagService;
import org.eu.smileyik.numericalrequirements.core.item.tag.service.LoreTagValue;
import org.eu.smileyik.numericalrequirements.core.util.Pair;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ItemService {
    String NBT_KEY_ID = "nreq-item";
    String NBT_KEY_SYNC = "nreq-sync";

    /**
     * 所有种类的Tag.
     */
    byte TAG_ALL = 0;
    /**
     * 仅分析可消耗的Tag.
     */
    byte TAG_CONSUME = 1;
    /**
     * 仅分析不可消耗的Tag.
     */
    byte TAG_UNCONSUME = 2;

    /**
     * 获取 Lore Tag 服务。
     * @return
     */
    LoreTagService getLoreTagService();

    /**
     * 注册一个 ItemTag
     * @param tag
     */
    void registerItemTag(ItemTag tag);

    /**
     * 取消注册指定 ItemTag
     * @param tag
     */
    void unregisterItemTag(ItemTag tag);

    /**
     * 根据ID获取一个ItemTag
     * @param id
     * @return
     */
    ItemTag getItemTagById(String id);

    /**
     * 获取全部ItemTag的ID
     * @return
     */
    List<String> getTagIds();

    /**
     * 分析Lore列表。
     * @param loreList 需要分析的lore
     * @param tagType 需要分析的ItemTag种类
     * @return 每个ItemTag及其对应的值，未找到符合要求的Tag则返回空Map
     */
    Map<ItemTag, List<LoreTagValue>> analyzeLoreList(List<String> loreList, byte tagType);

    /**
     * 分析单条Lore.
     * @param lore 需要分析的lore
     * @param tagType 需要分析的ItemTag种类
     * @return 返回符合的ItemTag以及其对应的值, 未找到符合的Tag则返回null
     */
    Pair<ItemTag, LoreTagValue> analyzeLore(String lore, byte tagType);

    /**
     * 判断ItemTag和Lore是否匹配。
     * @param tag
     * @param lore
     * @return 匹配返回true 反之返回false
     */
    boolean matches(ItemTag tag, String lore);

    /**
     * 通过ID加载一个物品。
     * @param id 物品ID
     * @param amount 物品数量
     * @return 指定的物品，不存在返回null.
     */
    ItemStack loadItem(String id, int amount);

    /**
     * 存储物品到配置文件
     * @param id 要存储的物品ID
     * @param stack 要存储的物品
     */
    void storeItem(String id, ItemStack stack);

    /**
     * 获取物品ID
     * @return
     */
    Collection<String> getItemIds();

    /**
     * 更新物品。
     * @param itemStack 要更新的物品
     * @return 如果更新成功返回true, 无需更新或无法更新等其他情况返回false.
     */
    boolean updateItem(ItemStack itemStack);

    void shutdown();
}
