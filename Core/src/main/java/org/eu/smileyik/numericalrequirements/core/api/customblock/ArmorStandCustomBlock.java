package org.eu.smileyik.numericalrequirements.core.api.customblock;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.eu.smileyik.numericalrequirements.core.api.NumericalRequirements;
import org.eu.smileyik.numericalrequirements.core.api.item.ItemService;
import org.eu.smileyik.numericalrequirements.core.api.util.ConfigurationHashMap;
import org.eu.smileyik.numericalrequirements.debug.DebugLogger;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 该自定义方块的实现是: 给盔甲架头部放置一个物品, 并生成在要放置的位置,
 * 与此同时, 在要放置的位置上放置一个方块, 并且放置的方块与盔甲架头部物品相同,
 * 而该物品为 getBlockItemId 方法返回的物品ID所对应的物品.
 * 利用此方法将生成一个真实的, 在世界中存在的盔甲架实体以及一个方块.
 */
public class ArmorStandCustomBlock implements CustomBlock {
    protected static final Material BARRIER = Material.valueOf("BARRIER");
    protected static final Material STONE = Material.valueOf("STONE");
    protected static final Material AIR = Material.valueOf("AIR");
    protected final ItemService itemService = NumericalRequirements.getInstance().getItemService();

    private String id;
    private String blockItemId;
    private final Map<Pos, ArmorStand> armorStands = Collections.synchronizedMap(new HashMap<>());

    @Override
    public String getBlockItemId() {
        return blockItemId;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 放置方块的工作需要在与主线程同步的情况下运行. 放置时要记录盔甲架位置.
     * @param itemStack
     * @param location
     * @param pos
     */
    private synchronized void syncPlace(ItemStack itemStack, Location location, Pos pos) {
        synchronized (armorStands) {
            Block b = location.getBlock();
            b.setType(itemStack.getType());
            b.getState().setType(itemStack.getType());

            removeUnregisterArmorStand(pos, location);

            ArmorStand armorStand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
            armorStand.setVisible(false);
            armorStand.setMarker(true);
            armorStand.setHelmet(itemStack);
            armorStands.put(pos, armorStand);
            DebugLogger.debug("placed armor stand at %s", pos);
        }
    }

    @Override
    public synchronized void place(Pos pos) {
        // 如果该方块位置已经有了一个盔甲架, 则直接放弃放置.
        synchronized (armorStands) {
            if (armorStands.containsKey(pos)) {
                return;
            }
        }

        ItemStack itemStack = itemService.getItemKeeper().loadItem(getBlockItemId());
        Location location = pos.toEntityLocation();
        NumericalRequirements.getInstance().runTask(() -> syncPlace(itemStack, location, pos));
    }

    /**
     * 移除一个自定义方块的工作需要在与主线程同步情况下运行.
     * @param remove
     * @param pos
     */
    private synchronized void syncRemove(ArmorStand remove, Pos pos, boolean isUnload) {
        try {
            if (!isUnload) {
                Block block = pos.toLocation().getBlock();
                block.setType(AIR);
                block.getState().setType(AIR);
            }
            // 防止标记删除时, 盔甲架显示的物品还在.
            remove.setHelmet(null);

            // 加大力度删除他, 因为 remove 方法只是标记盔甲架需要删除.
            remove.setHealth(0);
            remove.damage(999999);
            remove.remove();

            Location location = pos.toEntityLocation();
            removeUnregisterArmorStand(pos, location);

            DebugLogger.debug("removed armor stand at %s", pos);
        } catch (Throwable e) {
            DebugLogger.debug(e);
        }
    }

    private void removeUnregisterArmorStand(Pos pos, Location location) {
        for (Entity nearbyEntity : location.getWorld().getNearbyEntities(location, 0.5, 0.5, 0.5)) {
            if (nearbyEntity instanceof ArmorStand) {
                Location other = nearbyEntity.getLocation();
                if (other.getBlockX() == location.getBlockX() && other.getBlockY() == location.getBlockY() && other.getBlockZ() == location.getBlockZ()) {
                    nearbyEntity.remove();
                    DebugLogger.debug("removed unregister armor stand at %s", pos);
                }
            }
        }
    }

    @Override
    public void remove(Pos pos) {
        remove(pos, false);
    }

    @Override
    public synchronized void remove(Pos pos, boolean isUnload) {
        ArmorStand armorStand = null;
        synchronized (armorStands) {
            armorStand = armorStands.remove(pos);
        }
        final ArmorStand remove = armorStand;
        if (remove != null) {
            if (NumericalRequirements.getPlugin().getServer().isPrimaryThread()) {
                syncRemove(remove, pos, isUnload);
            } else {
                NumericalRequirements.getInstance().runTask(() -> syncRemove(remove, pos, isUnload));
            }
        }
    }

    @Override
    public void drop(Player player, Location location) {
        ItemStack itemStack = itemService.getItemKeeper().loadItem(getBlockItemId());
        if (itemStack == null) return;
        location.getWorld().dropItemNaturally(
                location, itemStack
        );
    }

    @Override
    public void load(ConfigurationHashMap section) {
        CustomBlock.super.load(section);
        this.blockItemId = section.getString("block-item-id");
    }

    @Override
    public void save(ConfigurationHashMap section) {
        CustomBlock.super.save(section);
        section.put("block-item-id", blockItemId);
    }
}
