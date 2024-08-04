package org.eu.smileyik.numericalrequirements.core.item.serialization.entry;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.eu.smileyik.numericalrequirements.core.api.util.ConfigurationHashMap;
import org.eu.smileyik.numericalrequirements.reflect.ReflectClass;
import org.eu.smileyik.numericalrequirements.reflect.builder.ReflectClassBuilder;

/**
 * ItemMeta: org.bukkit.inventory.meta.AxolotlBucketMeta
 */
public class AxolotlBucketEntry extends AbstractReflectItemSerializerEntry {
    private static final String ENUM_VARIANT_CLASS_NAME = "org.bukkit.entity.Axolotl$Variant";
    private Class<? extends Enum> ENUM_VARIANT_CLASS;

    @Override
    protected ReflectClass init() throws Throwable {
        ENUM_VARIANT_CLASS = (Class<? extends Enum>) Class.forName(ENUM_VARIANT_CLASS_NAME);
        return new ReflectClassBuilder("org.bukkit.inventory.meta.AxolotlBucketMeta")
                .method("getVariant").args()
                .method("hasVariant").args()
                .method("setVariant").args(ENUM_VARIANT_CLASS_NAME)
                .toClass();
    }

    @Override
    public String getKey() {
        return null;
    }

    @Override
    public String getId() {
        return "axolotl-bucket";
    }

    @Override
    public void serialize(Handler handler, ConfigurationHashMap section, ItemStack itemStack, ItemMeta itemMeta) {
        if (!clazz.getMainClass().isAssignableFrom(itemMeta.getClass())) return;
        if ((boolean) clazz.execute("hasVariant", itemMeta)) {
            Object execute = clazz.execute("getVariant", itemMeta);
            section.put(getId(), execute.toString());
        }
    }

    @Override
    public ItemStack deserialize(Handler handler, ConfigurationHashMap section, ItemStack itemStack, ItemMeta itemMeta) {
        if (!clazz.getMainClass().isAssignableFrom(itemMeta.getClass())) return null;
        if (section.containsKey(getId())) {
            Enum<?> anEnum = Enum.valueOf(ENUM_VARIANT_CLASS, section.getString(getId()).toUpperCase());
            clazz.execute("setVariant", itemMeta, anEnum);
        }
        return null;
    }
}
