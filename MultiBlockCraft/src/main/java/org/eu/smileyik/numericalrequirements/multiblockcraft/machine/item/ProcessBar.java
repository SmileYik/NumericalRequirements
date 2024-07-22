package org.eu.smileyik.numericalrequirements.multiblockcraft.machine.item;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.data.MachineData;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.data.impl.SimpleUpdatableMachineData;
import org.eu.smileyik.numericalrequirements.reflect.MySimpleReflect;
import org.eu.smileyik.numericalrequirements.reflect.ReflectClass;
import org.eu.smileyik.numericalrequirements.reflect.ReflectClassPathBuilder;

import java.text.MessageFormat;

public class ProcessBar implements InvItem {
    private String name;
    private String disableName;
    private int start, end;

    @Override
    public ItemStack update(ItemStack item, MachineData data) {
        SimpleUpdatableMachineData d = (SimpleUpdatableMachineData) data;
        double totalTime = d.getTotalTime();
        double rate = totalTime == 0 ? 0 : d.getCraftedTime() / totalTime;
        int i = (int) Math.round(rate * (end - start) + start);
        ItemStack itemS = Util.setCustomModelData(item, i);
        ItemMeta meta = itemS.getItemMeta();
        if (rate == 0) {
            meta.setDisplayName(disableName);
        } else {
            meta.setDisplayName(MessageFormat.format(name, String.format("%.0f", rate * 100)));
        }
        itemS.setItemMeta(meta);
        return itemS;
    }

    @Override
    public void load(ConfigurationSection section) {
        disableName = section.getString("disable-name");
        name = section.getString("name");
        start = section.getInt("start");
        end = section.getInt("end");
    }

    private static class Util {
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
}
