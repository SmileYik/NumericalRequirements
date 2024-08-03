package org.eu.smileyik.numericalrequirements.test.item;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.inventory.ItemStack;
import org.eu.smileyik.numericalrequirements.core.api.NumericalRequirements;
import org.eu.smileyik.numericalrequirements.core.api.item.ItemService;
import org.eu.smileyik.numericalrequirements.core.api.util.YamlUtil;
import org.eu.smileyik.numericalrequirements.core.item.serialization.JsonItemSerializer;
import org.eu.smileyik.numericalrequirements.test.NeedTest;

import java.util.Collection;

@NeedTest
public class ItemSerializationTest {
    @NeedTest
    public void itemDeserializationTest() {
        ItemService itemService = NumericalRequirements.getInstance().getItemService();
        itemService.reloadItems();
        Collection<String> itemIds = itemService.getItemIds();
        System.out.println(itemIds);
        System.out.println(itemService.loadItem("id", 1));
    }

    @NeedTest
    public void copyItemTest() {
        ItemService itemService = NumericalRequirements.getInstance().getItemService();
        itemService.reloadItems();
        Collection<String> itemIds = itemService.getItemIds();
        System.out.println(itemIds);
        ItemStack itemStack = itemService.loadItem("id", 1);
        System.out.println(itemStack);
        itemService.storeItem("id2", itemStack);
        System.out.println(itemIds);
        ItemStack itemStack2 = itemService.loadItem("id2", 1);
        System.out.println(itemStack2);
    }

    private ConfigurationSection getConfiguration() throws InvalidConfigurationException {
        return YamlUtil.loadFromString(
                "nbt:\n" +
                "  # 不序列化哪些 NBT 的键值\n" +
                "  ignore-keys:\n" +
                "    - \"CustomModelData\"\n" +
                "    - \"display\"\n" +
                "    - \"HideFlags\"");
    }

    private ConfigurationSection getConfigurationNotPretty() throws InvalidConfigurationException {
        return YamlUtil.loadFromString(
                "pretty-print: false\n" +
                "nbt:\n" +
                        "  # 不序列化哪些 NBT 的键值\n" +
                        "  ignore-keys:\n" +
                        "    - \"CustomModelData\"\n" +
                        "    - \"display\"\n" +
                        "    - \"HideFlags\"");
    }

    @NeedTest
    public String serializeItemToJsonTest() throws InvalidConfigurationException {
        JsonItemSerializer serializer = new JsonItemSerializer();
        serializer.configure(getConfigurationNotPretty());
        ItemService itemService = NumericalRequirements.getInstance().getItemService();
        itemService.reloadItems();
        ItemStack itemStack = itemService.loadItem("id", 1);
        System.out.println(itemStack);
        String json = serializer.serialize(itemStack);
        System.out.println(json);
        return json;
    }

    @NeedTest
    public void deserializeItemFromJsonTest() throws InvalidConfigurationException {
        JsonItemSerializer serializer = new JsonItemSerializer();
        serializer.configure(getConfigurationNotPretty());
        String s = serializeItemToJsonTest();
        ItemStack deserialize = serializer.deserialize(s);
        System.out.println(deserialize);
    }

    @NeedTest
    public String serializeItemToPrettyJsonTest() throws InvalidConfigurationException {
        JsonItemSerializer serializer = new JsonItemSerializer();
        serializer.configure(getConfiguration());
        ItemService itemService = NumericalRequirements.getInstance().getItemService();
        itemService.reloadItems();
        ItemStack itemStack = itemService.loadItem("id", 1);
        System.out.println(itemStack);
        String json = serializer.serialize(itemStack);
        System.out.println(json);
        return json;
    }

    @NeedTest
    public void deserializeItemFromPrettyJsonTest() throws InvalidConfigurationException {
        JsonItemSerializer serializer = new JsonItemSerializer();
        serializer.configure(getConfiguration());
        String s = serializeItemToPrettyJsonTest();
        ItemStack deserialize = serializer.deserialize(s);
        System.out.println(deserialize);
    }

}
