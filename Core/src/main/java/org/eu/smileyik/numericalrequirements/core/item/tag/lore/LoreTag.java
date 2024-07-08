package org.eu.smileyik.numericalrequirements.core.item.tag.lore;

import org.bukkit.ChatColor;
import org.eu.smileyik.numericalrequirements.core.item.tag.ItemTag;

import java.util.List;

public abstract class LoreTag implements ItemTag<LoreValue> {
    private final LorePattern patternWithColor;
    private final LorePattern patternWithoutColor;

    public LoreTag() {
        patternWithColor = LorePattern.compile(getModeString());
        patternWithoutColor = LorePattern.compile(ChatColor.stripColor(getModeString()));
    }

    public LoreTag(String modeString) {
        patternWithColor = LorePattern.compile(modeString);
        patternWithoutColor = LorePattern.compile(ChatColor.stripColor(modeString));
    }

    /**
     * lore要去掉颜色.
     * @param lore
     * @return
     */
    public LoreValue getValue(String lore) {
        return patternWithoutColor.getValue(lore);
    }

    public boolean matches(String lore) {
        return patternWithoutColor.matches(ChatColor.stripColor(lore));
    }

    public boolean isValidValues(List<String> values) {
        return patternWithColor.isValidValue(values);
    }

    public String buildLore(List<String> values) {
        return patternWithColor.buildLoreByStringList(values);
    }

    public String buildLore(LoreValue value) {
        return patternWithColor.buildLore(value);
    }

    public LorePattern getPatternWithColor() {
        return patternWithColor;
    }

    public LorePattern getPatternWithoutColor() {
        return patternWithoutColor;
    }

    /**
     * 获取Lore模式匹配字符串。
     * @return
     */
    protected abstract String getModeString();
}
