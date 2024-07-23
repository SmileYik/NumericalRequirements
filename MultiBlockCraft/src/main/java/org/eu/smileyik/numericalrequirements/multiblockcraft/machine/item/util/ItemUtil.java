package org.eu.smileyik.numericalrequirements.multiblockcraft.machine.item.util;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.eu.smileyik.numericalrequirements.debug.DebugLogger;
import org.eu.smileyik.numericalrequirements.reflect.MySimpleReflect;
import org.eu.smileyik.numericalrequirements.reflect.ReflectClass;
import org.eu.smileyik.numericalrequirements.reflect.ReflectClassPathBuilder;

public class ItemUtil {
    private static final ReflectClass itemMetaClass;

    static {
        ReflectClass aClass = null;
        try {
            aClass = MySimpleReflect.getReflectClass(new ReflectClassPathBuilder()
                    .newGroup("org.bukkit.inventory.meta.ItemMeta#")
                    .append("setCustomModelData(java.lang.Integer)")
                    .endGroup()
                    .finish());
        } catch (Exception e) {
            DebugLogger.debug("该服务端不支持设定CustomModelData");
            DebugLogger.debug(e);
        }
        itemMetaClass = aClass;
    }

    public static ItemStack setCustomModelData(ItemStack itemStack, int i) {
        if (itemMetaClass != null) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMetaClass.execute("setCustomModelData", itemMeta, i);
            itemStack.setItemMeta(itemMeta);
        } else {
            itemStack.setDurability((short) i);
        }
        return itemStack;
    }
}
