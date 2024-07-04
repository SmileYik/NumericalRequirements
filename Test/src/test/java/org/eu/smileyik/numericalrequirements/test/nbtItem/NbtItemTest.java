package org.eu.smileyik.numericalrequirements.test.nbtItem;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.eu.smileyik.numericalrequirements.nms.nbtitem.NBTItem;
import org.eu.smileyik.numericalrequirements.nms.nbtitem.NBTItemHelper;
import org.eu.smileyik.numericalrequirements.test.NeedTest;

@NeedTest
public class NbtItemTest {
    @NeedTest
    public void itemCastTest() throws ClassNotFoundException {
        ItemStack item = new ItemStack(Material.APPLE);
        NBTItem cast = NBTItemHelper.cast(item);
        YamlConfiguration configuration = new YamlConfiguration();
        configuration.set("item", item);
        System.out.println(configuration.saveToString());
        System.out.println("aaaaaaaaaaaaaaaaaaa");
        System.out.println("sssssssssssssssssssssss");
    }
}
