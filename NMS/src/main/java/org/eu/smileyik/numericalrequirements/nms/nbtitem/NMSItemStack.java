package org.eu.smileyik.numericalrequirements.nms.nbtitem;

import org.eu.smileyik.numericalrequirements.debug.DebugLogger;
import org.eu.smileyik.numericalrequirements.nms.nbt.NBTTagCompound;
import org.eu.smileyik.numericalrequirements.reflect.MySimpleReflect;
import org.eu.smileyik.numericalrequirements.reflect.ReflectClass;
import org.eu.smileyik.numericalrequirements.versionscript.VersionScript;

import java.io.IOException;

public class NMSItemStack {
    private static final String SCRIPT_PATH = "/version-script/NMSItemStack.txt";
    private static final ReflectClass CLAZZ;

    static {
        try {
            String currentVersion = VersionScript.runScriptByResource(SCRIPT_PATH);
            CLAZZ = MySimpleReflect.readByResource(
                    currentVersion, false,
                    "${version}", VersionScript.VERSION
            );
        } catch (NoSuchFieldException | ClassNotFoundException | NoSuchMethodException | IOException e) {
            DebugLogger.debug(e);
            throw new RuntimeException(e);
        }
    }

    private final Object instance;

    protected NMSItemStack(Object instance) {
        this.instance = instance;
    }

    public Object getInstance() {
        return instance;
    }

    public boolean hasTag() {
        return (boolean) CLAZZ.execute("hasTag", instance);
    }

    public NBTTagCompound getTag() {
        Object execute = CLAZZ.execute("getTag", instance);
        if (execute == null) return null;
        return new NBTTagCompound(execute);
    }

    public void setTag(NBTTagCompound tag) {
        CLAZZ.execute("setTag", instance, tag == null ? null : tag.getInstance());
    }

    public Object getDataComponent(Object componentType) {
        return CLAZZ.execute("getDataComponent", instance, componentType);
    }

    public void setDataComponent(Object componentType, Object value) {
        CLAZZ.execute("setDataComponent", instance, componentType, value);
    }

    public CustomData getCustomData() {
        Object dataComponent = getDataComponent(CustomData.KEY_CUSTOM_DATA);
        if (dataComponent == null) return null;
        return new CustomData(dataComponent);
    }

    public void setCustomData(CustomData value) {
        setDataComponent(CustomData.KEY_CUSTOM_DATA, value.getInstance());
    }
}
