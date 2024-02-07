import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.file.YamlConstructor;
import org.bukkit.configuration.file.YamlRepresenter;
import org.bukkit.potion.PotionEffectType;
import org.eu.smileyik.numericalrequirements.core.util.YamlUtil;
import org.junit.Test;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

public class YamlConfigurationTest {
    @Test
    public void test1() throws InvalidConfigurationException {
        String line = "public static final PotionEffectType SPEED = new PotionEffectTypeWrapper(1);\n" +
                "\n" +
                "    /**\n" +
                "     * Decreases movement speed.\n" +
                "     */\n" +
                "    public static final PotionEffectType SLOW = new PotionEffectTypeWrapper(2);\n" +
                "\n" +
                "    /**\n" +
                "     * Increases dig speed.\n" +
                "     */\n" +
                "    public static final PotionEffectType FAST_DIGGING = new PotionEffectTypeWrapper(3);\n" +
                "\n" +
                "    /**\n" +
                "     * Decreases dig speed.\n" +
                "     */\n" +
                "    public static final PotionEffectType SLOW_DIGGING = new PotionEffectTypeWrapper(4);\n" +
                "\n" +
                "    /**\n" +
                "     * Increases damage dealt.\n" +
                "     */\n" +
                "    public static final PotionEffectType INCREASE_DAMAGE = new PotionEffectTypeWrapper(5);\n" +
                "\n" +
                "    /**\n" +
                "     * Heals an entity.\n" +
                "     */\n" +
                "    public static final PotionEffectType HEAL = new PotionEffectTypeWrapper(6);\n" +
                "\n" +
                "    /**\n" +
                "     * Hurts an entity.\n" +
                "     */\n" +
                "    public static final PotionEffectType HARM = new PotionEffectTypeWrapper(7);\n" +
                "\n" +
                "    /**\n" +
                "     * Increases jump height.\n" +
                "     */\n" +
                "    public static final PotionEffectType JUMP = new PotionEffectTypeWrapper(8);\n" +
                "\n" +
                "    /**\n" +
                "     * Warps vision on the client.\n" +
                "     */\n" +
                "    public static final PotionEffectType CONFUSION = new PotionEffectTypeWrapper(9);\n" +
                "\n" +
                "    /**\n" +
                "     * Regenerates health.\n" +
                "     */\n" +
                "    public static final PotionEffectType REGENERATION = new PotionEffectTypeWrapper(10);\n" +
                "\n" +
                "    /**\n" +
                "     * Decreases damage dealt to an entity.\n" +
                "     */\n" +
                "    public static final PotionEffectType DAMAGE_RESISTANCE = new PotionEffectTypeWrapper(11);\n" +
                "\n" +
                "    /**\n" +
                "     * Stops fire damage.\n" +
                "     */\n" +
                "    public static final PotionEffectType FIRE_RESISTANCE = new PotionEffectTypeWrapper(12);\n" +
                "\n" +
                "    /**\n" +
                "     * Allows breathing underwater.\n" +
                "     */\n" +
                "    public static final PotionEffectType WATER_BREATHING = new PotionEffectTypeWrapper(13);\n" +
                "\n" +
                "    /**\n" +
                "     * Grants invisibility.\n" +
                "     */\n" +
                "    public static final PotionEffectType INVISIBILITY = new PotionEffectTypeWrapper(14);\n" +
                "\n" +
                "    /**\n" +
                "     * Blinds an entity.\n" +
                "     */\n" +
                "    public static final PotionEffectType BLINDNESS = new PotionEffectTypeWrapper(15);\n" +
                "\n" +
                "    /**\n" +
                "     * Allows an entity to see in the dark.\n" +
                "     */\n" +
                "    public static final PotionEffectType NIGHT_VISION = new PotionEffectTypeWrapper(16);\n" +
                "\n" +
                "    /**\n" +
                "     * Increases hunger.\n" +
                "     */\n" +
                "    public static final PotionEffectType HUNGER = new PotionEffectTypeWrapper(17);\n" +
                "\n" +
                "    /**\n" +
                "     * Decreases damage dealt by an entity.\n" +
                "     */\n" +
                "    public static final PotionEffectType WEAKNESS = new PotionEffectTypeWrapper(18);\n" +
                "\n" +
                "    /**\n" +
                "     * Deals damage to an entity over time.\n" +
                "     */\n" +
                "    public static final PotionEffectType POISON = new PotionEffectTypeWrapper(19);\n" +
                "\n" +
                "    /**\n" +
                "     * Deals damage to an entity over time and gives the health to the\n" +
                "     * shooter.\n" +
                "     */\n" +
                "    public static final PotionEffectType WITHER = new PotionEffectTypeWrapper(20);\n" +
                "\n" +
                "    /**\n" +
                "     * Increases the maximum health of an entity.\n" +
                "     */\n" +
                "    public static final PotionEffectType HEALTH_BOOST = new PotionEffectTypeWrapper(21);\n" +
                "\n" +
                "    /**\n" +
                "     * Increases the maximum health of an entity with health that cannot be\n" +
                "     * regenerated, but is refilled every 30 seconds.\n" +
                "     */\n" +
                "    public static final PotionEffectType ABSORPTION = new PotionEffectTypeWrapper(22);\n" +
                "\n" +
                "    /**\n" +
                "     * Increases the food level of an entity each tick.\n" +
                "     */\n" +
                "    public static final PotionEffectType SATURATION = new PotionEffectTypeWrapper(23);\n" +
                "\n" +
                "    /**\n" +
                "     * Outlines the entity so that it can be seen from afar.\n" +
                "     */\n" +
                "    public static final PotionEffectType GLOWING = new PotionEffectTypeWrapper(24);\n" +
                "\n" +
                "    /**\n" +
                "     * Causes the entity to float into the air.\n" +
                "     */\n" +
                "    public static final PotionEffectType LEVITATION = new PotionEffectTypeWrapper(25);\n" +
                "\n" +
                "    /**\n" +
                "     * Loot table luck.\n" +
                "     */\n" +
                "    public static final PotionEffectType LUCK = new PotionEffectTypeWrapper(26);\n" +
                "\n" +
                "    /**\n" +
                "     * Loot table unluck.\n" +
                "     */\n" +
                "    public static final PotionEffectType UNLUCK = new PotionEffectTypeWrapper(27);";
        for (String l : line.split("\n")) {
            l = l.trim();
            if (l.startsWith("public static final PotionEffectType ")) {
                String s = l.substring("public static final PotionEffectType ".length()).split(" ")[0];
                System.out.println("nr effect add SmileYik PotionEffect " + s + " 100000 1");
            }
        }
        for (PotionEffectType value : PotionEffectType.values()) {
            System.out.println("nr effect add SmileYik PotionEffect " + value + " 100000 1");
        }
        YamlConfiguration configuration = new YamlConfiguration();
        configuration.loadFromString("abc: \n  bcd: 1");
        System.out.println(configuration.isConfigurationSection("avc"));
        // System.out.println(configuration.saveToString());
        System.out.println(configuration.getConfigurationSection("abcd"));
        String s = configuration.saveToString();
        System.out.println(s);
        ConfigurationSection b = configuration.createSection("b");
        b.set("c", s);
        System.out.println("-------" + b.getString("c"));
        System.out.println(YamlUtil.saveToString(configuration));
        System.out.println(YamlUtil.saveToString(YamlUtil.loadFromString("#aaaaaaaaa\n{}")));
    }
}
