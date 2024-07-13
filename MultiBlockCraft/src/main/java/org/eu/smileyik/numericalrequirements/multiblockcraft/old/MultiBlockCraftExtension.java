package org.eu.smileyik.numericalrequirements.multiblockcraft.old;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.eu.smileyik.numericalrequirements.core.api.extension.Extension;
import org.eu.smileyik.numericalrequirements.core.api.extension.ExtensionTask;
import org.eu.smileyik.numericalrequirements.multiblockcraft.old.inventory.SimpleCraftInventory;
import org.eu.smileyik.numericalrequirements.multiblockcraft.old.inventory.listener.SimpleCraftInventoryListener;
import org.eu.smileyik.numericalrequirements.multiblockcraft.old.recipe.RecipeService;
import org.eu.smileyik.numericalrequirements.multiblockcraft.old.recipe.YamlRecipeService;

import java.util.ArrayList;

public class MultiBlockCraftExtension extends Extension {
    private static MultiBlockCraftExtension extension;
    private RecipeService recipeService;
    private SimpleCraftInventory simpleCraftInventory = new SimpleCraftInventory("aaa");

    @Override
    public void onEnable() {
        extension = this;
        recipeService = new YamlRecipeService(this);
        recipeService.loadRecipes();
        getPlugin().getServer().getPluginManager().registerEvents(new SimpleCraftInventoryListener(), getPlugin());
        getApi().getExtensionService().registerTask(new ExtensionTask() {
            @Override
            public String getId() {
                return "test-inv";
            }

            @Override
            public String getName() {
                return "aaa";
            }

            @Override
            public String getDescription() {
                return "aaa";
            }

            @Override
            public Extension getExtension() {
                return extension;
            }

            @Override
            public void run(CommandSender sender, String[] args) {
                if (args.length != 0) {
                    if (args[0].equals("true")) {
                        Inventory inv = simpleCraftInventory.createCreateInventory(args.length != 2);
                        ((Player) sender).openInventory(inv);
                    }
                } else {
                    Inventory inventory = simpleCraftInventory.createInventory();
                    ((Player) sender).openInventory(inventory);
                }
            }
        });
        getPlugin().getServer().getScheduler().runTaskLaterAsynchronously(
                getPlugin(), () -> {
                    ArrayList<String> objects = new ArrayList<>();
                    objects.add("test");
                    simpleCraftInventory.setRecipeIds(new ArrayList<>(objects));
                },
                20
        );

    }

    public static MultiBlockCraftExtension getExtension() {
        return extension;
    }

    public RecipeService getRecipeService() {
        return recipeService;
    }

    public SimpleCraftInventory getSimpleCraftInventory() {
        return simpleCraftInventory;
    }
}
