package org.eu.smileyik.numericalrequirements.thirst;

import org.bukkit.inventory.ItemStack;
import org.eu.smileyik.numericalrequirements.core.I18N;
import org.eu.smileyik.numericalrequirements.core.api.element.ElementPlayer;
import org.eu.smileyik.numericalrequirements.core.api.element.data.Element;
import org.eu.smileyik.numericalrequirements.core.api.element.handler.ElementHandler;
import org.eu.smileyik.numericalrequirements.core.api.item.tag.ConsumableTag;
import org.eu.smileyik.numericalrequirements.core.api.item.tag.nbt.NBTTag;
import org.eu.smileyik.numericalrequirements.core.api.player.NumericalPlayer;
import org.eu.smileyik.numericalrequirements.nms.nbt.NBTTagCompound;
import org.eu.smileyik.numericalrequirements.nms.nbt.NBTTagTypeId;
import org.eu.smileyik.numericalrequirements.nms.nbtitem.NBTItem;
import org.eu.smileyik.numericalrequirements.nms.nbtitem.NBTItemHelper;

import java.util.List;

public class ThirstNBTTag extends NBTTag<Double> implements ConsumableTag<Double> {
    private final ThirstElement element;
    private final ElementHandler elementHandler;

    public ThirstNBTTag(ThirstElement element, ElementHandler elementHandler) {
        this.element = element;
        this.elementHandler = elementHandler;
    }

    @Override
    public Double getValue(NBTTagCompound tagCompound) {
        if (tagCompound.hasKeyOfType(getId(), NBTTagTypeId.DOUBLE)) {
            return tagCompound.getDouble(getId());
        }
        return null;
    }

    @Override
    public boolean isValidValue(List<String> value) {
        if (value == null || value.isEmpty()) return false;
        try {
            Double.parseDouble(value.get(0));
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    @Override
    public void setValue(ItemStack itemStack, List<String> value) {
        double v = Double.parseDouble(value.get(0));
        NBTItem item = NBTItemHelper.cast(itemStack);
        if (item == null) return;
        NBTTagCompound tag = item.getTag();
        tag.setDouble(getId(), v);
        itemStack.setItemMeta(item.getItemStack().getItemMeta());
    }

    @Override
    public String getId() {
        return "nreq-thirst";
    }

    @Override
    public String getName() {
        return I18N.tr("extension.thirst.tag.name");
    }

    @Override
    public String getDescription() {
        return I18N.tr("extension.thirst.tag.description");
    }

    @Override
    public void onConsume(NumericalPlayer player, Double value) {
        Element elementData = ElementPlayer.getElementData(player, element);
        assert elementData != null;
        ThirstData data = (ThirstData) elementData;
        data.calculateAndGet(value, Double::sum);
        elementHandler.handlePlayer(player, data);
    }
}
