package org.eu.smileyik.numericalrequirements.multiblockcraft.machine.creater;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.eu.smileyik.numericalrequirements.core.I18N;
import org.eu.smileyik.numericalrequirements.core.api.Msg;
import org.eu.smileyik.numericalrequirements.core.api.NumericalRequirements;
import org.eu.smileyik.numericalrequirements.debug.DebugLogger;
import org.eu.smileyik.numericalrequirements.multiblockcraft.MultiBlockCraftExtension;
import org.eu.smileyik.numericalrequirements.multiblockcraft.SimpleItem;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.Machine;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.holder.CraftHolder;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.item.InvItem;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.Recipe;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.TimeRecipe;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.impl.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class RecipeCreator implements Listener, Machine {

    private static final Map<String, ChatInteract.Step> STEPS = new HashMap<>();
    private static final List<Class<? extends Recipe>> RECIPES = Arrays.asList(
            SimpleRecipe.class, SimpleOrderedRecipe.class,
            SimpleTimeRecipe.class, SimpleTimeOrderedRecipe.class
    );

    static {
        STEPS.put("set-id", (map, p, prefix, str) -> {
            if (str == null) return "set-id";
            map.put("id", str);
            Msg.trMsg(p, prefix + ".step.set-id.display", str);
            return "set-name";
        });
        STEPS.put("set-name", (map, p, prefix, str) -> {
            if (str == null) return "set-name";
            map.put("name", str);
            Msg.trMsg(p, prefix + ".step.set-name.display", str);
            return "choose-recipes";
        });
        STEPS.put("choose-recipes", (map, p, prefix, str) -> {
            if (str == null) return "choose-recipes";
            Class<? extends Recipe> target = null;
            for (Class<? extends Recipe> recipe : RECIPES) {
                if (recipe.getSimpleName().equalsIgnoreCase(str)) {
                    target = recipe;
                    break;
                }
            }
            if (target == null) {
                Msg.trMsg(p, prefix + ".step.choose-recipes.not-valid-recipe", str);
                return "choose-recipes";
            }
            map.put("type", target.getName());
            Msg.trMsg(p, prefix + ".step.choose-recipes.display", target.getSimpleName());
            return TimeRecipe.class.isAssignableFrom(target) ? "set-time" : null;
        });
        STEPS.put("set-time", (map, p, prefix, str) -> {
            if (str == null) return "set-time";
            try {
                Double.parseDouble(str);
                map.put("time", str);

                Msg.trMsg(p, prefix + ".step.set-time.display", str);
                return null;
            } catch (NumberFormatException e) {
                Msg.trMsg(p, prefix + ".step.set-time.not-valid-time", str);
                return "set-time";
            }
        });
    }

    private final MultiBlockCraftExtension extension;
    private final Machine machine;
    private final Player player;
    private SimpleItem[] inputs, outputs;

    public RecipeCreator(MultiBlockCraftExtension extension, Machine machine, Player player) {
        this.extension = extension;
        this.machine = machine;
        this.player = player;
    }

    public void open() {
        Inventory inv = machine.createGui();
        Holder holder = new Holder(this);
        Inventory gui = extension.getPlugin().getServer().createInventory(holder, inv.getSize(), I18N.tr("extension.multi-block-craft.recipe-creator.title", machine.getTitle(), machine.getName()));
        gui.setContents(inv.getContents());
        player.openInventory(gui);
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        HumanEntity player = event.getPlayer();
        Inventory inv = event.getInventory();

        boolean inputsAllEmpty = true;
        SimpleItem[] inputs = new SimpleItem[machine.getInputSlots().size()];
        int idx = 0;
        for (Integer inputSlot : machine.getInputSlots()) {
            ItemStack item = inv.getItem(inputSlot);
            String itemId = NumericalRequirements.getInstance().getItemService().getItemId(item);
            if (itemId != null) {
                inputs[idx] = new SimpleItem(SimpleItem.TYPE_ID, item, itemId, item.getAmount());
            } else {
                inputs[idx] = new SimpleItem(SimpleItem.TYPE_BUKKIT, item);
            }
            if (inputs[idx].getItemStack() != null) inputsAllEmpty = false;
            ++idx;
        }

        idx = 0;
        boolean outputsAllEmpty = true;
        SimpleItem[] outputs = new SimpleItem[machine.getOutputSlots().size()];
        for (Integer outputSlot : machine.getOutputSlots()) {
            ItemStack item = inv.getItem(outputSlot);
            String itemId = NumericalRequirements.getInstance().getItemService().getItemId(item);
            if (itemId != null) {
                outputs[idx] = new SimpleItem(SimpleItem.TYPE_ID, item, itemId, item.getAmount());
            } else {
                outputs[idx] = new SimpleItem(SimpleItem.TYPE_BUKKIT, item);
            }
            if (outputs[idx].getItemStack() != null) outputsAllEmpty = false;
            ++idx;
        }

        if (inputsAllEmpty && outputsAllEmpty) {
            Msg.trMsg(player, "extension.multi-block-craft.recipe-creator.stop");
            return;
        }

        this.inputs = inputs;
        this.outputs = outputs;

        new ChatInteract(
                STEPS, this.player, this::finishedCreate, "extension.multi-block-craft.recipe-creator"
        ).start("set-id");
    }

    private void finishedCreate(Map<String, String> map) {
        Recipe recipe = new SimpleAbstractRecipe() {
            {
                this.rawInputs = inputs;
                this.rawOutputs = outputs;
                this.id = map.getOrDefault("id", UUID.randomUUID().toString());
                this.name = map.getOrDefault("name", this.id);
            }
        };

        File file = MultiBlockCraftExtension.getInstance().getMachineService().createRecipe(machine.getId(), recipe);
        YamlConfiguration r = YamlConfiguration.loadConfiguration(file);
        r.set("type", map.getOrDefault("type", null));
        if (map.containsKey("time")) r.set("time", Double.parseDouble(map.get("time")));

        StringBuilder sb = new StringBuilder(r.saveToString());
        sb.insert(0, "\n");
        sb.insert(0, I18N.tr("extension.multi-block-craft.recipe-creator.recipe-head"));
        try {
            Files.write(file.toPath(), sb.toString().getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
            Msg.trMsg(player, "extension.multi-block-craft.recipe-creator.success", file);
        } catch (IOException e) {
            Msg.trMsg(player, "extension.multi-block-craft.recipe-creator.failure");
            DebugLogger.debug(e);
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
}
