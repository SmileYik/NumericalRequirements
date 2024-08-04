package org.eu.smileyik.numericalrequirements.core.item.serialization.entry;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.eu.smileyik.numericalrequirements.core.api.item.ItemSerializerEntry;
import org.eu.smileyik.numericalrequirements.core.api.util.ConfigurationHashMap;
import org.eu.smileyik.numericalrequirements.debug.DebugLogger;
import org.eu.smileyik.numericalrequirements.reflect.MySimpleReflect;
import org.eu.smileyik.numericalrequirements.reflect.ReflectClass;
import org.eu.smileyik.numericalrequirements.reflect.ReflectClassPathBuilder;

public class HideTooltipEntry implements ItemSerializerEntry {
    final boolean flag;
    private ReflectClass itemMetaClass;

    public HideTooltipEntry() {
        boolean flag1 = true;
        try {
            itemMetaClass = MySimpleReflect.getReflectClass(new ReflectClassPathBuilder()
                            .newGroup("org.bukkit.inventory.meta.ItemMeta#")
                            .append("isHideTooltip()")
                            .append("setHideTooltip(boolean)")
                            .endGroup()
                            .finish());
        } catch (NoSuchFieldException | ClassNotFoundException | NoSuchMethodException e) {
            DebugLogger.debug(e);
            flag1 = false;
        }
        flag = flag1;
    }

    @Override
    public String getId() {
        return "hide-tooltip";
    }

    @Override
    public String getKey() {
        return null;
    }

    @Override
    public boolean isAvailable() {
        return flag;
    }

    @Override
    public void serialize(Handler handler, ConfigurationHashMap section, ItemStack itemStack, ItemMeta itemMeta) {
        boolean flag = (boolean) itemMetaClass.execute("isHideTooltip", itemMeta);
        if (flag) section.put("hide-tooltip", true);
    }

    @Override
    public ItemStack deserialize(Handler handler, ConfigurationHashMap section, ItemStack itemStack, ItemMeta itemMeta) {
        if (section.contains("hide-tooltip")) {
            itemMetaClass.execute("setHideTooltip", itemMeta, section.getBoolean("hide-tooltip"));
        }
        return null;
    }
}
