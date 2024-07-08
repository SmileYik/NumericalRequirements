package org.eu.smileyik.numericalrequirements.core.item.tag.lore;

import org.eu.smileyik.numericalrequirements.core.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LorePattern {

    private static final Pattern TYPE_PATTERN = Pattern.compile("<%:.+?>");

    private final String patternString;
    private final String[] splitLore;
    private final Pattern[] patterns;
    private final List<ValueTranslator<?>> translators;

    protected LorePattern(String[] splitLore, List<ValueTranslator<?>> values) {
        this.splitLore = splitLore;
        this.translators = values;

        int size = values.size();
        String[] regexes = new String[splitLore.length];
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < splitLore.length; ++i) {
            regexes[i] = this.splitLore[i].replace("\\", "\\\\")
                    .replace("|", "\\|")
                    .replace("+", "\\+")
                    .replace("*", "\\*")
                    .replace("^", "\\^")
                    .replace("$", "\\$")
                    .replace("?", "\\?")
                    .replace("(", "\\(")
                    .replace(")", "\\)")
                    .replace(".", "\\.")
                    .replace("[", "\\[")
                    .replace("]", "\\]")
                    .replace("{", "\\{")
                    .replace("}", "\\}");
            sb.append(regexes[i]);
            if (i < size) {
                sb.append(values.get(i).getRegex());
            }
        }
        patternString = sb.toString();

        this.patterns = new Pattern[size];
        for (int i = 0; i < size; ++i) {
            ValueTranslator<?> loreTagType = values.get(i);
            String regex = String.format(
                    "(?<=%s)%s(?=%s)",
                    regexes[i], loreTagType.getRegex(), regexes[i + 1]
            );
            this.patterns[i] = Pattern.compile(regex);
        }
    }

    public boolean matches(String lore) {
        return lore != null && lore.matches(patternString);
    }

    public LoreValue getValue(String lore) {
        if (!matches(lore)) {
            return null;
        }
        List<Pair<ValueTranslator<?>, String>> list = new ArrayList<>();
        int size = translators.size();
        int idx = 0;
        for (int i = 0; i < size; ++i) {
            ValueTranslator<?> translator = translators.get(i);
            Matcher matcher = patterns[i].matcher(lore);
            if (matcher.find(idx)) {
                int start = matcher.start();
                int end   = matcher.end();
                list.add(Pair.newPair(translator, lore.substring(start, end)));
                idx = end;
            } else {
                return null;
            }
        }
        return new LoreValue(list);
    }

    public String buildLore(LoreValue value) {
        if (value.size() != splitLore.length - 1) return null;
        StringBuilder sb = new StringBuilder(splitLore[0]);
        for (int i = 1; i < splitLore.length; ++i) {
            sb.append(value.getString(i - 1)).append(splitLore[i]);
        }
        return sb.toString();
    }

    public String buildLoreByStringList(List<String> value) {
        if (value.size() != splitLore.length - 1) return null;
        StringBuilder sb = new StringBuilder(splitLore[0]);
        for (int i = 1; i < splitLore.length; ++i) {
            sb.append(translators.get(i - 1).format(value.get(i - 1))).append(splitLore[i]);
        }
        return sb.toString();
    }

    public boolean isValidValue(List<String> value) {
        int size = translators.size();
        if (value.size() != size) return false;
        for (int i = 0; i < size; ++i) {
            if (!translators.get(i).matches(value.get(i))) {
                return false;
            }
        }
        return true;
    }

    public List<ValueTranslator<?>> getTranslators() {
        return translators;
    }

    public int getValueSize() {
        return translators.size();
    }

    public static LorePattern compile(String modeString) {
        List<String> format = new ArrayList<>();
        List<ValueTranslator<?>> translators = new ArrayList<>();
        Matcher matcher = TYPE_PATTERN.matcher(modeString);
        int i = 0;
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            format.add(modeString.substring(i, start));
            translators.add(ValueTranslator.VALUE_TRANSLATOR_MAP.get(modeString.substring(start + 3, end - 1)));
            i = end;
        }
        format.add(modeString.substring(i));
        return new LorePattern(format.toArray(new String[0]), translators);
    }
}
