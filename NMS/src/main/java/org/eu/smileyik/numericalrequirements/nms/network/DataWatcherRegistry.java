package org.eu.smileyik.numericalrequirements.nms.network;

import org.eu.smileyik.numericalrequirements.debug.DebugLogger;
import org.eu.smileyik.numericalrequirements.nms.ReflectClassBase;
import org.eu.smileyik.numericalrequirements.reflect.MySimpleReflect;
import org.eu.smileyik.numericalrequirements.reflect.ReflectClass;
import org.eu.smileyik.numericalrequirements.versionscript.VersionScript;

import java.io.IOException;
import java.util.Map;
import java.util.WeakHashMap;

public class DataWatcherRegistry implements ReflectClassBase {
    private static final String SCRIPT_PATH = "/version-script/DataWatcherRegistry.txt";
    private static final ReflectClass CLASS;
    private static final Map<String, Object> DATA_WATCHER_SERIALIZER_CACHE = new WeakHashMap<>();

    public static final String BYTE = "Byte";
    public static final String INTEGER = "Integer";
    public static final String FLOAT = "Float";
    public static final String STRING = "String";
    public static final String I_CHAT_BASE_COMPONENT = "IChatBaseComponent";
    public static final String OPTIONAL_I_CHAT_BASE_COMPONENT = "OptionalIChatBaseComponent";
    public static final String ITEM_STACK = "ItemStack";
    public static final String OPTIONAL_I_BLOCK_DATA = "OptionalIBlockData";
    public static final String BOOLEAN = "Boolean";
    public static final String PARTICLE_PARAM = "ParticleParam";
    public static final String VECTOR3F = "Vector3f";
    public static final String BLOCK_POSITION = "BlockPosition";
    public static final String OPTIONAL_BLOCK_POSITION = "OptionalBlockPosition";
    public static final String ENUM_DIRECTION = "EnumDirection";
    public static final String OPTIONAL_UUID = "OptionalUUID";
    public static final String NBT_TAG_COMPOUND = "NBTTagCompound";
    public static final String VILLAGER_DATA = "VillagerData";
    public static final String OPTIONAL_INT = "OptionalInt";
    public static final String ENTITY_POSE = "EntityPose";

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

    public static synchronized Object getDataWatcherSerializer(String type) {
        if (CLASS == null) return null;
        if (DATA_WATCHER_SERIALIZER_CACHE.containsKey(type)) {
            return DATA_WATCHER_SERIALIZER_CACHE.get(type);
        }

        Object o = null;
        if (CLASS.hasField(type)) {
            o = CLASS.get(type, null);
            DATA_WATCHER_SERIALIZER_CACHE.put(type, o);
        } else {
            DebugLogger.debug("Not found data watcher serializer for %s", type);
        }
        return o;
    }

    @Override
    public Object getInstance() {
        return null;
    }
}
