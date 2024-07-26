package org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.tag;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.eu.smileyik.numericalrequirements.core.I18N;
import org.eu.smileyik.numericalrequirements.core.api.item.tag.nbt.DisplayableNBTTag;
import org.eu.smileyik.numericalrequirements.multiblockcraft.MultiBlockCraftExtension;
import org.eu.smileyik.numericalrequirements.nms.nbt.NBTTagCompound;
import org.eu.smileyik.numericalrequirements.nms.nbt.NBTTagTypeId;
import org.eu.smileyik.numericalrequirements.nms.nbtitem.NBTItem;
import org.eu.smileyik.numericalrequirements.nms.nbtitem.NBTItemHelper;

import java.util.List;

public class DurabilityNBT extends DisplayableNBTTag<Integer> {

    public DurabilityNBT() {
        super(ChatColor.translateAlternateColorCodes('&', MultiBlockCraftExtension.getConfig().getString("recipe-tool.tag.nreq-tool-durability")));
    }

    @Override
    public Integer getValue(NBTTagCompound tagCompound) {
        return tagCompound.hasKeyOfType(getId(), NBTTagTypeId.INT) ? tagCompound.getInt(getId()) : null;
    }

    @Override
    public boolean isValidValue(List<String> value) {
        if (value.size() == 1) {
            try {
                Integer.parseInt(value.get(0));
                return true;
            } catch (NumberFormatException ignore) {

            }
        }
        return false;
    }

    @Override
    public void setValue(ItemStack itemStack, List<String> value) {
        int i = Integer.parseInt(value.get(0));
        setValue(itemStack, i);
    }

    @Override
    public void setValue(ItemStack itemStack, Integer value) {
        NBTItem cast = NBTItemHelper.cast(itemStack);
        if (cast == null) return;
        NBTTagCompound tag = cast.getTag();
        tag.setInt(getId(), value);
        itemStack.setItemMeta(cast.getItemStack().getItemMeta());
    }

    @Override
    public String getId() {
        return "nreq-tool-durability";
    }

    @Override
    public String getName() {
        return I18N.tr("extension.multi-block-craft.tag.tool-durability.name");
    }

    @Override
    public String getDescription() {
        return I18N.tr("extension.multi-block-craft.tag.tool-durability.description");
    }
}
