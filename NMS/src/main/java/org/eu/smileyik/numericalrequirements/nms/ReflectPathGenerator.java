package org.eu.smileyik.numericalrequirements.nms;

import org.eu.smileyik.numericalrequirements.reflect.builder.ReflectClassBuilder;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class ReflectPathGenerator {
    private static final String CURRENT_PACKET = "${version}";

    public static void main(String[] args) throws InvocationTargetException, IllegalAccessException, IOException {
        String template = Files.readString(Path.of("plugins", "NumericalRequirements", "reflect_class_template.txt"));
        for (int i = 1; i < args.length; i += 2) {
            String name = args[i - 1];
            String className = args[i];
            try {
                ReflectClassBuilder builder = ReflectClassBuilder.newByClass(className);
                String prettyString = builder.toPrettyString();
                File file = new File("build/generator/", String.format("%s.txt", name));
                File parent = file.getParentFile();
                if (!parent.exists()) {
                    parent.mkdirs();
                }
                System.out.println(file.getCanonicalPath());
                Path path = file.toPath();
                prettyString = prettyString.replace(NMS.VERSION, CURRENT_PACKET);
                Files.writeString(
                        path, prettyString,
                        StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE
                );
                Files.writeString(
                        new File(parent, String.format("%s.java", name)).toPath(),
                        template.replace("${class_name}", name).replace("${script_path}", String.format("/version-script/%s.txt", name)),
                        StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE
                );
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

    }

//    private static ReflectClassBuilder ChatMessageType_1_16() throws ClassNotFoundException {
//        return ReflectClassBuilder.newByClass("net.minecraft.server." + NMS.VERSION + ".ChatMessageType");
//    }
//
//    private static ReflectClassBuilder PacketPlayOutChat_1_16() throws ClassNotFoundException {
//        return ReflectClassBuilder.newByClass("net.minecraft.server." + NMS.VERSION + ".PacketPlayOutChat");
//    }

//    private static ReflectClassBuilder CraftPlayer() {
//        return ReflectClassBuilder.newByClass(CraftPlayer.class);
//    }
//
//    private static ReflectClassBuilder ChatComponentText() {
//        return ReflectClassBuilder.newByClass(ChatComponentText.class);
//    }
//
//    private static ReflectClassBuilder PacketPlayOutChat_1_5() {
//        return ReflectClassBuilder.newByClass(PacketPlayOutChat.class);
//    }
//
//    private static ReflectClassBuilder EntityPlayer_1_5() {
//        return ReflectClassBuilder.newByClass(EntityPlayer.class);
//    }
//
//    private static ReflectClassBuilder PlayerConnection() {
//        return ReflectClassBuilder.newByClass(PlayerConnection.class);
//    }

//
//    private static ReflectClassBuilder NBTTagCompound_1_5_to_1_8() {
//
//    }

//    private static ReflectClassBuilder NBTTagCompound_1_5_to_1_8() {
//        return new ReflectClassBuilder(String.format("net.minecraft.server.%s.NBTTagCompound", CURRENT_PACKET))
//                .constructor("new").finished()
//                .method("c", "getKeys").finished()
//                .method("getTypeId", "getTypeId").finished()
//                .method("set", "set").args("java.lang.String", String.format("net.minecraft.server.%s.NBTBase", CURRENT_PACKET))
//                .method("setByte", "setByte").args("java.lang.String", "byte")
//                .method("setShort", "setShort").args("java.lang.String", "short")
//                .method("setInt", "setInt").args("java.lang.String", "int")
//                .method("setLong", "setLong").args("java.lang.String", "long")
//                .method("setFloat", "setFloat").args("java.lang.String", "float")
//                .method("setDouble", "setDouble").args("java.lang.String", "double")
//                .method("setString", "setString").args("java.lang.String", "java.lang.String")
//                .method("setByteArray", "setByteArray").args("java.lang.String", "byte[]")
//                .method("setIntArray", "setIntArray").args("java.lang.String", "int[]")
//                .method("setBoolean", "setBoolean").args("java.lang.String", "boolean")
//                .method("get", "get").args("java.lang.String") // NBTBase
//                .method("b", "getKeyTypeId").args("java.lang.String") // type
//                .method("hasKey", "hasKey").args("java.lang.String")
//                .method("hasKeyOfType", "hasKeyOfType").args("java.lang.String", "int")
//                .method("getByte", "getByte").args("java.lang.String")
//                .method("getShort", "getShort").args("java.lang.String")
//                .method("getInt", "getInt").args("java.lang.String")
//                .method("getLong", "getLong").args("java.lang.String")
//                .method("getFloat", "getFloat").args("java.lang.String")
//                .method("getDouble", "getDouble").args("java.lang.String")
//                .method("getString", "getString").args("java.lang.String")
//                .method("getByteArray", "getByteArray").args("java.lang.String")
//                .method("getIntArray", "getIntArray").args("java.lang.String")
//                .method("getCompound", "getCompound").args("java.lang.String") // NBTTagCompound
//                .method("getList", "getList").args("java.lang.String", "int") // NBTTagList
//                .method("getBoolean", "getBoolean").args("java.lang.String")
//                .method("remove", "remove").args("java.lang.String")
//                .method("isEmpty", "isEmpty").finished();
//    }
//
//    private static ReflectClassBuilder NBTTagCompound_1_9_to_1_16() {
//        return new ReflectClassBuilder(String.format("net.minecraft.server.%s.NBTTagCompound", CURRENT_PACKET))
//                .constructor("new").finished()
//                .method("c", "getKeys").finished()
//                .method("getTypeId", "getTypeId").finished()
//                .method("d", "size").finished()
//                .method("set", "set").args("java.lang.String", String.format("net.minecraft.server.%s.NBTBase", CURRENT_PACKET))
//                .method("setByte", "setByte").args("java.lang.String", "byte")
//                .method("setShort", "setShort").args("java.lang.String", "short")
//                .method("setInt", "setInt").args("java.lang.String", "int")
//                .method("setLong", "setLong").args("java.lang.String", "long")
//                .method("a", "setUUID").args("java.lang.String", UUID.class.getName())
//                .method("a", "getUUID").args("java.lang.String")
//                .method("b", "isUUID").args("java.lang.String")
//                .method("setFloat", "setFloat").args("java.lang.String", "float")
//                .method("setDouble", "setDouble").args("java.lang.String", "double")
//                .method("setString", "setString").args("java.lang.String", "java.lang.String")
//                .method("setByteArray", "setByteArray").args("java.lang.String", "byte[]")
//                .method("setIntArray", "setIntArray").args("java.lang.String", "int[]")
//                .method("setBoolean", "setBoolean").args("java.lang.String", "boolean")
//                .method("get", "get").args("java.lang.String") // NBTBase
//                .method("d", "getKeyTypeId").args("java.lang.String") // type
//                .method("hasKey", "hasKey").args("java.lang.String")
//                .method("hasKeyOfType", "hasKeyOfType").args("java.lang.String", "int")
//                .method("getByte", "getByte").args("java.lang.String")
//                .method("getShort", "getShort").args("java.lang.String")
//                .method("getInt", "getInt").args("java.lang.String")
//                .method("getLong", "getLong").args("java.lang.String")
//                .method("getFloat", "getFloat").args("java.lang.String")
//                .method("getDouble", "getDouble").args("java.lang.String")
//                .method("getString", "getString").args("java.lang.String")
//                .method("getByteArray", "getByteArray").args("java.lang.String")
//                .method("getIntArray", "getIntArray").args("java.lang.String")
//                .method("getCompound", "getCompound").args("java.lang.String") // NBTTagCompound
//                .method("getList", "getList").args("java.lang.String", "int") // NBTTagList
//                .method("getBoolean", "getBoolean").args("java.lang.String")
//                .method("remove", "remove").args("java.lang.String")
//                .method("isEmpty", "isEmpty").finished()
//                .method("a", "merge").args(String.format("net.minecraft.server.%s.NBTTagCompound", CURRENT_PACKET));
//    }
//
//    private static ReflectClassBuilder NBTTagCompound_1_17() {
//        return new ReflectClassBuilder("net.minecraft.nbt.NBTTagCompound")
//                .constructor("new").finished()
//                .method("getKeys", "getKeys").finished()
//                .method("getTypeId", "getTypeId").finished()
//                .method("e", "size").finished()
//                .method("set", "set").args("java.lang.String", "net.minecraft.nbt.NBTBase")
//                .method("setByte", "setByte").args("java.lang.String", "byte")
//                .method("setShort", "setShort").args("java.lang.String", "short")
//                .method("setInt", "setInt").args("java.lang.String", "int")
//                .method("setLong", "setLong").args("java.lang.String", "long")
//                .method("a", "setUUID").args("java.lang.String", UUID.class.getName())
//                .method("a", "getUUID").args("java.lang.String")
//                .method("b", "isUUID").args("java.lang.String")
//                .method("setFloat", "setFloat").args("java.lang.String", "float")
//                .method("setDouble", "setDouble").args("java.lang.String", "double")
//                .method("setString", "setString").args("java.lang.String", "java.lang.String")
//                .method("setByteArray", "setByteArray").args("java.lang.String", "byte[]")
//                .method("setIntArray", "setIntArray").args("java.lang.String", "int[]")
//                .method("setBoolean", "setBoolean").args("java.lang.String", "boolean")
//                .method("get", "get").args("java.lang.String") // NBTBase
//                .method("d", "getKeyTypeId").args("java.lang.String") // type
//                .method("hasKey", "hasKey").args("java.lang.String")
//                .method("hasKeyOfType", "hasKeyOfType").args("java.lang.String", "int")
//                .method("getByte", "getByte").args("java.lang.String")
//                .method("getShort", "getShort").args("java.lang.String")
//                .method("getInt", "getInt").args("java.lang.String")
//                .method("getLong", "getLong").args("java.lang.String")
//                .method("getFloat", "getFloat").args("java.lang.String")
//                .method("getDouble", "getDouble").args("java.lang.String")
//                .method("getString", "getString").args("java.lang.String")
//                .method("getByteArray", "getByteArray").args("java.lang.String")
//                .method("getIntArray", "getIntArray").args("java.lang.String")
//                .method("getCompound", "getCompound").args("java.lang.String") // NBTTagCompound
//                .method("getList", "getList").args("java.lang.String", "int") // NBTTagList
//                .method("getBoolean", "getBoolean").args("java.lang.String")
//                .method("remove", "remove").args("java.lang.String")
//                .method("isEmpty", "isEmpty").finished()
//                .method("a", "merge").args("net.minecraft.nbt.NBTTagCompound");
//    }
//
//    private static ReflectClassBuilder NBTTagCompound_1_18() {
//        return new ReflectClassBuilder("net.minecraft.nbt.NBTTagCompound")
//                .constructor("new").finished()
//                .method("d", "getKeys").finished()
//                .method("a", "getTypeId").finished()
//                .method("e", "size").finished()
//                .method("a", "set").args("java.lang.String", "net.minecraft.nbt.NBTBase")
//                .method("a", "setByte").args("java.lang.String", "byte")
//                .method("a", "setShort").args("java.lang.String", "short")
//                .method("a", "setInt").args("java.lang.String", "int")
//                .method("a", "setLong").args("java.lang.String", "long")
//                .method("a", "setUUID").args("java.lang.String", UUID.class.getName())
//                .method("a", "getUUID").args("java.lang.String")
//                .method("b", "isUUID").args("java.lang.String")
//                .method("a", "setFloat").args("java.lang.String", "float")
//                .method("a", "setDouble").args("java.lang.String", "double")
//                .method("a", "setString").args("java.lang.String", "java.lang.String")
//                .method("a", "setByteArray").args("java.lang.String", "byte[]")
//                .method("a", "setIntArray").args("java.lang.String", "int[]")
//                .method("a", "setBoolean").args("java.lang.String", "boolean")
//                .method("c", "get").args("java.lang.String") // NBTBase
//                .method("d", "getKeyTypeId").args("java.lang.String") // type
//                .method("e", "hasKey").args("java.lang.String")
//                .method("b", "hasKeyOfType").args("java.lang.String", "int")
//                .method("f", "getByte").args("java.lang.String")
//                .method("g", "getShort").args("java.lang.String")
//                .method("h", "getInt").args("java.lang.String")
//                .method("i", "getLong").args("java.lang.String")
//                .method("j", "getFloat").args("java.lang.String")
//                .method("k", "getDouble").args("java.lang.String")
//                .method("l", "getString").args("java.lang.String")
//                .method("m", "getByteArray").args("java.lang.String")
//                .method("n", "getIntArray").args("java.lang.String")
//                .method("p", "getCompound").args("java.lang.String") // NBTTagCompound
//                .method("c", "getList").args("java.lang.String", "int") // NBTTagList
//                .method("q", "getBoolean").args("java.lang.String")
//                .method("r", "remove").args("java.lang.String")
//                .method("f", "isEmpty").finished()
//                .method("a", "merge").args("net.minecraft.nbt.NBTTagCompound");
//    }
//
//    private static ReflectClassBuilder NMSItemStack_1_5_to_1_16() {
//        return new ReflectClassBuilder(String.format("net.minecraft.server.%s.ItemStack", CURRENT_PACKET))
//                .method("hasTag", "hasTag").finished()
//                .method("getTag", "getTag").finished()
//                .method("setTag", "setTag").args(String.format("net.minecraft.server.%s.NBTTagCompound", CURRENT_PACKET));
//    }
//
//    private static ReflectClassBuilder NMSItemStack_1_17() {
//        return new ReflectClassBuilder("net.minecraft.world.item.ItemStack")
//                .method("hasTag", "hasTag").finished()
//                .method("getTag", "getTag").finished()
//                .method("setTag", "setTag").args("net.minecraft.nbt.NBTTagCompound");
//    }
//
//    private static ReflectClassBuilder NMSItemStack_1_18() {
//        return new ReflectClassBuilder("net.minecraft.world.item.ItemStack")
//                .method("r", "hasTag").finished()
//                .method("t", "getTag").finished()
//                .method("c", "setTag").args("net.minecraft.nbt.NBTTagCompound");
//    }
//
//    private static ReflectClassBuilder NMSItemStack_1_18_2() {
//        return new ReflectClassBuilder("net.minecraft.world.item.ItemStack")
//                .method("s", "hasTag").finished()
//                .method("t", "getTag").finished()
//                .method("c", "setTag").args("net.minecraft.nbt.NBTTagCompound");
//    }
//
//    private static ReflectClassBuilder NMSItemStack_1_19() {
//        return new ReflectClassBuilder("net.minecraft.world.item.ItemStack")
//                .method("t", "hasTag").finished()
//                .method("u", "getTag").finished()
//                .method("c", "setTag").args("net.minecraft.nbt.NBTTagCompound");
//    }
//
//    private static ReflectClassBuilder NMSItemStack_1_20() {
//        return new ReflectClassBuilder("net.minecraft.world.item.ItemStack")
//                .method("u", "hasTag").finished()
//                .method("v", "getTag").finished()
//                .method("c", "setTag").args("net.minecraft.nbt.NBTTagCompound");
//    }
//
//    private static ReflectClassBuilder NMSItemStack_1_21() {
//        return new ReflectClassBuilder("net.minecraft.world.item.ItemStack")
//                .method("c", "getDataComponent").args("net.minecraft.core.component.DataComponentType")
//                .method("b", "setDataComponent").args("net.minecraft.core.component.DataComponentType", "java.lang.Object");
//    }
}
