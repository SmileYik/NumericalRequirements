package org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.event;

import org.bukkit.inventory.ItemStack;
import org.eu.smileyik.numericalrequirements.multiblockcraft.MultiBlockCraftExtension;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.Recipe;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 配方重新格式化物品事件。
 * 配方在加载时以及在使用 Recipe.isMatch 的方法时，都要将输入的物品格式化为易于比对的物品序列。
 * 在 Recipe 类规范输入物品时，将会为每一个不为 null 的物品广播一次本事件。（若物品为 null 则代表为最后一个事件）
 * 对于部分需要校验可变物品数据的物品的配方，可以使用此事件更改对应物品为一个标准的物品模板，
 * 以通过配方校验，并配合 RecipeTakeItemEvent 事件，对输入物品进行扣除数据或其他操作。
 */
public class RecipeFormatItemEvent extends RecipeEvent {
    private static final AtomicLong COUNTER = new AtomicLong();
    private static Long refreshedTimestamp;
    private static Long prevRefreshedTimestamp;

    private final long id;
    private final int slot;
    private final int index;
    private final int max;
    private ItemStack item;

    public RecipeFormatItemEvent(Recipe recipe, long id, int slot, int index, int max, ItemStack item) {
        super(!MultiBlockCraftExtension.getInstance().getPlugin().getServer().isPrimaryThread(), recipe);
        this.id = id;
        this.slot = slot;
        this.index = index;
        this.max = max;
        this.item = item;
    }

    public RecipeFormatItemEvent(boolean isAsync, Recipe recipe, long id, int slot, int index, int max, ItemStack item) {
        super(isAsync, recipe);
        this.id = id;
        this.slot = slot;
        this.index = index;
        this.max = max;
        this.item = item;
    }

    /**
     * 获取当前物品。一般情况下当前物品都不会为null,而当本事件作为一系列事件的尾包时，可能会返回null.
     * @return
     */
    public ItemStack getItem() {
        return item;
    }

    /**
     * 设定当前物品。当当前物品为null时，本事件将作为一个补充尾包触发，故在此时不应该调用此方法。
     * @param item
     */
    public void setItem(ItemStack item) {
        this.item = item;
    }

    /**
     * 获取当前事件ID, 一般情况下事件ID相同仅代表属于同一系列事件。
     * 事件ID为64位整形，每次进行一系列此事件时固定生成一个事件ID。
     * @return
     */
    public long getId() {
        return id;
    }

    /**
     * 获取当前物品所属于槽位。此槽位并不属于物品真实的槽位，而是输入物品按顺序的相对位置。
     * @return 从0开始的数字
     */
    public int getSlot() {
        return slot;
    }

    /**
     * 获取当前事件为整个流程中的第几个事件，从0开始。该事件将按顺序依次响应。
     * @return
     */
    public int getIndex() {
        return index;
    }

    /**
     * 获取当前事件中最后一个事件的下标。该事件将按顺序依次响应。
     * 当 getIndex() == getMax() 时，代表该系列事件结束。
     * @return
     */
    public int getMax() {
        return max;
    }

    /**
     * 获取上一次刷新(溢出且回归至0)的时间戳，若返回的数字为 0 代表第一次刷新（第一次固定为0）。
     * @return
     */
    public static Long getPrevRefreshedTimestamp() {
        return prevRefreshedTimestamp;
    }

    /**
     * 获取本次刷新的时间戳。
     * @return
     */
    public static Long getRefreshedTimestamp() {
        return refreshedTimestamp;
    }

    /**
     * 生成下一个事件ID,本事件ID仅在一系列此事件的开头时调用一次，
     * 并作为后续数个本事件的ID参数。
     * @return
     */
    public static long nextId() {
        long id = COUNTER.getAndIncrement();
        if (id == 0) {
            prevRefreshedTimestamp = refreshedTimestamp;
            refreshedTimestamp = System.currentTimeMillis();
        }
        return id;
    }
}
