import org.bukkit.configuration.file.YamlConfiguration;
import org.eu.smileyik.numericalrequirements.core.api.util.YamlUtil;
import org.eu.smileyik.numericalrequirements.multiblockcraft.MultiBlockCraftExtension;
import org.junit.jupiter.api.Test;

public class Test1 {
    @Test
    public void a() {
        YamlConfiguration configuration = new YamlConfiguration();
        configuration.set("a", MultiBlockCraftExtension.class.getName());
        System.out.println(YamlUtil.saveToString(configuration));
    }
}
