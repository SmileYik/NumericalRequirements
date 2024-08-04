package org.eu.smileyik.numericalrequirements.multiblockcraft;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.eu.smileyik.numericalrequirements.core.api.NumericalRequirements;
import org.eu.smileyik.numericalrequirements.core.api.util.YamlUtil;

public class SimpleItem {
    public static final String TYPE_EMPTY = "empty";
    public static final String TYPE_ID = "id";
    public static final String TYPE_BUKKIT = "bukkit";
    public static final String TYPE_NREQ = "nreq";

    private final String type;
    private final ItemStack itemStack;

    private String id;
    private int amount = 1;

    public SimpleItem(String type, ItemStack itemStack) {
        this.type = type;
        this.itemStack = itemStack;
    }

    public SimpleItem(String type, ItemStack itemStack, String id, int amount) {
        this.type = type;
        this.itemStack = itemStack;
        this.id = id;
        this.amount = amount;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void store(ConfigurationSection section) {
        section.set("type", type);
        if (type.equals(TYPE_BUKKIT)) {
            section.set("item", itemStack);
        } else if (type.equals(TYPE_NREQ)) {
            section.set("item", YamlUtil.fromMap(NumericalRequirements.getInstance().getItemService().getItemKeeper().storeItem(itemStack)));
            if (itemStack != null) section.set("amount", itemStack.getAmount());
        } else if (type.equals(TYPE_ID)) {
            section.set("id", id);
            section.set("amount", amount);
        } else {
        }
    }

    public static SimpleItem load(ConfigurationSection section) {
        String type = section.getString("type");
        if (type.equalsIgnoreCase(TYPE_ID)) {
            return loadByID(section);
        } else if (type.equalsIgnoreCase(TYPE_BUKKIT)) {
            return loadByBukkit(section);
        } else if (type.equalsIgnoreCase(TYPE_NREQ)) {
            return loadByNReq(section);
        } else {
            return new SimpleItem(TYPE_EMPTY, null);
        }
    }

    private static SimpleItem loadByID(ConfigurationSection section) {
        return new SimpleItem(
                TYPE_ID,
                NumericalRequirements.getInstance().getItemService().getItemKeeper().loadItem(
                        section.getString("id"),
                        section.getInt("amount", 1)
                ),
                section.getString("id"),
                section.getInt("amount")
        );
    }

    private static SimpleItem loadByBukkit(ConfigurationSection section) {
        return new SimpleItem(
                TYPE_BUKKIT,
                section.getItemStack("item")
        );
    }

    private static SimpleItem loadByNReq(ConfigurationSection section) {
        return new SimpleItem(
                TYPE_NREQ,
                NumericalRequirements.getInstance().getItemService().getItemKeeper().loadItemFromYaml(
                        section.getConfigurationSection("item"),
                        section.getInt("amount", 1)
                )
        );
    }
}
