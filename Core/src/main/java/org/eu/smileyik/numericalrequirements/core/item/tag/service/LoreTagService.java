package org.eu.smileyik.numericalrequirements.core.item.tag.service;

import org.eu.smileyik.numericalrequirements.core.item.tag.type.LoreTagType;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface LoreTagService {

    static final Pattern TYPE_PATTERN = Pattern.compile("<%:.+?>");

    void registerLoreTagType(LoreTagType<?> type);

    LoreTagType<?> getLoreTagType(String typeName);

    default LoreTagPattern compile(String modeString) {
        List<String> format = new ArrayList<>();
        List<LoreTagType<?>> types = new ArrayList<>();
        Matcher matcher = TYPE_PATTERN.matcher(modeString);
        int i = 0;
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            format.add(modeString.substring(i, start));
            types.add(getLoreTagType(modeString.substring(start + 3, end - 1)));
            i = end;
        }
        format.add(modeString.substring(i));
        return new LoreTagPattern(modeString, format.toArray(new String[0]), types);
    }

    void shutdown();
}
