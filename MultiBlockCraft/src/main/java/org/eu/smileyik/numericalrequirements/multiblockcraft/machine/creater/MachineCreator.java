package org.eu.smileyik.numericalrequirements.multiblockcraft.machine.creater;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.BlockVector;
import org.eu.smileyik.numericalrequirements.core.I18N;
import org.eu.smileyik.numericalrequirements.core.api.Msg;
import org.eu.smileyik.numericalrequirements.core.api.item.ItemService;
import org.eu.smileyik.numericalrequirements.core.api.util.YamlUtil;
import org.eu.smileyik.numericalrequirements.debug.DebugLogger;
import org.eu.smileyik.numericalrequirements.multiblockcraft.MultiBlockCraftExtension;
import org.eu.smileyik.numericalrequirements.multiblockcraft.SimpleItem;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.Machine;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.MultiBlockMachine;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.holder.CraftHolder;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.impl.SimpleCraftTable;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.impl.SimpleMultiBlockMachine;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.impl.SimpleStorableCraftTable;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.impl.SimpleTimeCraftTable;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.item.InvItem;
import org.eu.smileyik.numericalrequirements.multiblockcraft.multiblock.MultiBlockFace;
import org.eu.smileyik.numericalrequirements.multiblockcraft.multiblock.structure.MultiBlockStructureMainBlock;
import org.eu.smileyik.numericalrequirements.multiblockcraft.multiblock.structure.StructureMainBlock;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.Recipe;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class MachineCreator implements Machine {
    private static final ItemStack INPUT_PLACEHOLDER;
    private static final ItemStack OUTPUT_PLACEHOLDER;
    private static final ItemStack EMPTY_PLACEHOLDER;
    private static final Map<String, ChatInteract.Step> STEPS = new HashMap<>();
    private static final List<Class<? extends Machine>> MACHINES = Arrays.asList(
            SimpleCraftTable.class, SimpleStorableCraftTable.class,
            SimpleTimeCraftTable.class, SimpleMultiBlockMachine.class
    );

    static {
        INPUT_PLACEHOLDER = createItem(I18N.tr("extension.multi-block-craft.machine-creator.input-placeholder-name"));
        OUTPUT_PLACEHOLDER = createItem(I18N.tr("extension.multi-block-craft.machine-creator.output-placeholder-name"));
        EMPTY_PLACEHOLDER = createItem(I18N.tr("extension.multi-block-craft.machine-creator.empty-placeholder-name"));
        STEPS.put("set-id", ChatInteract.newSimpleStep("set-id", "set-name", "id"));
        STEPS.put("set-name", ChatInteract.newSimpleStep("set-name", "set-title", "name"));
        STEPS.put("set-title", ChatInteract.newSimpleStep("set-title", "set-machine-item", "title"));
        STEPS.put("set-machine-item", ((map, p, prefix, str) -> {
            if (str == null) return "set-machine-item";

            ItemStack item = p.getInventory().getItemInMainHand();
            if (item == null || item.getType() == Material.AIR) {
                Msg.trMsg(p, prefix + ".step.set-machine-item.not-item-in-hand");
                return "set-machine-item";
            }

            SimpleItem simpleItem = getSimpleItem(item);
            YamlConfiguration config = new YamlConfiguration();
            simpleItem.store(config);
            map.put("machine-item", config.saveToString());

            Msg.trMsg(p, prefix + ".step.set-machine-item.display");
            return "set-type";
        }));
        STEPS.put("set-type", ((map, p, prefix, str) -> {
            if (str == null) return "set-type";
            Class<? extends Machine> target = null;
            for (Class<? extends Machine> machine : MACHINES) {
                if (machine.getSimpleName().equalsIgnoreCase(str)) {
                    target = machine;
                    break;
                }
            }
            if (target == null) {
                Msg.trMsg(p, prefix + ".step.set-type.not-valid-type", str);
                return "set-type";
            }
            map.put("type", target.getName());
            Msg.trMsg(p, prefix + ".step.set-type.display", target.getSimpleName());
            return MultiBlockMachine.class.isAssignableFrom(target) ? "set-structure" : null;
        }));
        STEPS.put("set-structure", (map, p, prefix, str) -> {
            if (str == null) return "set-structure";
            if (str.equalsIgnoreCase("ok")) {
                if (!map.containsKey("distance")) {
                    Msg.trMsg(p, prefix + ".step.set-structure.not-find-block-first");
                    return "set-structure";
                }
                Block targetBlock = new Location(
                        MultiBlockCraftExtension.getInstance().getPlugin().getServer().getWorld(map.get("world")),
                        Integer.parseInt(map.get("x")),
                        Integer.parseInt(map.get("y")),
                        Integer.parseInt(map.get("z"))
                ).getBlock();
                BlockFace face = BlockFace.valueOf(map.get("face"));

                StructureMainBlock structure = StructureMainBlock.create(targetBlock, MultiBlockFace.getByFace(face)[0].getFaces(), new MultiBlockStructureMainBlock(), MultiBlockCraftExtension.getConfig().getInt("machine.max-structure-block", 81));
                YamlConfiguration config = new YamlConfiguration();
                structure.store(config);
                map.put("structure", config.saveToString());
                return "set-check-period";
            }
            int distance = 3;
            try {
                distance = Integer.parseInt(str);
            } catch (NumberFormatException ignore) { }
            Block targetBlock = p.getTargetBlock(null, distance);
            if (targetBlock == null || targetBlock.isEmpty() || targetBlock.isLiquid()) {
                map.remove("distance");
                Msg.trMsg(p, prefix + ".step.set-structure.not-find-block", distance);
                return "set-structure";
            }
            map.put("distance", String.valueOf(distance));
            map.put("world", targetBlock.getWorld().getName());
            map.put("x", String.valueOf(targetBlock.getLocation().getBlockX()));
            map.put("y", String.valueOf(targetBlock.getLocation().getBlockY()));
            map.put("z", String.valueOf(targetBlock.getLocation().getBlockZ()));
            BlockVector blockVector = p.getLocation().getDirection().normalize().toBlockVector();
            int x = -blockVector.getBlockX();
            int y = -blockVector.getBlockY();
            int z = -blockVector.getBlockZ();
            BlockFace target = BlockFace.EAST;
            for (BlockFace face : MultiBlockFace.getByFace(BlockFace.EAST)[0].getFaces()) {
                if (face.getModX() == x && face.getModY() == y && face.getModZ() == z) {
                    target = face;
                    break;
                }
            }
            map.put("face", target.name());

            Material type = targetBlock.getType();
            Msg.trMsg(p, prefix + ".step.set-structure.find-block", distance, type.name());
            return "set-structure";
        });
        STEPS.put("set-check-period", (map, p, prefix, str) -> {
            long time;
            try {
                time = Long.parseLong(str);
            } catch (NumberFormatException e) {
                Msg.trMsg(p, prefix + ".step.set-check-period.not-valid-time", str);
                return "set-check-period";
            }
            map.put("time", String.valueOf(time));
            return null;
        });
    }


    private final MultiBlockCraftExtension extension;
    private final Player player;
    private final int invSize;
    private Map<Integer, ItemStack> invItems;
    private ItemStack otherItem;
    private List<Integer> inputSlots, outputSlots, emptySlots;

    public MachineCreator(MultiBlockCraftExtension extension, Player player, int invSize) {
        this.extension = extension;
        this.player = player;
        this.invSize = invSize;
    }

    public void start() {
        Holder holder = new Holder(this);
        Inventory inventory = extension.getPlugin().getServer().createInventory(holder, invSize, I18N.tr("extension.multi-block-craft.machine-creator.title"));
        holder.setInventory(inventory);
        inventory.addItem(INPUT_PLACEHOLDER.clone(), OUTPUT_PLACEHOLDER.clone(), EMPTY_PLACEHOLDER.clone());
        player.openInventory(inventory);
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        List<Integer> inputSlots = new ArrayList<>();
        List<Integer> outputSlots = new ArrayList<>();
        List<Integer> emptySlots = new ArrayList<>();

        Inventory inventory = event.getInventory();
        Holder holder = (Holder) inventory.getHolder();
        Inventory inv = holder.getInventory();
        Map<ItemStack, Integer> countMap = new HashMap<>();
        int max = -1;
        ItemStack maxItem = null;
        for (int i = 0; i < invSize; i++) {
            ItemStack item = inv.getItem(i);
            if (item == null) {
                Msg.trMsg(player, "extension.multi-block-craft.machine-creator.inventory-not-full");
                return;
            }
            item.setAmount(1);
            if (INPUT_PLACEHOLDER.isSimilar(item)) {
                inputSlots.add(i);
                inv.setItem(i, null);
                continue;
            } else if (OUTPUT_PLACEHOLDER.isSimilar(item)) {
                outputSlots.add(i);
                inv.setItem(i, null);
                continue;
            } else if (EMPTY_PLACEHOLDER.isSimilar(item)) {
                emptySlots.add(i);
                inv.setItem(i, null);
                continue;
            }
            countMap.put(item, countMap.getOrDefault(item, 0) + 1);
            int count = countMap.get(item);
            if (maxItem == null || count > max) {
                maxItem = item;
                max = count;
            }
        }

        Map<Integer, ItemStack> invItems = new HashMap<>();
        for (int i = 0; i < invSize; i++) {
            ItemStack item = inv.getItem(i);
            if (item == null) continue;
            if (maxItem != null && maxItem.isSimilar(item)) continue;
            invItems.put(i, item);
        }

        this.invItems = invItems;
        this.inputSlots = inputSlots;
        this.outputSlots = outputSlots;
        this.emptySlots = emptySlots;
        this.otherItem = maxItem;

        new ChatInteract(STEPS, player, this::finishedCreate, "extension.multi-block-craft.machine-creator").start("set-id");
    }

    private void finishedCreate(Map<String, String> map) {
        YamlConfiguration config = new YamlConfiguration();
        config.set("type", map.get("type"));
        config.set("id", map.get("id"));
        config.set("name", map.get("name"));
        config.set("title", map.get("title"));
        config.set("inv-size", invSize);
        try {
            config.set("machine-item", YamlUtil.loadFromString(map.get("machine-item")));
            if (map.containsKey("structure")) {
                config.set("structure", YamlUtil.loadFromString(map.get("structure")));
            }
        } catch (InvalidConfigurationException e) {
            DebugLogger.debug(e);
            Msg.trMsg(player, "extension.multi-block-craft.machine-creator.failure");
            return;
        }
        config.set("input-slots", inputSlots);
        config.set("output-slots", outputSlots);
        config.set("empty-slots", emptySlots);
        getSimpleItem(otherItem).store(config.createSection("other-slots"));
        ConfigurationSection section = config.createSection("inv-items");
        invItems.forEach((idx, item) -> {
            getSimpleItem(item).store(section.createSection(idx.toString()));
        });
        File dir = new File(extension.getDataFolder(), "machines/" + map.get("id"));
        if (!dir.exists()) dir.mkdirs();
        File machine = new File(dir, "machine.yml");
        try {
            config.save(machine);
        } catch (IOException e) {
            DebugLogger.debug(e);
            Msg.trMsg(player, "extension.multi-block-craft.machine-creator.failure");
            return;
        }
        Msg.trMsg(player, "extension.multi-block-craft.machine-creator.success");
    }

    private static SimpleItem getSimpleItem(ItemStack item) {
        String itemId = ItemService.getItemId(item);
        if (itemId != null) {
            return new SimpleItem(SimpleItem.TYPE_ID, item, itemId, item.getAmount());
        } else {
            return new SimpleItem(SimpleItem.TYPE_BUKKIT, item);
        }
    }

    private static ItemStack createItem(String name) {
        ItemStack itemStack = new ItemStack(Material.APPLE);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(name);
        itemStack.setItemMeta(itemMeta);
        itemStack.setAmount(64);
        return itemStack;
    }


    private static final class Holder implements CraftHolder {
        private final Machine machine;
        private Inventory inventory;

        private Holder(Machine machine) {
            this.machine = machine;
        }

        public void setInventory(Inventory inventory) {
            this.inventory = inventory;
        }

        @Override
        public Machine getMachine() {
            return machine;
        }

        @Override
        public String getIdentifier() {
            return "";
        }

        @Override
        public Inventory getInventory() {
            return inventory;
        }
    }

    @Override
    public String getId() {
        return "";
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public String getTitle() {
        return "";
    }

    @Override
    public List<Integer> getInputSlots() {
        return Collections.emptyList();
    }

    @Override
    public List<Integer> getOutputSlots() {
        return Collections.emptyList();
    }

    @Override
    public List<Integer> getEmptySlots() {
        return Collections.emptyList();
    }

    @Override
    public ItemStack getMachineItem() {
        return null;
    }

    @Override
    public Collection<Recipe> getRecipes() {
        return Collections.emptyList();
    }

    @Override
    public void addRecipe(Recipe recipe) {

    }

    @Override
    public Recipe findRecipe(String id) {
        return null;
    }

    @Override
    public Recipe findRecipe(ItemStack[] inputs) {
        return null;
    }

    @Override
    public Map<Integer, InvItem> getFuncItems() {
        return Collections.emptyMap();
    }
}
