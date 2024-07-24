package org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.event;

import org.bukkit.event.Cancellable;
import org.bukkit.inventory.ItemStack;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.Recipe;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 当调用 Recipe.takeInputs 时触发。若取消该事件则将要被拿的原材料物品不会被扣减。
 */
public class RecipeTakeItemEvent extends RecipeEvent implements Cancellable {
    private static final AtomicLong COUNTER = new AtomicLong();
    private static Long refreshedTimestamp;
    private static Long prevRefreshedTimestamp;

    private final long id;
    private final int slot;
    private final int index;
    private final int max;
    private boolean cancelled;
    private ItemStack item;

    public RecipeTakeItemEvent(Recipe recipe, long id, int slot, int index, int max, ItemStack item) {
        super(recipe);
        this.id = id;
        this.slot = slot;
        this.index = index;
        this.max = max;
        this.item = item;

    }

    public RecipeTakeItemEvent(boolean isAsync, Recipe recipe, long id, int slot, int index, int max, ItemStack item) {
        super(isAsync, recipe);
        this.id = id;
        this.slot = slot;
        this.index = index;
        this.max = max;
        this.item = item;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    /**
     * 获取将要被扣减的物品。
     * @return
     */
    public ItemStack getItem() {
        return item;
    }

    /**
     * 将要扣减的物品设置为指定物品。
     * @param item
     */
    public void setItem(ItemStack item) {
        this.item = item;
    }

    /**
     * 获取当前事件ID, 一般情况下事件ID相同仅代表属于同一次提取物品内的事件。
     * 事件ID为64位整形，每次调用 Recipe.takeInputs 方法时固定生成一个事件ID。
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
     * 当 getIndex() == getMax() 时代表 Recipe.takeInputs 方法的一个流程结束。
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
     * 生成下一个事件ID,本事件ID仅在 Recipe.takeInputs 方法开头时调用一次，
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
