package org.eu.smileyik.numericalrequirements.core.item.serialization.entry;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.eu.smileyik.numericalrequirements.core.api.item.ItemSerializer;
import org.eu.smileyik.numericalrequirements.core.api.util.ConfigurationHashMap;
import org.eu.smileyik.numericalrequirements.reflect.ReflectClass;
import org.eu.smileyik.numericalrequirements.reflect.builder.ReflectClassBuilder;

public abstract class AbstractSimpleReflectItemSerializerEntry extends AbstractReflectItemSerializerEntry {
    @Override
    protected ReflectClass init() throws Throwable {
        return new ReflectClassBuilder(getClassName())
                .method(method("get")).args()
                .method(method("has")).args()
                .method(method("set")).args(getFieldType())
                .toClass();
    }

    protected abstract String getFieldName();
    protected abstract String getClassName();
    protected abstract String getFieldType();
    protected abstract Object getValue(ConfigurationHashMap section);
    protected abstract void setValue(ConfigurationHashMap section, Object value);

    protected Object getValue(ItemSerializer context, ConfigurationHashMap section) {
        return getValue(section);
    }

    protected void setValue(ItemSerializer context, ConfigurationHashMap section, Object value) {
        setValue(section, value);
    }

    private String method(String type) {
        return String.format("%s%s", type, getFieldName());
    }

    @Override
    public void serialize(ItemSerializer context, Handler handler, ConfigurationHashMap section, ItemStack itemStack, ItemMeta itemMeta) {
        if (!clazz.getMainClass().isAssignableFrom(itemMeta.getClass())) return;
        if ((boolean) clazz.execute(method("has"), itemMeta)) {
            Object execute = clazz.execute(method("get"), itemMeta);
            setValue(context, section, execute);
        }
    }

    @Override
    public ItemStack deserialize(ItemSerializer context, Handler handler, ConfigurationHashMap section, ItemStack itemStack, ItemMeta itemMeta) {
        if (!clazz.getMainClass().isAssignableFrom(itemMeta.getClass())) return null;
        if (getKey() != null && section.isMap(getKey()) || section.containsKey(getId())) {
            clazz.execute(method("set"), itemMeta, getValue(context, section));
        }
        return null;
    }

    @Override
    public ItemStack deserialize(Handler handler, ConfigurationHashMap section, ItemStack itemStack, ItemMeta itemMeta) {
        return null;
    }

    @Override
    public void serialize(Handler handler, ConfigurationHashMap section, ItemStack itemStack, ItemMeta itemMeta) {

    }

    @Override
    public String getKey() {
        return null;
    }
}
