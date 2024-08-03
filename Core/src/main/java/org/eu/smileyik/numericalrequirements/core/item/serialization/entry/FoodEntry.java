package org.eu.smileyik.numericalrequirements.core.item.serialization.entry;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.eu.smileyik.numericalrequirements.core.api.item.ItemSerializationEntry;
import org.eu.smileyik.numericalrequirements.core.api.util.ConfigurationHashMap;
import org.eu.smileyik.numericalrequirements.debug.DebugLogger;
import org.eu.smileyik.numericalrequirements.reflect.MySimpleReflect;
import org.eu.smileyik.numericalrequirements.reflect.ReflectClass;
import org.eu.smileyik.numericalrequirements.reflect.ReflectClassPathBuilder;

import java.util.List;

public class FoodEntry implements ItemSerializationEntry {
    final boolean flag;

    ReflectClass itemMetaClass;
    ReflectClass foodComponent;

    public FoodEntry() {
        boolean flag1 = true;

        try {
            itemMetaClass = MySimpleReflect.getReflectClass(new ReflectClassPathBuilder()
                            .newGroup("org.bukkit.inventory.meta.ItemMeta#")
                            .append("getFood()")
                            .append("setFood(org.bukkit.inventory.meta.components.FoodComponent)")
                            .append("hasFood()")
                            .endGroup()
                            .finish());
            foodComponent = MySimpleReflect.getReflectClass(new ReflectClassPathBuilder()
                            .newGroup("org.bukkit.inventory.meta.components.FoodComponent")
                            .newGroup("#")
                            .append("canAlwaysEat()")
                            .append("getEatSeconds()")
                            .append("getEffects()")
                            .append("getNutrition()")
                            .append("getSaturation()")
                            .append("setCanAlwaysEat(boolean)")
                            .append("setEatSeconds(float)")
                            .append("setNutrition(int)")
                            .append("setSaturation(float)")
                            .append("addEffect(org.bukkit.potion.PotionEffect, float)")
                            .endGroup()
                            .newGroup("$FoodEffect#")
                            .append("getEffect()")
                            .append("getProbability()")
                            .endGroup()
                            .endGroup()
                            .finish());
        } catch (NoSuchFieldException | ClassNotFoundException | NoSuchMethodException e) {
            DebugLogger.debug(e);
            flag1 = false;
        }
        flag = flag1;
    }

    @Override
    public String getKey() {
        return getId();
    }

    @Override
    public String getId() {
        return "food";
    }

    @Override
    public boolean isAvailable() {
        return flag;
    }

    @Override
    public void serialize(Handler handler, ConfigurationHashMap section, ItemStack itemStack, ItemMeta itemMeta) {
        if (!(boolean) itemMetaClass.execute("hasFood", itemMeta)) {
            return;
        }
        Object food = itemMetaClass.execute("getFood", itemMeta);
        if (food == null) return;
        section.put("can-always-eat", foodComponent.execute("canAlwaysEat", food));
        section.put("eat-seconds", foodComponent.execute("getEatSeconds", food));
        section.put("nutrition", foodComponent.execute("getNutrition", food));
        section.put("saturation", foodComponent.execute("getSaturation", food));
        ConfigurationHashMap ecs = section.createMap("effects");
        List<Object> effects = (List<Object>) foodComponent.execute("getEffects", food);
        for (Object effect : effects) {
            PotionEffect potionEffect = (PotionEffect) foodComponent.execute("getEffect", effect);
            float probability = (float) foodComponent.execute("getProbability", effect);
            String name = potionEffect.getType().getName();
            ConfigurationHashMap ec = ecs.createMap(name);
            ec.put("probability", probability);
            ec.put("duration", potionEffect.getDuration());
            ec.put("amplifier", potionEffect.getAmplifier());
        }
    }

    @Override
    public ItemStack deserialize(Handler handler, ConfigurationHashMap section, ItemStack itemStack, ItemMeta itemMeta) {
        Object food = itemMetaClass.execute("getFood", itemMeta);

        if (section.contains("can-always-eat")) foodComponent.execute("setCanAlwaysEat", food, section.getBoolean("can-always-eat"));
        if (section.contains("eat-seconds")) foodComponent.execute("setEatSeconds", food, (float) section.getDouble("eat-seconds"));
        if (section.contains("nutrition")) foodComponent.execute("setNutrition", food, section.getInt("nutrition"));
        if (section.contains("saturation")) foodComponent.execute("setSaturation", food, (float) section.getInt("saturation"));
        if (section.contains("effects")) {
            section = section.getMap("effects");
            for (String type : section.keySet()) {
                ConfigurationHashMap ec = section.getMap(type);
                PotionEffect pe = new PotionEffect(
                        PotionEffectType.getByName(type),
                        ec.getInt("duration"),
                        ec.getInt("amplifier")
                );
                foodComponent.execute("addEffect", food, pe, (float) ec.getDouble("probability"));
            }
        }

        itemMetaClass.execute("setFood", itemMeta, food);
        return null;
    }
}
