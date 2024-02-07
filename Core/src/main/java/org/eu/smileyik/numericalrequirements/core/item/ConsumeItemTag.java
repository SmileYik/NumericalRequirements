package org.eu.smileyik.numericalrequirements.core.item;

import org.eu.smileyik.numericalrequirements.core.item.tag.service.LoreTagPattern;

public abstract class ConsumeItemTag extends ItemTag {

    protected ConsumeItemTag(String tagId, String name, String description, LoreTagPattern pattern) {
        super(tagId, name, description, pattern);
    }
}
