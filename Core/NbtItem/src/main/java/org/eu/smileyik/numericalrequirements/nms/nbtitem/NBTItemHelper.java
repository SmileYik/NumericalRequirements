package org.eu.smileyik.numericalrequirements.nms.nbtitem;

import org.bukkit.inventory.ItemStack;
import org.eu.smileyik.numericalrequirements.reflect.builder.ReflectClassBuilder;

/**
 * 辅助创建NBTItem实例。
 * @see NBTItem
 */
public class NBTItemHelper {

    public static String[] VERSIONS = NBTItem.getBukkitVersion().split("_");
    public static int MIDDLE_VERSION = Integer.parseInt(VERSIONS[1]);

    /**
     * 将Bukkit的物品转换为NBT物品。
     * @param itemStack 要转换的物品。
     * @return 如果转换成功返回对应NBT物品，否则则返回null。
     */
    public static NBTItem cast(ItemStack itemStack) {
        if (MIDDLE_VERSION >= 21) {
            return new NBTItemImplFor_1_21(itemStack);
        } else if (MIDDLE_VERSION >= 5) {
            return new NBTItemImpl(itemStack);
        }
        return null;
    }

    public static ReflectClassBuilder getCurrentVersionNMSItemStack() {
        if (MIDDLE_VERSION >= 5 && MIDDLE_VERSION <= 16) {
            return getNMSItemStack_1_5_to_1_16();
        } else if (MIDDLE_VERSION == 17) {
            return getNMSItemStack_1_17();
        } else if (MIDDLE_VERSION == 18) {
            switch (VERSIONS[2].toUpperCase()) {
                case "R1":
                    return getNMSItemStack_1_18();
                default:
                    return getNMSItemStack_1_18_2();
            }
        } else if (MIDDLE_VERSION == 19) {
            return getNMSItemStack_1_19();
        } else if (MIDDLE_VERSION == 20) {
            return getNMSItemStack_1_20();
        } else if (MIDDLE_VERSION >= 21) {
            return getNMSItemStack_1_21();
        } else {
            return null;
        }
    }

    private static ReflectClassBuilder getNMSItemStack_1_5_to_1_16() {
        return new ReflectClassBuilder(String.format("net.minecraft.server.%s.ItemStack", NBTItem.getBukkitVersion()))
                .method("hasTag", "hasTag").finished()
                .method("getTag", "getTag").finished()
                .method("setTag", "setTag").args(String.format("net.minecraft.server.%s.NBTTagCompound", NBTItem.getBukkitVersion()));
    }

    private static ReflectClassBuilder getNMSItemStack_1_17() {
        return new ReflectClassBuilder("net.minecraft.world.item.ItemStack")
                .method("hasTag", "hasTag").finished()
                .method("getTag", "getTag").finished()
                .method("setTag", "setTag").args("net.minecraft.nbt.NBTTagCompound");
    }

    private static ReflectClassBuilder getNMSItemStack_1_18() {
        return new ReflectClassBuilder("net.minecraft.world.item.ItemStack")
                .method("r", "hasTag").finished()
                .method("t", "getTag").finished()
                .method("c", "setTag").args("net.minecraft.nbt.NBTTagCompound");
    }

    private static ReflectClassBuilder getNMSItemStack_1_18_2() {
        return new ReflectClassBuilder("net.minecraft.world.item.ItemStack")
                .method("s", "hasTag").finished()
                .method("t", "getTag").finished()
                .method("c", "setTag").args("net.minecraft.nbt.NBTTagCompound");
    }

    private static ReflectClassBuilder getNMSItemStack_1_19() {
        return new ReflectClassBuilder("net.minecraft.world.item.ItemStack")
                .method("t", "hasTag").finished()
                .method("u", "getTag").finished()
                .method("c", "setTag").args("net.minecraft.nbt.NBTTagCompound");
    }

    private static ReflectClassBuilder getNMSItemStack_1_20() {
        return new ReflectClassBuilder("net.minecraft.world.item.ItemStack")
                .method("u", "hasTag").finished()
                .method("v", "getTag").finished()
                .method("c", "setTag").args("net.minecraft.nbt.NBTTagCompound");
    }

    private static ReflectClassBuilder getNMSItemStack_1_21() {
        return new ReflectClassBuilder("net.minecraft.world.item.ItemStack")
                .method("c", "getDataComponent").args("net.minecraft.core.component.DataComponentType")
                .method("b", "setDataComponent").args("net.minecraft.core.component.DataComponentType", "java.lang.Object");
    }
}
