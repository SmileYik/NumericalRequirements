package org.eu.smileyik.numericalrequirements.multiblockcraft.machine.tag;

import org.bukkit.inventory.ItemStack;
import org.eu.smileyik.numericalrequirements.core.I18N;
import org.eu.smileyik.numericalrequirements.core.api.item.tag.nbt.NBTTag;
import org.eu.smileyik.numericalrequirements.nms.nbt.NBTTagCompound;
import org.eu.smileyik.numericalrequirements.nms.nbt.NBTTagTypeId;
import org.eu.smileyik.numericalrequirements.nms.nbtitem.NBTItem;
import org.eu.smileyik.numericalrequirements.nms.nbtitem.NBTItemHelper;

import java.util.List;

public class MachineNBTTag extends NBTTag<String> {
    @Override
    public String getValue(NBTTagCompound tagCompound) {
        if (tagCompound.hasKeyOfType(getId(), NBTTagTypeId.STRING)) {
            return tagCompound.getString(getId());
        }
        return null;
    }

    @Override
    public boolean isValidValue(List<String> value) {
        return !value.isEmpty();
    }

    @Override
    public void setValue(ItemStack itemStack, List<String> value) {
        NBTItem item = NBTItemHelper.cast(itemStack);
        if (item == null) return;
        NBTTagCompound tag = item.getTag();
        tag.setString(getId(), value.get(0));
        itemStack.setItemMeta(item.getItemStack().getItemMeta());
    }

    @Override
    public String getId() {
        return "nreq-machine";
    }

    @Override
    public String getName() {
        return I18N.tr("extension.multi-block-craft.tag.machine.name");
    }

    @Override
    public String getDescription() {
        return I18N.tr("extension.multi-block-craft.tag.machine.description");
    }
}
