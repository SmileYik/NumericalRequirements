package org.eu.smileyik.numericalrequirements.nms.nbtitem;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.eu.smileyik.numericalrequirements.reflect.ReflectClass;
import org.eu.smileyik.numericalrequirements.reflect.builder.ReflectClassBuilder;

public class CraftItemStack {
    private static final ReflectClass CLAZZ;

    static {
        String ver = Bukkit.getServer().getClass()
                .getPackage().getName().replace(".", ",").split(",")[3];
        try {
            String itemStackClass = String.format("net.minecraft.server.%s.ItemStack", ver);
            if (Integer.parseInt(ver.split("_")[1]) >= 17) {
                itemStackClass = "net.minecraft.world.item.ItemStack";
            }

            CLAZZ = new ReflectClassBuilder(String.format("org.bukkit.craftbukkit.%s.inventory.CraftItemStack", ver))
                    .method("asBukkitCopy").args(itemStackClass)
                    .method("asNMSCopy").args(ItemStack.class.getName())
                    .toClass();
        } catch (NoSuchFieldException | ClassNotFoundException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static ItemStack asBukkitCopy(NMSItemStack itemStack) {
        if (itemStack == null) return null;
        return (ItemStack) CLAZZ.execute("asBukkitCopy", null, itemStack.getInstance());
    }

    public static NMSItemStack asNMSCopy(ItemStack itemStack) {
        Object execute = CLAZZ.execute("asNMSCopy", null, itemStack);
        if (execute == null) return null;
        return new NMSItemStack(execute);
    }

}
