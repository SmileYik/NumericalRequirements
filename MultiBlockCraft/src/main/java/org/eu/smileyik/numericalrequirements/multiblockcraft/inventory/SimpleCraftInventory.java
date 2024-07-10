package org.eu.smileyik.numericalrequirements.multiblockcraft.inventory;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.eu.smileyik.numericalrequirements.core.api.NumericalRequirements;
import org.eu.smileyik.numericalrequirements.core.api.util.Pair;
import org.eu.smileyik.numericalrequirements.multiblockcraft.MultiBlockCraftExtension;
import org.eu.smileyik.numericalrequirements.multiblockcraft.inventory.holder.CraftInventoryHolder;
import org.eu.smileyik.numericalrequirements.multiblockcraft.inventory.holder.CreateRecipeHolder;
import org.eu.smileyik.numericalrequirements.multiblockcraft.inventory.holder.SimpleCraftHolder;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.Recipe;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.ordered.OrderedRecipe;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.unordered.UnorderedRecipe;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SimpleCraftInventory extends AbstractCraftInventory {
    private final Plugin plugin = (Plugin) NumericalRequirements.getInstance();
    private final List<Integer> materialSlots = Arrays.asList(0, 1, 2, 9, 10, 11, 18, 19, 20);
    private final List<Integer> productSlots = Arrays.asList(4, 5, 13, 14);

    public SimpleCraftInventory(String id) {
        super(id);
    }


    @Override
    public Class<? extends CraftInventoryHolder> getHolderClass() {
        return SimpleCraftHolder.class;
    }

    @Override
    public String getInventoryTitle() {
        return "TEST";
    }

    @Override
    public Inventory createInventory() {
        SimpleCraftHolder simpleCraftHolder = new SimpleCraftHolder();
        simpleCraftHolder.setCraftInventory(this);
        Inventory inventory = plugin.getServer().createInventory(simpleCraftHolder, 9 * 3, getInventoryTitle());
        simpleCraftHolder.setInventory(inventory);
        return inventory;
    }

    public Inventory createCreateInventory(boolean ordered) {
        class CreateClass extends SimpleCraftHolder implements CreateRecipeHolder {

            @Override
            public boolean isOrdered() {
                return ordered;
            }
        }

        CreateClass simpleCraftHolder = new CreateClass();
        simpleCraftHolder.setCraftInventory(this);
        Inventory inventory = plugin.getServer().createInventory(simpleCraftHolder, 9 * 3, getInventoryTitle());
        simpleCraftHolder.setInventory(inventory);
        return inventory;
    }

    @Override
    public Inventory createInventory(Recipe recipe) {
        return null;
    }

    @Override
    public List<Integer> getMaterialSlots() {
        return materialSlots;
    }

    @Override
    public List<Integer> getProductSlots() {
        return productSlots;
    }

    @Override
    public void handleInventoryOpen(InventoryOpenEvent event) {

    }

    @Override
    public void handleInventoryClose(InventoryCloseEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (!(holder instanceof SimpleCraftHolder)) {
            return;
        }
        if (holder instanceof CreateRecipeHolder) {
            ItemStack[] materials = new ItemStack[getMaterialSlots().size()];
            for (int i = 0; i < materials.length; ++i) {
                materials[i] = event.getInventory().getItem(getMaterialSlots().get(i));
            }
            ItemStack[] products = new ItemStack[getProductSlots().size()];
            for (int i = 0; i < products.length; ++i) {
                products[i] = event.getInventory().getItem(getProductSlots().get(i));
            }

            boolean ordered = ((CreateRecipeHolder) holder).isOrdered();
            if (ordered) {
                OrderedRecipe test = new OrderedRecipe("test");
                Pair<String, Map<String, ItemStack>> stringMapPair = OrderedRecipe.spawnShape(materials, false);
                test.setMaterials(stringMapPair.getFirst(), stringMapPair.getSecond());
                test.setProducts(products);
                MultiBlockCraftExtension.getExtension().getRecipeService().createRecipe(test);
            } else {
                UnorderedRecipe recipe = new UnorderedRecipe("test");
                recipe.setMaterials(materials);
                recipe.setProducts(products);
                MultiBlockCraftExtension.getExtension().getRecipeService().createRecipe(recipe);
            }
            List<String> availableRecipeIds = getAvailableRecipeIds();
            availableRecipeIds.add("test");
            setRecipeIds(availableRecipeIds);
            return;
        }


        SimpleCraftHolder simpleCraftHolder = (SimpleCraftHolder) holder;
        List<Integer> slots = simpleCraftHolder.isCrafted() ? getProductSlots() : getMaterialSlots();
        for (int slot : slots)  {
            ItemStack item = event.getInventory().getItem(slot);
            if (item != null) {
                addItemToPlayer((Player) event.getPlayer(), item);
            }
        }
    }

    private void addItemToPlayer(Player player, ItemStack itemStack) {
        player.getInventory().addItem(itemStack).forEach((k, v) -> {
            player.getWorld().dropItem(player.getLocation(), v);
        });
    }

    @Override
    public void handleInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (event.getInventory().getHolder() instanceof SimpleCraftHolder) {
            if (event.getClick() == ClickType.DOUBLE_CLICK ||
                    event.getClick() == ClickType.SHIFT_LEFT ||
                    event.getClick() == ClickType.SHIFT_RIGHT) {
                event.setCancelled(true);
                event.setResult(Event.Result.DENY);
                player.updateInventory();
                return;
            }
        }
        if (event.getClickedInventory() == null) return;
        InventoryHolder holder = event.getClickedInventory().getHolder();
        if (!(holder instanceof SimpleCraftHolder)) {
            return;
        }
        if (event.getSlotType() == InventoryType.SlotType.OUTSIDE) return;
        int slot = event.getSlot();
        if (!getMaterialSlots().contains(slot) && !getProductSlots().contains(slot)) {
            event.setCancelled(true);
            event.setResult(Event.Result.DENY);
            player.updateInventory();
            return;
        }
        if (holder instanceof CreateRecipeHolder) {
            return;
        }

        SimpleCraftHolder simpleCraftHolder = (SimpleCraftHolder) holder;
        if (getProductSlots().contains(slot)) {
            boolean takeProduct = false;
            if (simpleCraftHolder.isCrafted()) {
                takeProduct = true;
            } else if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR && simpleCraftHolder.takeItems()) {
                simpleCraftHolder.setCrafted(true);
                takeProduct = true;
            } else {
                event.setCancelled(true);
                event.setResult(Event.Result.DENY);
                player.updateInventory();
                for (Integer productSlot : getProductSlots()) {
                    event.getInventory().setItem(productSlot, null);
                }
            }
            if (takeProduct) {
                boolean finished = true;
                for (Integer productSlot : getProductSlots()) {
                    ItemStack item = event.getInventory().getItem(productSlot);
                    if (item != null && item.getType() != Material.AIR) {
                        finished = false;
                        break;
                    }
                }
                if (finished) {
                    simpleCraftHolder.reset(player);
                    if (simpleCraftHolder.findRecipe(-1, null)) {
                        ItemStack[] products = simpleCraftHolder.getRecipe().getProducts();
                        for (int i = 0; i < products.length; ++i) {
                            event.getInventory().setItem(getProductSlots().get(i), products[i]);
                        }
                    } else {
                        for (Integer productSlot : getProductSlots()) {
                            event.getInventory().setItem(productSlot, null);
                        }
                    }
                }
            }
            return;
        }

        if (getMaterialSlots().contains(slot)) {
            if (simpleCraftHolder.isCrafted()) {
                event.setCancelled(true);
                event.setResult(Event.Result.DENY);
                player.updateInventory();
                return;
            }
        }
        ItemStack cursor = event.getCursor();
        if (event.getClick() == ClickType.RIGHT) {
            cursor = cursor.clone();
            cursor.setAmount(1);
        }

        if (simpleCraftHolder.findRecipe(slot, cursor)) {
            ItemStack[] products = simpleCraftHolder.getRecipe().getProducts();
            for (int i = 0; i < products.length; ++i) {
                event.getInventory().setItem(getProductSlots().get(i), products[i]);
            }
        } else {
            for (Integer productSlot : getProductSlots()) {
                event.getInventory().setItem(productSlot, null);
            }
        }

    }

    @Override
    public void handleInventoryDrag(InventoryDragEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (!(holder instanceof SimpleCraftHolder)) {
            return;
        }
        for (Integer rawSlot : event.getRawSlots()) {
            if (!getMaterialSlots().contains(rawSlot)) {
                event.setCancelled(true);
                event.setResult(Event.Result.DENY);
                ((Player) event.getWhoClicked()).updateInventory();
            }
        }
    }

    private void createUnTimeRecipe() {

    }
}
