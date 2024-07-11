package org.eu.smileyik.numericalrequirements.thirst.listener;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.eu.smileyik.numericalrequirements.core.I18N;
import org.eu.smileyik.numericalrequirements.core.api.NumericalRequirements;
import org.eu.smileyik.numericalrequirements.core.api.item.ItemService;
import org.eu.smileyik.numericalrequirements.debug.DebugLogger;
import org.eu.smileyik.numericalrequirements.reflect.ReflectClass;
import org.eu.smileyik.numericalrequirements.reflect.builder.ReflectClassBuilder;

import java.util.HashSet;
import java.util.Set;

public class GetLiquid implements Listener {
    private static final boolean flag;
    private static final ReflectClass CLASS_FLUID_COLLISION_MODE;
    private static final ReflectClass CLASS_LIVING_ENTITY;
    private static final Object ALWAYS;

    static {
        ReflectClass classFluidCollisionMode = null;
        ReflectClass classLivingEntity = null;
        boolean flag0 = true;
        Object always = null;
        try {
            classFluidCollisionMode = new ReflectClassBuilder("org.bukkit.FluidCollisionMode")
                    .method("valueOf").args("java.lang.String")
                    .toClass();
            classLivingEntity = new ReflectClassBuilder("org.bukkit.entity.LivingEntity")
                    .method("getTargetBlockExact").args("int", "org.bukkit.FluidCollisionMode")
                    .toClass();
            always = classFluidCollisionMode.execute("valueOf", null, "ALWAYS");
        } catch (Exception e) {
            flag0 = false;
            DebugLogger.debug("Disable fluid collision mode");
            DebugLogger.debug(e);
        }
        CLASS_FLUID_COLLISION_MODE = classFluidCollisionMode;
        CLASS_LIVING_ENTITY = classLivingEntity;
        ALWAYS = always;
        flag = flag0;
    }

    private final Set<String> ignore;
    private final Set<String> liquid;
    private final int maxDistance;
    private final String defaultWaterId;
    private final boolean enableBiomeWater;
    private final boolean checkAir;
    private final boolean scanMode;
    private final ConfigurationSection section;
    private final ItemService itemService = NumericalRequirements.getInstance().getItemService();
    public GetLiquid(ConfigurationSection config) {
        if (config == null) {
            config = new YamlConfiguration();
        }
        this.ignore = new HashSet<>(config.getStringList("ignore"));
        this.liquid = new HashSet<>(config.getStringList("liquid-type"));
        this.maxDistance = config.getInt("max-distance", 2);
        this.scanMode = config.getBoolean("scan-mode", false);
        this.checkAir = config.getBoolean("check-air", false);
        this.defaultWaterId = config.getString("default-liquid", "default-water");
        this.enableBiomeWater = config.getBoolean("biome-liquid", true);
        this.section = config;
    }

    @EventHandler()
    public void onPlayerGetWater(final PlayerInteractEvent e) {
        if (!e.hasItem() || e.getItem().getType() != Material.GLASS_BOTTLE) return;
        if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (e.hasBlock() && ignore.contains(e.getClickedBlock().getType().name())) return;
        if (!checkAir && e.getAction() == Action.RIGHT_CLICK_AIR) {
            e.setCancelled(true);
            return;
        }
        Player player = e.getPlayer();
        Block prev = null;
        boolean invalid = true;
        if (scanMode) {
            for (int i = 1; i <= maxDistance; i++) {
                Block targetBlock = getTargetBlock(player, i)      ;
                DebugLogger.debug("scanning: distance: %d; target: %s", i, targetBlock);
                if (targetBlock == null) continue;
                if (liquid.contains(targetBlock.getType().name())) {
                    prev = targetBlock;
                    invalid = false;
                    break;
                } else if (targetBlock.equals(prev)) {
                    break;
                }
                prev = targetBlock;
            }
        } else {
            prev = getTargetBlock(player, maxDistance) ;
            DebugLogger.debug("distance: %d; target: %s", maxDistance, prev);
            invalid = prev == null || !liquid.contains(prev.getType().name());
        }

        if (prev != null && liquid.contains(prev.getType().name())) e.setCancelled(true);
        if (prev == null && e.hasBlock() || checkAir) e.setCancelled(true);
        if (invalid) return;

        String waterId = defaultWaterId;
        if (enableBiomeWater) {
            waterId = section.getString(String.format("biome.%s", prev.getBiome().name()), defaultWaterId);
        }
        ItemStack itemStack = itemService.loadItem(waterId, 1);
        if (itemStack == null) {
            I18N.warning("extensions.thirst.get-liquid.item-not-found", waterId);
            return;
        }
        ItemStack item = e.getItem();
        if (item != null) {
            item.setAmount(item.getAmount() - 1);
            player.getInventory().addItem(itemStack).forEach((k, v) -> {
                player.getWorld().dropItem(player.getLocation(), v);
            });
        }
    }

    private Block getTargetBlock(Player p, int maxDistance) {
        if (flag) {
            return (Block) CLASS_LIVING_ENTITY.execute("getTargetBlockExact", p, maxDistance, ALWAYS);
        } else {
            return p.getTargetBlock(null, maxDistance);
        }
    }
}
