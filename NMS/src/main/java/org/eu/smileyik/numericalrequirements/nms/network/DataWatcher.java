package org.eu.smileyik.numericalrequirements.nms.network;

import org.eu.smileyik.numericalrequirements.debug.DebugLogger;
import org.eu.smileyik.numericalrequirements.nms.ReflectClassBase;
import org.eu.smileyik.numericalrequirements.nms.entity.NMSEntity;
import org.eu.smileyik.numericalrequirements.reflect.MySimpleReflect;
import org.eu.smileyik.numericalrequirements.reflect.ReflectClass;
import org.eu.smileyik.numericalrequirements.versionscript.VersionScript;

import java.io.IOException;

public class DataWatcher implements ReflectClassBase {
    private static final String SCRIPT_PATH = "/version-script/network/DataWatcher.txt";
    private static final ReflectClass CLASS;

    static {
        try {
            String currentVersion = VersionScript.runScriptByResource(SCRIPT_PATH);
            if (currentVersion != null) {
                CLASS = MySimpleReflect.readByResource(
                        currentVersion, false,
                        "${version}", VersionScript.VERSION
                );
            } else {
                CLASS = null;
            }
        } catch (NoSuchFieldException | ClassNotFoundException | NoSuchMethodException | IOException e) {
            DebugLogger.debug(e);
            throw new RuntimeException(e);
        }
    }

    private final Object instance;

    public DataWatcher(Object instance) {
        if (CLASS == null) {
            throw new IllegalStateException("Class not initialized");
        }
        this.instance = instance;
    }

    /**
     * 新建一个 DataWatcher 实例
     * @param nmsEntity
     * @return
     */
    public static DataWatcher newInstance(NMSEntity nmsEntity) {
        return new DataWatcher(nmsEntity.getInstance());
    }

    /**
     * 新建一个 DataWatcher 实例
     * @param nmsEntity
     * @return
     */
    public static DataWatcher newInstance(Object nmsEntity) {
        if (CLASS == null) return null;
        return new DataWatcher(CLASS.newInstance("newInstance", nmsEntity));
    }

    @Override
    public Object getInstance() {
        return instance;
    }

    public static Object newDataWatcherObject(Class<?> nmsEntityClass, String registeredSerializer) {
        if (CLASS == null) return null;
        Object serializer = DataWatcherRegistry.getDataWatcherSerializer(registeredSerializer);
        if (serializer == null) return null;
        return CLASS.execute("newDataWatcherObject", null, nmsEntityClass, serializer);
    }

    public void register(Object dataWatcherObject, Object value) {
        CLASS.execute("register", instance, dataWatcherObject, value);
    }
}
