package org.eu.smileyik.numericalrequirements.core.item.serialization.entry;

import com.google.common.collect.Multimap;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.eu.smileyik.numericalrequirements.core.api.item.ItemSerializerEntry;
import org.eu.smileyik.numericalrequirements.core.api.util.ConfigurationHashMap;
import org.eu.smileyik.numericalrequirements.debug.DebugLogger;
import org.eu.smileyik.numericalrequirements.reflect.MySimpleReflect;
import org.eu.smileyik.numericalrequirements.reflect.ReflectConstructor;
import org.eu.smileyik.numericalrequirements.reflect.ReflectMethod;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public abstract class AttributeEntry implements ItemSerializerEntry {
    protected ReflectMethod<String> enumName;

    protected ReflectMethod<Object> operationOf;
    protected ReflectMethod<Object> attributeOf;

    protected ReflectMethod<String> getName;
    protected ReflectMethod<Double> getAmount;
    protected ReflectMethod<Object> getOperation;

    protected ReflectMethod<Boolean> addAttributeModifier;
    protected ReflectMethod<Multimap<Object, Object>> getAttributeModifiers; // Multimap<Attribute,AttributeModifier>

    final boolean flag;

    public AttributeEntry() {
        boolean flag1 = true;
        try {
            enumName = MySimpleReflect.getForce("java.lang.Enum#name()");
            operationOf = MySimpleReflect.getForce("org.bukkit.attribute.AttributeModifier$Operation#valueOf(java.lang.String)");
            attributeOf = MySimpleReflect.getForce("org.bukkit.attribute.Attribute#valueOf(java.lang.String)");

            // newAttributeModifier = MySimpleReflect.getForce("org.bukkit.attribute.AttributeModifier(java.lang.String, double, org.bukkit.attribute.AttributeModifier$Operation)");
            getName = MySimpleReflect.getForce("org.bukkit.attribute.AttributeModifier#getName()");
            getAmount = MySimpleReflect.getForce("org.bukkit.attribute.AttributeModifier#getAmount()");
            getOperation = MySimpleReflect.getForce("org.bukkit.attribute.AttributeModifier#getOperation()");

            addAttributeModifier = MySimpleReflect.getForce("org.bukkit.inventory.meta.ItemMeta#addAttributeModifier(org.bukkit.attribute.Attribute, org.bukkit.attribute.AttributeModifier)");
            getAttributeModifiers = MySimpleReflect.getForce("org.bukkit.inventory.meta.ItemMeta#getAttributeModifiers()");
        } catch (NoSuchFieldException | ClassNotFoundException | NoSuchMethodException e) {
            DebugLogger.debug(e);
            flag1 = false;
        }
        flag = flag1;
    }

    @Override
    public boolean isAvailable() {
        return flag;
    }

    @Override
    public String getId() {
        return "attribute";
    }

    @Override
    public String getKey() {
        return "attribute";
    }

    @Override
    public void serialize(Handler handler, ConfigurationHashMap section, ItemStack itemStack, ItemMeta itemMeta) {
        Multimap<Object, Object> attributes = getAttributeModifiers.execute(itemMeta);
        if (attributes == null) return;

        attributes.entries().forEach(entry -> {
            ConfigurationHashMap attr = section.createMap(getName.execute(entry.getValue()));
            attr.put("attribute", enumName.execute(entry.getKey()));
            attr.put("operation", enumName.execute(getOperation.execute(entry.getValue())));
            attr.put("amount", getAmount.execute(entry.getValue()));
            serialize(handler, attr, entry, itemStack, itemMeta);
        });
    }

    protected abstract void serialize(Handler handler, ConfigurationHashMap section, Map.Entry<Object, Object> entry, ItemStack itemStack, ItemMeta itemMeta);

    @Override
    public ItemStack deserialize(Handler handler, ConfigurationHashMap section, ItemStack itemStack, ItemMeta itemMeta) {
        for (Map.Entry<String, Object> entry : section.entrySet()) {
            String name = entry.getKey();
            ConfigurationHashMap attr = (ConfigurationHashMap) entry.getValue();
            Object am = deserialize(handler, attr, name, attr.getDouble("amount"), attr.getString("operation"));
            if (handler.isDeny()) continue;
            addAttributeModifier.execute(itemMeta, attributeOf.execute(null, attr.getString("attribute")), am);
        }
        return null;
    }

    protected abstract Object deserialize(Handler handler, ConfigurationHashMap section, String name, double amount, String operation);

    public static class Attribute1Entry extends AttributeEntry {
        protected ReflectConstructor newAttributeModifier;

        final boolean flag;

        /*
            public AttributeModifier(@NotNull
                                     @NotNull String name,
                                     double amount,
                                     @NotNull
                                     @NotNull AttributeModifier.Operation operation)
         */
        public Attribute1Entry() {
            this(false);
        }

        protected Attribute1Entry(boolean skip) {
            super();
            boolean flag1 = true;
            if (!skip) {
                try {
                    newAttributeModifier = MySimpleReflect.getForce("org.bukkit.attribute.AttributeModifier(java.lang.String, double, org.bukkit.attribute.AttributeModifier$Operation)");
                } catch (NoSuchFieldException | ClassNotFoundException | NoSuchMethodException e) {
                    DebugLogger.debug(e);
                    flag1 = false;
                }
            }
            flag = flag1;
        }

        @Override
        protected void serialize(Handler handler, ConfigurationHashMap section, Map.Entry<Object, Object> entry, ItemStack itemStack, ItemMeta itemMeta) {

        }

        @Override
        protected Object deserialize(Handler handler, ConfigurationHashMap section, String name, double amount, String operation) {
            return newAttributeModifier.execute(name, amount, operationOf.execute(null, operation));
        }

        @Override
        public boolean isAvailable() {
            return super.isAvailable() && flag;
        }

        @Override
        public int getPriority() {
            return 4;
        }
    }

    public static class Attribute2Entry extends Attribute1Entry {
        protected ReflectMethod<UUID> getUniqueId;
        final boolean flag;

        /*
            AttributeModifier(@NotNull UUID uuid, @NotNull String name, double amount, @NotNull AttributeModifier.Operation operation)
         */
        public Attribute2Entry() {
            this(false);
        }

        public Attribute2Entry(boolean skip) {
            super(true);
            boolean flag1 = true;
            try {
                if (!skip) newAttributeModifier = MySimpleReflect.getForce("org.bukkit.attribute.AttributeModifier(java.util.UUID, java.lang.String, double, org.bukkit.attribute.AttributeModifier$Operation)");
                getUniqueId = MySimpleReflect.getForce("org.bukkit.attribute.AttributeModifier#getUniqueId()");
            } catch (NoSuchFieldException | ClassNotFoundException | NoSuchMethodException e) {
                DebugLogger.debug(e);
                flag1 = false;
            }
            flag = flag1;
        }

        @Override
        protected void serialize(Handler handler, ConfigurationHashMap section, Map.Entry<Object, Object> entry, ItemStack itemStack, ItemMeta itemMeta) {
            super.serialize(handler, section, entry, itemStack, itemMeta);
            UUID uuid;
            try {
                uuid = getUniqueId.execute(entry.getValue());
            } catch (Exception e) {
                uuid = UUID.fromString(getName.execute(entry.getValue()));
            }
            section.put("uuid", uuid.toString());
        }

        @Override
        protected Object deserialize(Handler handler, ConfigurationHashMap section, String name, double amount, String operation) {
            if (!section.contains("uuid")) {
                handler.deny();
                return null;
            }
            String uuid = section.getString("uuid");
            return newAttributeModifier.execute(UUID.fromString(uuid), name, amount, operationOf.execute(null, operation));
        }

        @Override
        public boolean isAvailable() {
            return super.isAvailable() && flag;
        }

        @Override
        public int getPriority() {
            return 3;
        }
    }

    public static class Attribute3Entry extends Attribute2Entry {
        final boolean flag;

        ReflectMethod<Object> equipmentSlotOf;
        ReflectMethod<Object> getEquipmentSlot;

        // AttributeModifier(@NotNull UUID uuid, @NotNull String name, double amount, @NotNull AttributeModifier.Operation operation, @Nullable EquipmentSlot slot)
        public Attribute3Entry() {
            super(true);
            boolean flag1 = true;
            try {
                newAttributeModifier = MySimpleReflect.getForce("org.bukkit.attribute.AttributeModifier(java.util.UUID, java.lang.String, double, org.bukkit.attribute.AttributeModifier$Operation, org.bukkit.inventory.EquipmentSlot)");
                equipmentSlotOf = MySimpleReflect.get("org.bukkit.inventory.EquipmentSlot#valueOf(java.lang.String)");
                getEquipmentSlot = MySimpleReflect.getForce("org.bukkit.attribute.AttributeModifier#getSlot()");
            } catch (NoSuchFieldException | ClassNotFoundException | NoSuchMethodException e) {
                DebugLogger.debug(e);
                flag1 = false;
            }
            flag = flag1;
        }

        @Override
        protected void serialize(Handler handler, ConfigurationHashMap section, Map.Entry<Object, Object> entry, ItemStack itemStack, ItemMeta itemMeta) {
            super.serialize(handler, section, entry, itemStack, itemMeta);
            Object slot = getEquipmentSlot.execute(entry.getValue());
            if (slot == null) {
                section.put("slot", null);
            } else {
                section.put("slot", enumName.execute(slot));
            }
        }

        @Override
        protected Object deserialize(Handler handler, ConfigurationHashMap section, String name, double amount, String operation) {
            if (!section.contains("slot")) {
                handler.deny();
                return null;
            }
            return newAttributeModifier.execute(
                    UUID.fromString(section.getString("uuid")),
                    name, amount, operationOf.execute(null, operation),
                    equipmentSlotOf.execute(null, section.getString("slot"))
            );
        }

        @Override
        public int getPriority() {
            return 2;
        }

        @Override
        public boolean isAvailable() {
            return super.isAvailable() && flag;
        }
    }

    public static class Attribute4Entry extends Attribute2Entry {
        final boolean flag;

        ReflectMethod<Object> getByName;
        ReflectMethod<Object> getSlotGroup;


        // AttributeModifier(@NotNull UUID uuid, @NotNull String name, double amount, @NotNull AttributeModifier.Operation operation, @NotNull EquipmentSlotGroup slot)
        public Attribute4Entry() {
            super(true);
            boolean flag1 = true;
            try {
                newAttributeModifier = MySimpleReflect.get("org.bukkit.attribute.AttributeModifier(java.util.UUID, java.lang.String, double, org.bukkit.attribute.AttributeModifier$Operation, org.bukkit.inventory.EquipmentSlotGroup)");
                getSlotGroup = MySimpleReflect.get("org.bukkit.attribute.AttributeModifier#getSlotGroup()");
                getByName = MySimpleReflect.get("org.bukkit.inventory.EquipmentSlotGroup#getByName(java.lang.String)");
            } catch (NoSuchFieldException | ClassNotFoundException | NoSuchMethodException e) {
                DebugLogger.debug(e);
                flag1 = false;
            }
            flag = flag1;
        }

        @Override
        protected void serialize(Handler handler, ConfigurationHashMap section, Map.Entry<Object, Object> entry, ItemStack itemStack, ItemMeta itemMeta) {
            super.serialize(handler, section, entry, itemStack, itemMeta);
            section.put("slot-group", Objects.toString(getSlotGroup.execute(entry.getValue())));
        }

        @Override
        protected Object deserialize(Handler handler, ConfigurationHashMap section, String name, double amount, String operation) {
            if (!section.contains("slot-group")) {
                handler.deny();
                return null;
            }

            return newAttributeModifier.execute(
                    UUID.fromString(section.getString("uuid")),
                    name, amount, operationOf.execute(null, operation),
                    getByName.execute(null, section.getString("slot-group"))
            );
        }

        @Override
        public int getPriority() {
            return 1;
        }

        @Override
        public boolean isAvailable() {
            return super.isAvailable() && flag;
        }
    }
}
