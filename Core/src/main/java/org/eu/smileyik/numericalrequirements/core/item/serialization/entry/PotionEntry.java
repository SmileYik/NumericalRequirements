package org.eu.smileyik.numericalrequirements.core.item.serialization.entry;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.eu.smileyik.numericalrequirements.core.api.item.ItemSerializerEntry;
import org.eu.smileyik.numericalrequirements.core.api.util.ConfigurationHashMap;
import org.eu.smileyik.numericalrequirements.debug.DebugLogger;
import org.eu.smileyik.numericalrequirements.reflect.ReflectClass;
import org.eu.smileyik.numericalrequirements.reflect.builder.ReflectClassBuilder;

import java.util.List;

public abstract class PotionEntry implements ItemSerializerEntry {
    final boolean flag;

    protected PotionEntry() {
        boolean flag = true;
        try {
            init();
        } catch (Exception e) {
            DebugLogger.debug(e);
            flag = false;
        }
        this.flag = flag;
    }

    protected abstract void init() throws Exception;

    @Override
    public String getId() {
        return "potion";
    }

    @Override
    public boolean isAvailable() {
        return flag;
    }


    public static class Potion1Entry extends PotionEntry {

        protected ReflectClass POTION_META_CLASS;
        protected ReflectClass COLOR_CLASS;
        protected Class<?> POTION_CLASS;

        @Override
        protected void init() throws Exception {
            POTION_CLASS = Class.forName("org.bukkit.inventory.meta.PotionMeta");
            POTION_META_CLASS = new ReflectClassBuilder("org.bukkit.inventory.meta.PotionMeta")
                    .method("hasCustomEffects").finished()
                    .method("getCustomEffects").finished() // List<PotionEffect>
                    .method("addCustomEffect").args(PotionEffect.class.getName(), "boolean")
                    .method("hasColor").finished()
                    .method("getColor").finished()
                    .method("setColor").args("org.bukkit.Color")
                    .toClass();

            COLOR_CLASS = new ReflectClassBuilder("org.bukkit.Color")
                    .method("fromRGB", "fromARGB").args("int")
                    .method("asRGB", "asARGB").finished()
                    .toClass();
        }

        @Override
        public void serialize(Handler handler, ConfigurationHashMap section, ItemStack itemStack, ItemMeta itemMeta) {
            try {
                POTION_CLASS.cast(itemMeta);
            } catch (Exception e) {
                return;
            }
            if ((boolean) POTION_META_CLASS.execute("hasCustomEffects", itemMeta)) {
                ConfigurationHashMap effects = section.createMap("effects");
                List<PotionEffect> list =  (List<PotionEffect>) POTION_META_CLASS.execute("getCustomEffects", itemMeta);
                int i = 1;
                for (PotionEffect effect : list) {
                    String key = String.format("effect-%d", i++);
                    ConfigurationHashMap potion = effects.createMap(key);
                    potion.put("type", effect.getType().getName());
                    potion.put("duration", effect.getDuration());
                    potion.put("amplifier", effect.getAmplifier());
                }
            }

            if ((boolean) POTION_META_CLASS.execute("hasColor", itemMeta)) {
                Object color = POTION_META_CLASS.execute("getColor", itemMeta);
                int rgb = (int) COLOR_CLASS.execute("asARGB", color);
                section.put("color", rgbToString(rgb));
            }
        }

        protected String rgbToString(int rgb) {
            return String.format("#%08X", rgb);
        }

        protected int stringToRgb(String string) {
            int ret;
            if (string.startsWith("#")) {
                ret = Integer.parseUnsignedInt(string.substring(1), 16);
            } else {
                ret = Integer.parseUnsignedInt(string, 16);
            }
            DebugLogger.debug("%s to rgb %d", string, ret);
            return ret & 0x0FFFFFF;
        }

        @Override
        public ItemStack deserialize(Handler handler, ConfigurationHashMap section, ItemStack itemStack, ItemMeta itemMeta) {
            try {
                POTION_CLASS.cast(itemMeta);
            } catch (Exception e) {
                return null;
            }
            if (section.isMap("effects")) {
                ConfigurationHashMap effects = section.getMap("effects");
                for (String key : effects.keySet()) {
                    ConfigurationHashMap potion = effects.getMap(key);
                    String type = potion.getString("type");
                    int duration = potion.getInt("duration");
                    int amplifier = potion.getInt("amplifier");
                    PotionEffect pe = new PotionEffect(PotionEffectType.getByName(type), duration, amplifier);
                    POTION_META_CLASS.execute("addCustomEffect", itemMeta, pe, false);
                }
            }
            if (section.contains("color")) {
                Object color = COLOR_CLASS.execute("fromARGB", null, stringToRgb(section.getString("color")));
                POTION_META_CLASS.execute("setColor", itemMeta, color);
            }
            return null;
        }

        @Override
        public int getPriority() {
            return 5;
        }
    }

    public static class Potion2Entry extends Potion1Entry {
        @Override
        public int getPriority() {
            return 1;
        }

        @Override
        protected void init() throws Exception {
            super.init();
            COLOR_CLASS = new ReflectClassBuilder("org.bukkit.Color")
                    .method("fromARGB", "fromARGB").args("int")
                    .method("asARGB", "asARGB").finished()
                    .toClass();
        }
    }
}
