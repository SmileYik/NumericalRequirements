package org.eu.smileyik.numericalrequirements.core.item.tagold.service;

import org.eu.smileyik.numericalrequirements.core.item.tagold.type.LoreTagType;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoreTagPattern {
    private final String modeString;
    private final String patternString;
    private final String[] rawStrings;
    private final Pattern[] patterns;
    private final List<LoreTagType<?>> values;

    protected LoreTagPattern(String modeString, String[] rawStrings, List<LoreTagType<?>> values) {
        this.modeString = modeString;
        this.rawStrings = rawStrings;
        this.values = values;

        int size = values.size();
        String[] regexes = new String[rawStrings.length];
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rawStrings.length; ++i) {
            regexes[i] = this.rawStrings[i].replace("\\", "\\\\")
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
            LoreTagType<?> loreTagType = values.get(i);
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

    public LoreTagValue getValue(String lore) {
        if (!matches(lore)) {
            return null;
        }
        LoreTagValue value = new LoreTagValue();
        int size = values.size();
        int idx = 0;
        for (int i = 0; i < size; ++i) {
            LoreTagType<?> loreTagType = values.get(i);
            Matcher matcher = patterns[i].matcher(lore);
            if (matcher.find(idx)) {
                int start = matcher.start();
                int end   = matcher.end();
                value.add(new LoreTagTypeValue(loreTagType, lore.substring(start, end)));
                idx = end;
            } else {
                return null;
            }
        }
        return value;
    }

    public String getModeString() {
        return modeString;
    }

    public String buildLore(LoreTagValue value) {
        if (value.size() != rawStrings.length - 1) return null;
        StringBuilder sb = new StringBuilder(rawStrings[0]);
        for (int i = 1; i < rawStrings.length; ++i) {
            sb.append(value.get(i - 1).getValueString())
                    .append(rawStrings[i]);
        }
        return sb.toString();
    }

    public String buildLoreByStringList(List<String> value) {
        if (value.size() != rawStrings.length - 1) return null;
        StringBuilder sb = new StringBuilder(rawStrings[0]);
        for (int i = 1; i < rawStrings.length; ++i) {
            sb.append(values.get(i - 1).castString(value.get(i - 1)))
                    .append(rawStrings[i]);
        }
        return sb.toString();
    }

    public List<LoreTagType<?>> getValueTypes() {
        return values;
    }

    public int getValueSize() {
        return values.size();
    }

    @Override
    public String toString() {
        return "LoreTagPattern{" +
                "modeString='" + modeString + '\'' +
                ", patternString='" + patternString + '\'' +
                ", rawStrings=" + Arrays.toString(rawStrings) +
                ", patterns=" + Arrays.toString(patterns) +
                ", values=" + values +
                '}';
    }
}
