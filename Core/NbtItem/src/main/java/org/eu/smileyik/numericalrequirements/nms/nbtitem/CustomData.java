package org.eu.smileyik.numericalrequirements.nms.nbtitem;

import org.eu.smileyik.numericalrequirements.nms.nbt.NBTTagCompound;
import org.eu.smileyik.numericalrequirements.reflect.MySimpleReflect;
import org.eu.smileyik.numericalrequirements.reflect.ReflectClass;
import org.eu.smileyik.numericalrequirements.reflect.ReflectField;
import org.eu.smileyik.numericalrequirements.reflect.builder.ReflectClassBuilder;

public class CustomData {
    private static final ReflectClass CLASS;
    public static final Object KEY_CUSTOM_DATA;

    static {
        try {
            CLASS = new ReflectClassBuilder("net.minecraft.world.item.component.CustomData")
                    .method("c", "getNBTTagCompound").finished()
                    .method("a", "newCustomData").args("net.minecraft.nbt.NBTTagCompound")
                    .toClass();
            ReflectField field = MySimpleReflect.get("net.minecraft.core.component.DataComponents@b");
            KEY_CUSTOM_DATA = field.execute(null);
        } catch (NoSuchFieldException | ClassNotFoundException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private final Object instance;

    protected CustomData(Object instance) {
        this.instance = instance;
    }

    public NBTTagCompound getNBTTagCompound() {
        Object execute = CLASS.execute("getNBTTagCompound", instance);
        return execute == null ? new NBTTagCompound() : new NBTTagCompound(execute);
    }

    public static CustomData newCustomData(NBTTagCompound tag) {
        if (tag == null) {
            return null;
        }
        return new CustomData(CLASS.execute("newCustomData", null, tag.getInstance()));
    }


    protected Object getInstance() {
        return instance;
    }
}
