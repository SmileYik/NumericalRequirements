package org.eu.smileyik.numericalrequirements.core.element.formatter;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.eu.smileyik.numericalrequirements.core.api.NumericalRequirements;
import org.eu.smileyik.numericalrequirements.core.element.Element;
import org.eu.smileyik.numericalrequirements.core.element.ElementPlayer;
import org.eu.smileyik.numericalrequirements.core.element.data.ElementData;
import org.eu.smileyik.numericalrequirements.core.player.NumericalPlayer;
import org.eu.smileyik.numericalrequirements.core.util.Pair;
import org.eu.smileyik.numericalrequirements.debug.DebugLogger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// ${format:<%:str>;<%:str>}
public interface ElementFormatter <K extends Element, V extends ElementData> {
    Pattern PATTERN = Pattern.compile("(\\$\\{format:.+?;).+?}");

    Map<String, ElementFormatter<?, ?>> ELEMENT_FORMATTERS = new HashMap<>() {
        {
            add(new SimpleElementFormatter());
            add(new ProcessBarFormatter());
            add(new PercentageFormatter());
        }

        private void add(ElementFormatter<?, ?> formatter) {
            put(formatter.getId(), formatter);
        }
    };

    static void register(ElementFormatter<?, ?> formatter) {
        ELEMENT_FORMATTERS.put(formatter.getId(), formatter);
    }

    /**
     * 格式化元素至
     * @param element 元素
     * @param elementData 元素值
     * @return
     */
    String format(K element, V elementData);

    /**
     * 获取id
     * @return
     */
    String getId();

    /**
     * 配置
     * @param section 配置片段
     */
    void configure(ConfigurationSection section);

    static String replacePlaceholder(Player player, String text) {
        if (player == null) return text;
        return replacePlaceholder(NumericalRequirements.getInstance().getPlayerService().getNumericalPlayer(player), text);
    }

    static String replacePlaceholder(NumericalPlayer p, String text) {
        DebugLogger.debug("Replace placeholder: " + text);
        if (p == null) {
            DebugLogger.debug("not find player");
            return text;
        }
        StringBuilder sb = new StringBuilder();
        Matcher matcher = PATTERN.matcher(text);
        int idx = 0;
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            sb.append(text, idx, start);
            String[] strs = text.substring(start + 9, end - 1).split(";");
            if (strs.length != 2) {
                DebugLogger.debug("Failed find formatter and element id: %s", Arrays.toString(strs));
                continue;
            }
            DebugLogger.debug("formatter: %s; element: %s", strs[0], strs[1]);
            ElementFormatter<?, ?> elementFormatter = ELEMENT_FORMATTERS.get(strs[0]);
            if (elementFormatter == null) {
                DebugLogger.debug("Failed find formatter: %s", strs[0]);
                continue;
            }
            Pair<Element, ElementData> pair = ElementPlayer.getElementData(p, strs[1]);
            if (pair == null) {
                DebugLogger.debug("Failed find element: %s", strs[1]);
                continue;
            }
            sb.append(pair.getFirst().toString(elementFormatter, pair.getSecond()));
            idx = end;
        }
        sb.append(text.substring(idx));
        return sb.toString();
    }
}
