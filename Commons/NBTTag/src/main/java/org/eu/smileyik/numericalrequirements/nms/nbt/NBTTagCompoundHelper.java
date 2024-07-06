package org.eu.smileyik.numericalrequirements.nms.nbt;

import org.bukkit.Bukkit;
import org.eu.smileyik.numericalrequirements.reflect.builder.ReflectClassBuilder;

import java.util.UUID;

public interface NBTTagCompoundHelper {

    String CURRENT_PACKET = Bukkit.getServer().getClass()
            .getPackage().getName().replace(".", ",").split(",")[3];

    static ReflectClassBuilder getCurrentVersion() {
        String version = Bukkit.getServer().getClass()
                .getPackage().getName().replace(".", ",").split(",")[3];
        String[] versions = version.split("_");
        int i = Integer.parseInt(versions[1]);
        if (i >= 18) {
            return getVersion_1_18();
        } else if (i == 17) {
            return getVersion_1_17();
        } else if (i >= 9) {
            return getVersion_1_9_to_1_16();
        } else if (i >= 5) {
            return getVersion_1_5_to_1_8();
        }
        return null;
    }

    private static ReflectClassBuilder getVersion_1_5_to_1_8() {
        return new ReflectClassBuilder(String.format("net.minecraft.server.%s.NBTTagCompound", CURRENT_PACKET))
                .constructor("new").finished()
                .method("c", "getKeys").finished()
                .method("getTypeId", "getTypeId").finished()
                .method("set", "set").args("java.lang.String", String.format("net.minecraft.server.%s.NBTBase", CURRENT_PACKET))
                .method("setByte", "setByte").args("java.lang.String", "byte")
                .method("setShort", "setShort").args("java.lang.String", "short")
                .method("setInt", "setInt").args("java.lang.String", "int")
                .method("setLong", "setLong").args("java.lang.String", "long")
                .method("setFloat", "setFloat").args("java.lang.String", "float")
                .method("setDouble", "setDouble").args("java.lang.String", "double")
                .method("setString", "setString").args("java.lang.String", "java.lang.String")
                .method("setByteArray", "setByteArray").args("java.lang.String", "byte[]")
                .method("setIntArray", "setIntArray").args("java.lang.String", "int[]")
                .method("setBoolean", "setBoolean").args("java.lang.String", "boolean")
                .method("get", "get").args("java.lang.String") // NBTBase
                .method("b", "getKeyTypeId").args("java.lang.String") // type
                .method("hasKey", "hasKey").args("java.lang.String")
                .method("hasKeyOfType", "hasKeyOfType").args("java.lang.String", "int")
                .method("getByte", "getByte").args("java.lang.String")
                .method("getShort", "getShort").args("java.lang.String")
                .method("getInt", "getInt").args("java.lang.String")
                .method("getLong", "getLong").args("java.lang.String")
                .method("getFloat", "getFloat").args("java.lang.String")
                .method("getDouble", "getDouble").args("java.lang.String")
                .method("getString", "getString").args("java.lang.String")
                .method("getByteArray", "getByteArray").args("java.lang.String")
                .method("getIntArray", "getIntArray").args("java.lang.String")
                .method("getCompound", "getCompound").args("java.lang.String") // NBTTagCompound
                .method("getList", "getList").args("java.lang.String", "int") // NBTTagList
                .method("getBoolean", "getBoolean").args("java.lang.String")
                .method("remove", "remove").args("java.lang.String")
                .method("isEmpty", "isEmpty").finished();
    }

    private static ReflectClassBuilder getVersion_1_9_to_1_16() {
        return new ReflectClassBuilder(String.format("net.minecraft.server.%s.NBTTagCompound", CURRENT_PACKET))
                .constructor("new").finished()
                .method("c", "getKeys").finished()
                .method("getTypeId", "getTypeId").finished()
                .method("d", "size").finished()
                .method("set", "set").args("java.lang.String", String.format("net.minecraft.server.%s.NBTBase", CURRENT_PACKET))
                .method("setByte", "setByte").args("java.lang.String", "byte")
                .method("setShort", "setShort").args("java.lang.String", "short")
                .method("setInt", "setInt").args("java.lang.String", "int")
                .method("setLong", "setLong").args("java.lang.String", "long")
                .method("a", "setUUID").args("java.lang.String", UUID.class.getName())
                .method("a", "getUUID").args("java.lang.String")
                .method("b", "isUUID").args("java.lang.String")
                .method("setFloat", "setFloat").args("java.lang.String", "float")
                .method("setDouble", "setDouble").args("java.lang.String", "double")
                .method("setString", "setString").args("java.lang.String", "java.lang.String")
                .method("setByteArray", "setByteArray").args("java.lang.String", "byte[]")
                .method("setIntArray", "setIntArray").args("java.lang.String", "int[]")
                .method("setBoolean", "setBoolean").args("java.lang.String", "boolean")
                .method("get", "get").args("java.lang.String") // NBTBase
                .method("d", "getKeyTypeId").args("java.lang.String") // type
                .method("hasKey", "hasKey").args("java.lang.String")
                .method("hasKeyOfType", "hasKeyOfType").args("java.lang.String", "int")
                .method("getByte", "getByte").args("java.lang.String")
                .method("getShort", "getShort").args("java.lang.String")
                .method("getInt", "getInt").args("java.lang.String")
                .method("getLong", "getLong").args("java.lang.String")
                .method("getFloat", "getFloat").args("java.lang.String")
                .method("getDouble", "getDouble").args("java.lang.String")
                .method("getString", "getString").args("java.lang.String")
                .method("getByteArray", "getByteArray").args("java.lang.String")
                .method("getIntArray", "getIntArray").args("java.lang.String")
                .method("getCompound", "getCompound").args("java.lang.String") // NBTTagCompound
                .method("getList", "getList").args("java.lang.String", "int") // NBTTagList
                .method("getBoolean", "getBoolean").args("java.lang.String")
                .method("remove", "remove").args("java.lang.String")
                .method("isEmpty", "isEmpty").finished()
                .method("a", "merge").args(String.format("net.minecraft.server.%s.NBTTagCompound", CURRENT_PACKET));
    }

    private static ReflectClassBuilder getVersion_1_17() {
        return new ReflectClassBuilder("net.minecraft.nbt.NBTTagCompound")
                .constructor("new").finished()
                .method("getKeys", "getKeys").finished()
                .method("getTypeId", "getTypeId").finished()
                .method("e", "size").finished()
                .method("set", "set").args("java.lang.String", "net.minecraft.nbt.NBTBase")
                .method("setByte", "setByte").args("java.lang.String", "byte")
                .method("setShort", "setShort").args("java.lang.String", "short")
                .method("setInt", "setInt").args("java.lang.String", "int")
                .method("setLong", "setLong").args("java.lang.String", "long")
                .method("a", "setUUID").args("java.lang.String", UUID.class.getName())
                .method("a", "getUUID").args("java.lang.String")
                .method("b", "isUUID").args("java.lang.String")
                .method("setFloat", "setFloat").args("java.lang.String", "float")
                .method("setDouble", "setDouble").args("java.lang.String", "double")
                .method("setString", "setString").args("java.lang.String", "java.lang.String")
                .method("setByteArray", "setByteArray").args("java.lang.String", "byte[]")
                .method("setIntArray", "setIntArray").args("java.lang.String", "int[]")
                .method("setBoolean", "setBoolean").args("java.lang.String", "boolean")
                .method("get", "get").args("java.lang.String") // NBTBase
                .method("d", "getKeyTypeId").args("java.lang.String") // type
                .method("hasKey", "hasKey").args("java.lang.String")
                .method("hasKeyOfType", "hasKeyOfType").args("java.lang.String", "int")
                .method("getByte", "getByte").args("java.lang.String")
                .method("getShort", "getShort").args("java.lang.String")
                .method("getInt", "getInt").args("java.lang.String")
                .method("getLong", "getLong").args("java.lang.String")
                .method("getFloat", "getFloat").args("java.lang.String")
                .method("getDouble", "getDouble").args("java.lang.String")
                .method("getString", "getString").args("java.lang.String")
                .method("getByteArray", "getByteArray").args("java.lang.String")
                .method("getIntArray", "getIntArray").args("java.lang.String")
                .method("getCompound", "getCompound").args("java.lang.String") // NBTTagCompound
                .method("getList", "getList").args("java.lang.String", "int") // NBTTagList
                .method("getBoolean", "getBoolean").args("java.lang.String")
                .method("remove", "remove").args("java.lang.String")
                .method("isEmpty", "isEmpty").finished()
                .method("a", "merge").args("net.minecraft.nbt.NBTTagCompound");
    }

    private static ReflectClassBuilder getVersion_1_18() {
        return new ReflectClassBuilder("net.minecraft.nbt.NBTTagCompound")
                .constructor("new").finished()
                .method("d", "getKeys").finished()
                .method("a", "getTypeId").finished()
                .method("e", "size").finished()
                .method("a", "set").args("java.lang.String", "net.minecraft.nbt.NBTBase")
                .method("a", "setByte").args("java.lang.String", "byte")
                .method("a", "setShort").args("java.lang.String", "short")
                .method("a", "setInt").args("java.lang.String", "int")
                .method("a", "setLong").args("java.lang.String", "long")
                .method("a", "setUUID").args("java.lang.String", UUID.class.getName())
                .method("a", "getUUID").args("java.lang.String")
                .method("b", "isUUID").args("java.lang.String")
                .method("a", "setFloat").args("java.lang.String", "float")
                .method("a", "setDouble").args("java.lang.String", "double")
                .method("a", "setString").args("java.lang.String", "java.lang.String")
                .method("a", "setByteArray").args("java.lang.String", "byte[]")
                .method("a", "setIntArray").args("java.lang.String", "int[]")
                .method("a", "setBoolean").args("java.lang.String", "boolean")
                .method("c", "get").args("java.lang.String") // NBTBase
                .method("d", "getKeyTypeId").args("java.lang.String") // type
                .method("e", "hasKey").args("java.lang.String")
                .method("b", "hasKeyOfType").args("java.lang.String", "int")
                .method("f", "getByte").args("java.lang.String")
                .method("g", "getShort").args("java.lang.String")
                .method("h", "getInt").args("java.lang.String")
                .method("i", "getLong").args("java.lang.String")
                .method("j", "getFloat").args("java.lang.String")
                .method("k", "getDouble").args("java.lang.String")
                .method("l", "getString").args("java.lang.String")
                .method("m", "getByteArray").args("java.lang.String")
                .method("n", "getIntArray").args("java.lang.String")
                .method("p", "getCompound").args("java.lang.String") // NBTTagCompound
                .method("c", "getList").args("java.lang.String", "int") // NBTTagList
                .method("q", "getBoolean").args("java.lang.String")
                .method("r", "remove").args("java.lang.String")
                .method("f", "isEmpty").finished()
                .method("a", "merge").args("net.minecraft.nbt.NBTTagCompound");
    }
}