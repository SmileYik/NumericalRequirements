package org.eu.smileyik.numericalrequirements.test.item;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.eu.smileyik.numericalrequirements.core.api.NumericalRequirements;
import org.eu.smileyik.numericalrequirements.core.item.ItemService;
import org.eu.smileyik.numericalrequirements.test.NeedTest;

import java.util.Collection;

@NeedTest
public class ItemSerializationTest {
    @NeedTest
    public void itemDeserializationTest() {
        ItemService itemService = NumericalRequirements.getInstance().getItemService();
        Collection<String> itemIds = itemService.getItemIds();
        System.out.println(itemIds);
        System.out.println(itemService.loadItem("id", 1));
    }

    @NeedTest
    public void copyItemTest() {
        ItemService itemService = NumericalRequirements.getInstance().getItemService();
        Collection<String> itemIds = itemService.getItemIds();
        System.out.println(itemIds);
        ItemStack itemStack = itemService.loadItem("id", 1);
        System.out.println(itemStack);
        itemService.storeItem("id2", itemStack);
        System.out.println(itemIds);
        ItemStack itemStack2 = itemService.loadItem("id2", 1);
        System.out.println(itemStack2);
    }

}
