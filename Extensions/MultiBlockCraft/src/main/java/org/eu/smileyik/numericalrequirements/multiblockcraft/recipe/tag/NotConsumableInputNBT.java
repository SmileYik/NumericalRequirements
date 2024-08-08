package org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.tag;

import org.bukkit.inventory.ItemStack;
import org.eu.smileyik.numericalrequirements.core.I18N;
import org.eu.smileyik.numericalrequirements.core.api.item.tag.nbt.NBTTag;
import org.eu.smileyik.numericalrequirements.nms.nbt.NBTTagCompound;
import org.eu.smileyik.numericalrequirements.nms.nbt.NBTTagTypeId;
import org.eu.smileyik.numericalrequirements.nms.nbtitem.NBTItem;
import org.eu.smileyik.numericalrequirements.nms.nbtitem.NBTItemHelper;

import java.util.List;

public class NotConsumableInputNBT extends NBTTag<Boolean> {
    @Override
    public Boolean getValue(NBTTagCompound tagCompound) {
        return tagCompound.hasKeyOfType(getId(), NBTTagTypeId.BYTE) ? tagCompound.getBoolean(getId()) : null;
    }

    @Override
    public boolean isValidValue(List<String> value) {
        if (value.size() != 1) return false;
        try {
            Boolean.parseBoolean(value.get(0));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void setValue(ItemStack itemStack, List<String> value) {
        NBTItem cast = NBTItemHelper.cast(itemStack);
        if (cast == null) return;
        cast.getTag().setBoolean(getId(), Boolean.parseBoolean(value.get(0)));
        itemStack.setItemMeta(cast.getItemStack().getItemMeta());
    }

    @Override
    public String getId() {
        return "nreq-recipe-notcons";
    }

    @Override
    public String getName() {
        return I18N.tr("extension.multi-block-craft.tag.recipe-not-consumable.name");
    }

    @Override
    public String getDescription() {
        return I18N.tr("extension.multi-block-craft.tag.recipe-not-consumable.description");
    }
}
